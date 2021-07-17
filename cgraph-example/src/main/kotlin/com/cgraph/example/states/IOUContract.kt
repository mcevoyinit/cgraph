package com.cgraph.example.states

import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction

/**
 * A implementation of a basic smart contract in Corda.
 *
 * This contract enforces rules regarding the creation of a valid [IOUState], which in turn encapsulates an [IOUState].
 *
 * For a new [IOUState] to be issued onto the ledger, a transaction is required which takes:
 * - Zero input states.
 * - One output state: the new [IOUState].
 * - An Create() command with the public keys of both the lender and the borrower.
 *
 * All contracts must sub-class the [Contract] interface.
 */
class IOUContract : Contract {
    companion object {
        @JvmStatic
        val ID = "com.cgraph.example.states.IOUContract"
    }

    /**
     * The verify() function of all the states' contracts must not throw an exception for a transaction to be
     * considered valid.
     */
    override fun verify(tx: LedgerTransaction) {
        requireThat {
            // Generic constraints around the IOU transaction.
            //"No inputs should be consumed when issuing an IOU." using (tx.inputs.isEmpty())
            //"Only one output state should be created." using (tx.outputs.size == 1)
            val iou = tx.outputsOfType<IOUState>().first()
            "The lender and the borrower cannot be the same entity." using (iou.lender != iou.borrower)
            // IOU-specific constraints.
            "The IOU's value must be non-negative." using (iou.value > 0)

        }
    }

    /**
     * This contract only implements one command, Create.
     */
    interface Commands : CommandData {
        class Create : Commands
    }
}
