package com.cgraph.example.mutations

import com.cgraph.contract.GraphableState
import com.cgraph.contract.MapOfMaps
import com.cgraph.core.mutations.GraphQLMutationGenerator
import com.cgraph.core.mutations.TransactionType
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import org.junit.Test
import java.util.*

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
            stringValue = "Graphed",
            intValue = 1000000,
            linearId = UniqueIdentifier(id = UUID.randomUUID())
        )
        val mutation = GraphQLMutationGenerator().processStates(
            graphableState.buildEntityMap(),
            TransactionType.ISSUANCE
        )
        print("Mutation: $mutation")
    }

    @Test
    fun `generate graphql mutation for input and output state general txn`() {
        val graphableState = DummyUUIDState(
            id = UUID.randomUUID(),
            entity1 = UUID.randomUUID(),
            stringValue = "Graphed",
            intValue = 1000000,
            linearId = UniqueIdentifier(id = UUID.randomUUID())
        )
        val insertMutation = GraphQLMutationGenerator().processStates(
            graphableState.buildEntityMap(),
            TransactionType.ISSUANCE
        )

        val upsertMutation = GraphQLMutationGenerator().processStates(
            graphableState.buildEntityMap(),
            TransactionType.GENERAL
        )
        print("Insert Mutation: $insertMutation")
        print("Upsert Mutation: $upsertMutation")
    }
}

data class DummyCurrencyState(val id: UUID,
                              val name: String,
                              val isoCode: String,
                              override val linearId: UniqueIdentifier
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
                          val stringValue: String,
                          val intValue: Int,
                          override val linearId: UniqueIdentifier
) : GraphableState {

    /** The public keys of the involved parties. */
    override val participants: List<AbstractParty> get() = listOf()

    override fun buildEntityMap(): MapOfMaps {
        return mapOf(
            "entityType" to "DummyUUID",
            "id" to id,
            "entity1" to entity1,
            "stringValue" to stringValue,
            "intValue" to intValue
        )
    }
}