package com.cgraph.example.states

import com.cgraph.core.states.GraphableState
import com.cgraph.core.support.MapOfMaps
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import java.util.*

/*
    type Balance {
      id: String! @id
      holder: Member,
      value: String
      currency: Currency
    }
 */
@BelongsToContract(BalanceContract::class)
data class BalanceState(val value: Int,
                        val currency: UUID,
                        val holder: Party,
                        val holderMemberId: UUID,
                        override val linearId: UniqueIdentifier
) : GraphableState {

    /* The public keys of the involved parties. */
    override val participants: List<AbstractParty> get() = listOf(holder)

    override fun buildEntityMap(): MapOfMaps {
        return mapOf(
            "entityType" to "Balance",
            "id" to linearId.id.toString(),
            "value" to value,
            "currency" to currency,
            "holder" to holderMemberId
        )
    }
}