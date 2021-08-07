package com.cgraph.example.flows

import co.paralleluniverse.fibers.Suspendable
import com.cgraph.contracts.states.graphableString
import com.cgraph.contracts.states.uuid
import com.cgraph.core.services.graphService
import com.cgraph.example.sample.memberIdForX500Name
import com.cgraph.example.sample.queryCurrencyIdByIsoCode
import com.cgraph.example.states.BalanceContract
import com.cgraph.example.states.BalanceState
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.*
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import java.util.*

@InitiatingFlow
@StartableByService
@StartableByRPC
class IssueBalanceFlow(val currencyIsoCode: String, val value: Int) : FlowLogic<SignedTransaction>() {

    companion object {
        fun tracker() = ProgressTracker()
    }

    override val progressTracker = tracker()

    @Suspendable
    override fun call(): SignedTransaction {
        val notary = serviceHub.networkMapCache.notaryIdentities.single()
        val graphService = serviceHub.graphService()
        val currencyId = graphService.queryCurrencyIdByIsoCode(currencyIsoCode) ?: error("Currency $currencyIsoCode not present")
        val cordaNodeName = serviceHub.myInfo.legalIdentities.first().name.graphableString()
        val memberId = graphService.memberIdForX500Name(nodeName = cordaNodeName) ?: error("Member not present for $cordaNodeName")

        val balanceState = BalanceState(
            linearId = UniqueIdentifier(id = UUID.randomUUID()),
            holder = serviceHub.myInfo.legalIdentities.first(),
            value = value,
            currency = currencyId.uuid(),
            holderMemberId = memberId.uuid()
        )
        val txCommand = Command(BalanceContract.Commands.Create(), balanceState.participants.map { it.owningKey })
        val txBuilder = TransactionBuilder(notary)
            .addOutputState(balanceState)
            .addCommand(txCommand)

        // Verify that the transaction is valid.
        txBuilder.verify(serviceHub)
        // Sign the transaction.
        val partSignedTx = serviceHub.signInitialTransaction(txBuilder)
        // Notarise and record the transaction in both parties' vaults.
        return subFlow(FinalityFlow(partSignedTx, emptyList()))
    }
}

@Suppress("unused")
@InitiatedBy(IssueBalanceFlow::class)
class IssueBalanceResponderFlow(private val otherSession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        subFlow(ReceiveFinalityFlow(otherSideSession = otherSession))
    }
}


