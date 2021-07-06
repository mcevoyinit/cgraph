package com.cgraph.core.states

import com.cgraph.core.support.MapOfMaps
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.transactions.LedgerTransaction

/**
 * Dummy Contract is needed so that [GraphableState] is added to the node attachment storage
 * and is thus reachable by dependent CorDapps e.g [cgraph-example]
 */
@BelongsToContract(DummyContract::class)
data class DummyState(override val linearId: UniqueIdentifier) : GraphableState {
    override fun buildEntityMap(): MapOfMaps { return emptyMap() }
    override val participants: List<AbstractParty> = emptyList()
}

class DummyContract : Contract {
    companion object { @JvmStatic val ID = "com.cgraph.core.states.DummyContract" }
    override fun verify(tx: LedgerTransaction) {}
    interface Commands : CommandData { class Dummy : Commands }
}