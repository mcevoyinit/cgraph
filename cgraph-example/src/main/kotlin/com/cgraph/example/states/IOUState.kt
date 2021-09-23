package com.cgraph.example.states

import com.cgraph.contract.GraphableState
import com.cgraph.contract.MapOfMaps
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import java.util.*

/**
 * A graph enhanced IOU state object recording IOU agreements between two parties.
 *
 * A state must implement [ContractState] or one of its descendants.
 * This state also implements [GraphableState] and its member function
 * which defines its graph schema entity equivalent
 *
 * @param value the value of the IOU.
 * @param lender the party issuing the IOU.
 * @param lenderMemberId the member id of party issuing the IOU.
 * @param borrower the party receiving and approving the IOU.
 * @param borrowerMemberId the member id of party  receiving and approving the IOU.
 */

@BelongsToContract(IOUContract::class)
data class IOUState(val value: Int,
                    val currency: UUID,
                    val lender: Party,
                    val lenderMemberId: UUID,
                    val borrower: Party,
                    val borrowerMemberId: UUID,
                    override val linearId: UniqueIdentifier
) : GraphableState {

    override val participants: List<AbstractParty> get() = listOf(lender, borrower)

    override fun buildEntityMap(): MapOfMaps {
        return mapOf(
            "entityType" to "IOU",
            "id" to linearId.id.toString(),
            "value"  to value,
            "currency" to currency,
            "lender" to lenderMemberId,
            "borrower" to borrowerMemberId
        )
    }
}