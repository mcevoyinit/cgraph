package com.cgraph.example.flows

import co.paralleluniverse.fibers.Suspendable
import com.cgraph.core.support.graphService
import com.cgraph.core.support.graphableString
import com.cgraph.core.support.uuid
import com.cgraph.example.sample.memberIdForX500Name
import com.cgraph.example.sample.queryBalanceIdForCurrencyId
import com.cgraph.example.sample.queryCurrencyIdByName
import com.cgraph.example.states.BalanceContract
import com.cgraph.example.states.BalanceState
import com.cgraph.example.states.IOUContract
import com.cgraph.example.states.IOUState
import net.corda.core.contracts.Command
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.node.services.Vault
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.unwrap
import java.util.*

@InitiatingFlow
@StartableByService
@StartableByRPC
class UpdateBalanceFlow(val balanceId: UUID,
                        val increment: Int
) : FlowLogic<SignedTransaction>() {
    /**
     * The progress tracker checkpoints each stage of the flow and outputs the specified messages when each
     * checkpoint is reached in the code. See the 'progressTracker.currentStep' expressions within the call() function.
     */
    companion object {
        fun tracker() = ProgressTracker()
    }

    override val progressTracker = tracker()

    @Suspendable
    override fun call(): SignedTransaction {
        // Obtain a reference to the notary we want to use.
        val notary = serviceHub.networkMapCache.notaryIdentities[0]

        val balanceQueryCriteria = QueryCriteria.LinearStateQueryCriteria(
            null,
            listOf(UniqueIdentifier(id = balanceId)),
            Vault.StateStatus.UNCONSUMED, null)

        val inputBalanceState = serviceHub.vaultService.queryBy<BalanceState>(balanceQueryCriteria).states.singleOrNull() ?: error("Balance state not present id $balanceId")
        val balanceValue = inputBalanceState.state.data.value
        val outputBalanceState = inputBalanceState.state.data.copy(value = (balanceValue + increment))

        val balanceCommand = Command(BalanceContract.Commands.Create(), outputBalanceState.participants.map { it.owningKey })

        val txBuilder = TransactionBuilder(notary)
            .addInputState(inputBalanceState)
            .addOutputState(outputBalanceState, IOUContract.ID)
            .addCommand(balanceCommand)

        // Verify that the transaction is valid.
        txBuilder.verify(serviceHub)
        // Sign the transaction.
        val partSignedTx = serviceHub.signInitialTransaction(txBuilder)
        // Notarise and record the transaction in both parties' vaults.
        return subFlow(FinalityFlow(partSignedTx, emptyList()))
    }
}
@Suppress("unused")
@InitiatedBy(UpdateBalanceFlow::class)
class UpdateBalanceResponderFlow(private val otherSession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        subFlow(ReceiveFinalityFlow(otherSideSession = otherSession))
    }
}
