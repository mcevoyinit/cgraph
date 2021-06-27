package com.cgraph.example.braid

import io.cordite.braid.core.annotation.BraidMethod
import io.cordite.braid.core.annotation.BraidService
import io.cordite.braid.core.constants.BraidConstants.BRAID_AUTHORIZATION_SCHEME_NAME
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import net.corda.core.concurrent.CordaFuture
import net.corda.core.contracts.StateAndRef
import net.corda.core.node.services.Vault
import net.corda.core.node.services.vault.DEFAULT_PAGE_SIZE
import net.corda.core.transactions.SignedTransaction
import rx.Observable
import java.util.*
import javax.ws.rs.*

@BraidService(
    name = "iouService",
    description = "advanced iou graph service"
) // this name and description is used both for the JSON-RPC and REST protocols
@Path("/iou-service") // base path for this service
interface IOUService {

    @POST
    @Path("/echoFlow")
    @Operation(summary = "")
    fun echoFlow(@QueryParam("text") text: String): String

    @POST
    @Path("/post-gql-flow")
    @Operation(summary = "")
    fun postGraphQLFlow(@QueryParam("graphql") graphql: String): CordaFuture<UUID?>

    @POST
    @Path("/drop-data")
    @Operation(summary = "")
    fun dropDataFlow(): Boolean

    @POST
    @Path("/issue-currency-flow")
    @Operation(summary = "")
    fun issueCurrencyFlow(@QueryParam("name") name: String,
                          @QueryParam("isoCode") isoCode: String,
                          @QueryParam("observerX500Name") observerX500Name: String): SignedTransaction

    @POST
    @Path("/issue-balance-flow")
    @Operation(summary = "")
    fun issueBalanceFlow(@QueryParam("isoCode") isoCode: String,
                         @QueryParam("value") value: Int): SignedTransaction

    @POST
    @Path("/issue-iou-flow")
    @Operation(summary = "")
    fun issueIOUFlow(@QueryParam("iouValue") iouValue: Int,
                     @QueryParam("currencyName") currencyName: String,
                     @QueryParam("borrowerX500Name") borrowerX500Name: String): SignedTransaction
}
