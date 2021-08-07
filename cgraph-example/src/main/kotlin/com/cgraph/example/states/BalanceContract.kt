package com.cgraph.example.states

import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.transactions.LedgerTransaction

class BalanceContract : Contract {
    companion object {
        @JvmStatic
        val ID = "com.cgraph.example.states.BalanceContract"
    }

    /**
     * The verify() function of all the states' contracts must not throw an exception for a transaction to be
     * considered valid.
     */
    override fun verify(tx: LedgerTransaction) {

    }

    /**
     * This contract only implements one command, Create.
     */
    interface Commands : CommandData {
        class Create : Commands
        class Update : Commands
    }
}