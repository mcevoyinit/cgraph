package com.cgraph.example.flows

import co.paralleluniverse.fibers.Suspendable
import com.cgraph.example.states.BalanceContract
import com.cgraph.example.states.BalanceState
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.*
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import java.util.*

@InitiatingFlow
@StartableByService
@StartableByRPC
class UpdateBalanceFlow(
    private val balanceId: UUID,
    private val increment: Int
) : FlowLogic<SignedTransaction>() {

    companion object {
        fun tracker() = ProgressTracker()
    }

    override val progressTracker = tracker()

    @Suspendable
    override fun call(): SignedTransaction {
        val notary = serviceHub.networkMapCache.notaryIdentities[0]

        val balanceQueryCriteria = QueryCriteria.LinearStateQueryCriteria(
            linearId = listOf(UniqueIdentifier(id = balanceId)))

        val inputBalanceState = serviceHub.vaultService.queryBy<BalanceState>(balanceQueryCriteria).states.singleOrNull() ?: error("Balance state not present id $balanceId")
        val balanceValue = inputBalanceState.state.data.value
        val outputBalanceState = inputBalanceState.state.data.copy(value = (balanceValue + increment))

        val balanceCommand = Command(BalanceContract.Commands.Update(), outputBalanceState.participants.map { it.owningKey })

        val txBuilder = TransactionBuilder(notary)
            .addInputState(inputBalanceState)
            .addOutputState(outputBalanceState)
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
class UpdateBalanceResponderFlow(private val otherSession: FlowSession) : FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call() : SignedTransaction {
        return subFlow(ReceiveFinalityFlow(otherSideSession = otherSession))
    }
}
