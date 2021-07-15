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
        
        val lenderBalanceId = graphService.queryBalanceIdForCurrencyId(currencyId.toString())?.uuid() ?: error("Balance not present for currency $currencyId")
        val lenderQueryCriteria = QueryCriteria.LinearStateQueryCriteria(
            null,
            listOf(UniqueIdentifier(id = lenderBalanceId)),
            Vault.StateStatus.UNCONSUMED, null)

        val lenderInputBalanceState = serviceHub.vaultService.queryBy<BalanceState>(lenderQueryCriteria).states.singleOrNull() ?: error("Balance state not present for $currencyId")
        val lenderBalanceValue = lenderInputBalanceState.state.data.value
        val lenderOutputBalanceState = lenderInputBalanceState.state.data.copy(value = (lenderBalanceValue - iouState.value))

       // val borrowerFlowSession = initiateFlow(borrower)
        // borrowerFlowSession.send(currencyId.toString())
       // val borrowerInputBalanceState = subFlow(ReceiveStateAndRefFlow<BalanceState>(borrowerFlowSession)).first()

       // val borrowerBalanceValue = borrowerInputBalanceState.state.data.value
       // val borrowerOutputBalanceState = borrowerInputBalanceState.state.data.copy(value = borrowerBalanceValue + iouState.value)

        val iouCommand = Command(IOUContract.Commands.Create(), iouState.participants.map { it.owningKey })
        val lenderBalanceCommand = Command(BalanceContract.Commands.Create(), lenderOutputBalanceState.participants.map { it.owningKey })
        //val borrowerBalanceCommand = Command(BalanceContract.Commands.Create(), borrowerOutputBalanceState.participants.map { it.owningKey })

        val txBuilder = TransactionBuilder(notary)
        //    .addInputState(lenderInputBalanceState)
        //    .addInputState(borrowerInputBalanceState)
         //   .addOutputState(lenderOutputBalanceState, BalanceContract.ID)
        //    .addOutputState(borrowerOutputBalanceState, BalanceContract.ID)
            .addOutputState(iouState, IOUContract.ID)
       //     .addCommand(BalanceContract.Commands.Create(), lenderOutputBalanceState.participants.map { it.owningKey })
            //.addCommand(borrowerBalanceCommand)
            .addCommand(iouCommand)

        // Verify that the transaction is valid.
        txBuilder.verify(serviceHub)

        // Sign the transaction.
        val partSignedTx = serviceHub.signInitialTransaction(txBuilder)

        // Send the state to the counterparty, and receive it back with their signature.
        //
        //val fullySignedTx = subFlow(CollectSignaturesFlow(partSignedTx, setOf(borrowerFlowSession)))

        // Notarise and record the transaction in both parties' vaults.
        return subFlow(FinalityFlow(partSignedTx, emptyList())) //setOf(borrowerFlowSession)))
    }
}

@InitiatedBy(IssueIOUFlow::class)
class Acceptor(val borrowerSession: FlowSession) : FlowLogic<SignedTransaction>() {

        @Suspendable
        override fun call(): SignedTransaction {
  /*          val currencyId = borrowerSession.receive<String>()
                .unwrap {
                    it
                }
            val graphService = serviceHub.graphService()
            val balanceId = graphService.queryBalanceIdForCurrencyId(currencyId)?.uuid() ?: error("Balance not present for currency $currencyId")
            val queryCriteria = QueryCriteria.LinearStateQueryCriteria(
                null,
                listOf(UniqueIdentifier(id = balanceId)),
                Vault.StateStatus.UNCONSUMED, null)

            val borrowerBalanceState = serviceHub.vaultService.queryBy<BalanceState>(queryCriteria).states.singleOrNull() ?: error("Balance state not present for $currencyId")

            subFlow(SendStateAndRefFlow(borrowerSession, listOf(borrowerBalanceState)))

            val signTransactionFlow = object : SignTransactionFlow(borrowerSession) {
                override fun checkTransaction(stx: SignedTransaction) = requireThat {
                    "This must be an IOU transaction." using (true)
                }
            }
            val txId = subFlow(signTransactionFlow).id*/

            return subFlow(ReceiveFinalityFlow(borrowerSession))//, expectedTxId = txId))
        }
    }