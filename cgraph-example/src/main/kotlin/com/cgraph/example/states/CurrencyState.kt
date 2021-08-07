package com.cgraph.example.states

import com.cgraph.contracts.states.GraphableState
import com.cgraph.contracts.states.MapOfMaps
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party

@BelongsToContract(CurrencyContract::class)
data class CurrencyState(val name: String,
                        val isoCode: String,
                        val issuer: Party,
                        override val linearId: UniqueIdentifier
) : GraphableState {

    /** The public keys of the involved parties. */
    override val participants: List<AbstractParty> get() = listOf(issuer)

    override fun buildEntityMap(): MapOfMaps {
        return mapOf(
            "entityType" to "Currency",
            "id" to linearId.id.toString(),
            "name" to name,
            "isoCode" to isoCode
        )
    }
}


