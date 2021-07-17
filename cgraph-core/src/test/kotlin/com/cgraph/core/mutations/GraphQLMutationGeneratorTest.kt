package com.cgraph.core.mutations

import com.cgraph.core.states.GraphableState
import com.cgraph.core.support.MapOfMaps
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import org.junit.Test
import java.util.*

data class DummyCurrencyState(val id: UUID,
                              val name: String,
                              val isoCode: String, override val linearId: UniqueIdentifier
) : GraphableState {

    /** The public keys of the involved parties. */
    override val participants: List<AbstractParty> get() = listOf()

    override fun buildEntityMap(): MapOfMaps {
        return mapOf(
            "entityType" to "Currency",
            "id" to id,
            "name" to name,
            "isoCode" to isoCode
        )
    }
}

data class DummyUUIDState(val id: UUID,
                          val entity1: UUID,
                          val entity2: UUID, override val linearId: UniqueIdentifier
) : GraphableState {

    /** The public keys of the involved parties. */
    override val participants: List<AbstractParty> get() = listOf()

    override fun buildEntityMap(): MapOfMaps {
        return mapOf(
            "entityType" to "DummyUUID",
            "id" to id,
            "entity1" to entity1,
            "isoCentity2ode" to entity2
        )
    }
}

class GraphQLMutationGeneratorTest {

    @Test
    fun `generate graphql mutation from graphable state`() {
        val graphableState = DummyCurrencyState(
            id = UUID.randomUUID(),
            name = "Euro",
            isoCode = "EUR",
            linearId = UniqueIdentifier(id = UUID.randomUUID())
        )
        val mutation = GraphQLMutationGenerator().processStates(
            graphableState.buildEntityMap(),
            TransactionType.ISSUANCE
        )
        print("Mutation: $mutation")
    }

    @Test
    fun `generate graphql mutation with from graphable state with UUID relationships`() {
        val graphableState = DummyUUIDState(
            id = UUID.randomUUID(),
            entity1 = UUID.randomUUID(),
            entity2 = UUID.randomUUID(),
            linearId = UniqueIdentifier(id = UUID.randomUUID())
        )
        val mutation = GraphQLMutationGenerator().processStates(
            graphableState.buildEntityMap(),
            TransactionType.ISSUANCE
        )
        print("Mutation: $mutation")
    }
}