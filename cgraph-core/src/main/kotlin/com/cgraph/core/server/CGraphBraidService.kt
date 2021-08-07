package com.cgraph.core.server

import io.cordite.braid.core.annotation.BraidService
import io.swagger.v3.oas.annotations.Operation
import net.corda.core.concurrent.CordaFuture
import java.util.*
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.QueryParam

@BraidService(
    name = "cgraphService",
    description = "cgraph braid service"
) // this name and description is used both for the JSON-RPC and REST protocols
@Path("/cgraph-service") // base path for this service
interface CGraphBraidService {

    @POST
    @Path("/echo")
    @Operation(summary = "")
    fun echo(@QueryParam("text") text: String): CordaFuture<String>

    @POST
    @Path("/invoke-flow")
    @Operation(summary = "")
    fun invokeFlow(@QueryParam("flowName") flowName: String, args: List<Any>): Any

    @POST
    @Path("/post-gql-flow")
    @Operation(summary = "")
    fun postGraphQLFlow(@QueryParam("graphql") graphql: String): CordaFuture<UUID?>

    @POST
    @Path("/drop-data")
    @Operation(summary = "")
    fun dropDataFlow(): Boolean

}
