package com.cgraph.example.flows

import co.paralleluniverse.fibers.Suspendable
import com.cgraph.contract.graphableString
import com.cgraph.contract.uuid
import com.cgraph.core.services.graphService
import com.cgraph.example.sample.memberIdForX500Name
import com.cgraph.example.sample.queryCurrencyIdByName
import com.cgraph.example.states.IOUContract
import com.cgraph.example.states.IOUState
import net.corda.core.contracts.Command
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import java.util.*

/**
 * This flow allows two parties (the [Initiator] and the [Acceptor]) to come to an agreement about the IOU encapsulated
 * within an [IOUState].
 *
 * In our simple example, the [Acceptor] always accepts a valid IOU.
 *
 * These flows have deliberately been implemented by using only the call() method for ease of understanding. In
 * practice we would recommend splitting up the various stages of the flow into sub-routines.
 *
 * All methods called within the [FlowLogic] sub-class need to be annotated with the @Suspendable annotation.
 */
@InitiatingFlow
@StartableByService
@StartableByRPC
class IssueIOUFlow(val iouValue: Int,
                   val currencyName: String,
                   val borrower: Party
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
        val graphService = serviceHub.graphService()

        //Fetch what we need from the graph
        val lenderMemberId = graphService.memberIdForX500Name(serviceHub.myInfo.legalIdentities.first().name.graphableString())
            ?.uuid() ?: throw IllegalStateException("No member found for lender party")
        val borrowerMemberId = graphService.memberIdForX500Name(borrower.name.graphableString())
            ?.uuid() ?: throw IllegalStateException("No member found for borrower party")
        val currencyId = graphService.queryCurrencyIdByName(currencyName)?.uuid() ?: throw IllegalStateException("No currency found for party")

        // Generate an unsigned transaction.
        val iouState = IOUState(
            linearId = UniqueIdentifier(id = UUID.randomUUID()),
            value = iouValue,
            currency = currencyId,
            lender = serviceHub.myInfo.legalIdentities.first(),
            lenderMemberId = lenderMemberId,
            borrower = borrower,
            borrowerMemberId = borrowerMemberId
        )

        val iouCommand = Command(IOUContract.Commands.Create(), iouState.participants.map { it.owningKey })

        val txBuilder = TransactionBuilder(notary)
            .addOutputState(iouState, IOUContract.ID)
            .addCommand(iouCommand)

        // Verify that the transaction is valid.
        txBuilder.verify(serviceHub)

        // Sign the transaction.
        val partSignedTx = serviceHub.signInitialTransaction(txBuilder)

        // Send the state to the counterparty, and receive it back with their signature.
        val borrowerFlowSession = initiateFlow(borrower)
        val fullySignedTx = subFlow(CollectSignaturesFlow(partSignedTx, setOf(borrowerFlowSession)))

        // Notarise and record the transaction in both parties' vaults.
        return subFlow(FinalityFlow(fullySignedTx, listOf(borrowerFlowSession)))
    }
}

@InitiatedBy(IssueIOUFlow::class)
class Acceptor(val borrowerSession: FlowSession) : FlowLogic<SignedTransaction>() {

        @Suspendable
        override fun call(): SignedTransaction {

            val signTransactionFlow = object : SignTransactionFlow(borrowerSession) {
                override fun checkTransaction(stx: SignedTransaction) = requireThat {
                    "This must be true" using (true)
                }
            }

            val tx = subFlow(signTransactionFlow)
            return subFlow(ReceiveFinalityFlow(borrowerSession, expectedTxId = tx.id))
        }
    }