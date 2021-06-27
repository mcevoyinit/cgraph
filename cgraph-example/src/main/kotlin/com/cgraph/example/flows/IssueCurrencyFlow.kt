package com.cgraph.example.flows

import co.paralleluniverse.fibers.Suspendable
import com.cgraph.example.states.CurrencyContract
import com.cgraph.example.states.CurrencyState
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.node.StatesToRecord
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import java.util.*

@InitiatingFlow
@StartableByService
class IssueCurrencyFlow(val name: String,
                        val isoCode: String,
                        val observers: List<Party>
) : FlowLogic<SignedTransaction>() {

    companion object {
        fun tracker() = ProgressTracker()
    }

    override val progressTracker = tracker()

    @Suspendable
    override fun call(): SignedTransaction {
        val notary = serviceHub.networkMapCache.notaryIdentities.single() // METHOD 1

        val currencyState = CurrencyState(
            linearId = UniqueIdentifier(id = UUID.randomUUID()),
            issuer = serviceHub.myInfo.legalIdentities.first(),
            name = name,
            isoCode = isoCode
        )
        val txCommand = Command(CurrencyContract.Commands.Create(), currencyState.participants.map { it.owningKey })
        val txBuilder = TransactionBuilder(notary)
            .addOutputState(currencyState, CurrencyContract.ID)
            .addCommand(txCommand)

        // Verify that the transaction is valid.
        txBuilder.verify(serviceHub)

        // Sign the transaction.
        val partSignedTx = serviceHub.signInitialTransaction(txBuilder)
        // Notarise and record the transaction in all relevant parties' vaults.
        val observers = observers.map { initiateFlow(it) }
        return subFlow(FinalityFlow(partSignedTx, observers))
    }
}

@Suppress("unused")
@InitiatedBy(IssueCurrencyFlow::class)
class IssueCurrencyResponderFlow(private val otherSession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        subFlow(ReceiveFinalityFlow(otherSideSession = otherSession, statesToRecord = StatesToRecord.ALL_VISIBLE))
    }
}