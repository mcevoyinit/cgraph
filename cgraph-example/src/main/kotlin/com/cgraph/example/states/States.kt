package com.cgraph.example.states

import com.cgraph.core.services.DGraphInstanceMethod
import com.cgraph.core.services.DGraphServiceFactory

/**
 * A graph enhanced IOU state object recording IOU agreements between two parties.
 *
 * A state must implement [ContractState] or one of its descendants.
 * This state also implements [GraphableState] and its member function
 * which defines its graph schema equivalent
 *
 * @param value the value of the IOU.
 * @param lender the party issuing the IOU.
 * @param lenderMemberId the member id of party issuing the IOU.
 * @param borrower the party receiving and approving the IOU.
 * @param borrowerMemberId the member id of party  eceiving and approving the IOU.
 */
/*@BelongsToContract(IOUContract::class)
data class IOUState(val value: Int,
                    val currency: UUID,
                    val lender: Party,
                    val lenderMemberIxd: UUID,
                    val borrower: Party,
                    val borrowerMemberId: UUID,
                    override val linearId: UniqueIdentifier = UniqueIdentifier()):
    LinearState, GraphableState {

    *//** The public keys of the involved parties. *//*
    override val participants: List<AbstractParty> get() = listOf(lender, borrower)

    override fun buildEntityMap(): MapOfMaps {
        return mapOf(
            "entityType" to "IOU",
            "value"  to value,
            "currency" to currency,
            "lender" to lender,
            "borrower" to borrower
        )
    }
}

@BelongsToContract(MemberContract::class)
data class MemberState(val id: UUID,
                       val party: Party,
                       val borrowed: List<UUID>,
                       val lended: List<UUID>,
                       val balances: List<UUID>,
                    override val linearId: UniqueIdentifier = UniqueIdentifier()):
    LinearState, GraphableState {

    *//** The public keys of the involved parties. *//*
    override val participants: List<AbstractParty> get() = listOf(party)

    override fun buildEntityMap(): MapOfMaps {
        return mapOf(
            "entityType" to "Member",
            "cordaNodeName" to party.name.toString(),
            "borrowed" to borrowed,
            "lended" to lended,
            "balances" to balances
        )
    }
}*/

/*
@BelongsToContract(CurrencyContract::class)
data class CurrencyState(val id: UUID,
                         val name: String,
                         val isoCode: String,
                         val issuer: Party) : GraphableState {

    *//** The public keys of the involved parties. *//*
    override val participants: List<AbstractParty> get() = listOf(issuer)

    override fun buildEntityMap(): MapOfMaps {
        return mapOf(
            "entityType" to "Currency",
            "id" to id.toString(),
            "name" to name,
            "isoCode" to isoCode
        )
    }
}*/
/*
@BelongsToContract(MemberContract::class)
data class BalanceState(val id: UUID,
                        val party: Party,
                        val borrowed: List<UUID>,
                        val lended: List<UUID>,
                        val balances: List<UUID>,
                        override val linearId: UniqueIdentifier = UniqueIdentifier()):
    LinearState, GraphableState {

    *//** The public keys of the involved parties. *//*
    override val participants: List<AbstractParty> get() = listOf(party)

    override fun buildEntityMap(): MapOfMaps {
        return mapOf(
            "entityType" to "Member",
            "cordaNodeName" to party.name.toString(),
            "borrowed" to borrowed,
            "lended" to lended,
            "balances" to balances
        )
    }
}*/

fun main(args: Array<String>) {
    print("Start DGraph")
    val dgraph = DGraphServiceFactory().start(8080, DGraphInstanceMethod.GOLANG)

/*    val graphableState =  IOUState(
        value = 5,
        currency = UUID.randomUUID(),
        lender = Party(
            certificate = X509Certificate(),

        )
    )
    val gson = Gson()
    val check = gson.toJson()*/
    print("DGraph started")
}

