package com.cgraph.core.states

import com.cgraph.core.support.MapOfMaps
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.LinearState
import net.corda.core.identity.AbstractParty
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import org.hibernate.annotations.Type

interface GraphableState : LinearState { //,  QueryableState {
    /**
     * @return [MapOfMaps] representation of the implementing contract state.
     * This map is used by CGraph to generate mutations in order to write persisted states to the graph db instance.
     * Future enhances may offer more niche annotation support to better track relationships across state model and graph entities.
     */
    fun buildEntityMap(): MapOfMaps

    /*override fun generateMappedObject(schema: MappedSchema): PersistentState {
        return when (schema) {
            is GraphableSchemaV1 -> GraphableSchemaV1.PersistentGraphable(
                id = this.buildEntityMap()["id"] as String,
                graphed = false
            )
            else -> throw IllegalArgumentException("Unrecognised schema $schema")
        }
    }

    override fun supportedSchemas(): Iterable<MappedSchema> = listOf(GraphableSchemaV1)*/
}

/**
 * Schema object for [GraphableState].
 */
object GraphableSchema


/**
 * The GraphableState schema.
 *
 * This is the data structure needed for CGraph's two phase commit approach towards achieving
 * as high real-time consistency as possible between the corda and graph databases.
 *
 * [id] can be the shared entity id across the state and graph entity
 * [graphed] is only marked as true once we've received confirmation that the entity is marked in the graph db.
 * We can query for graphed=false across restarts to see what [GraphableState] may be laying around ungraphed.
 * Ideally the graph database is set up to handle deduplication, etc.
 */
/*object GraphableSchemaV1 : MappedSchema(
    schemaFamily = GraphableSchema.javaClass,
    version = 1,
    mappedTypes = listOf(PersistentGraphable::class.java)) {

    override val migrationResource: String?
        get() = "graphable.changelog-master";

    @Entity
    @Table(name = "graphable_states")
    class PersistentGraphable(
        @Column(name = "id")
        var id: String,

        @Column(name = "graphed")
        var graphed: Boolean

        ) : PersistentState() {
        // Default constructor required by hibernate.
        constructor(): this("", false)
    }
}*/

