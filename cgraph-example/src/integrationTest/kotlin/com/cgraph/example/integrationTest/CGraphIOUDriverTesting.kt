package com.cgraph.example.integrationTest

import com.cgraph.core.flows.DropGraphDataFlow
import com.cgraph.core.flows.PostGraphQLFlow
import com.cgraph.core.services.GraphQLRequestType
import com.cgraph.core.support.CGraph
import com.cgraph.core.support.graphableString
import com.cgraph.example.data.QUERY_CURRENCY_BY_ISO
import com.cgraph.example.data.UPSERT_MEMBER_GQL
import com.cgraph.example.flows.IssueBalanceFlow
import com.cgraph.example.flows.IssueCurrencyFlow
import com.cgraph.example.flows.IssueIOUFlow
import com.cgraph.example.flows.UpdateBalanceFlow
import com.cgraph.example.states.BalanceState
import com.cgraph.example.support.verifyBalance
import com.cgraph.example.support.verifyCurrency
import net.corda.core.identity.CordaX500Name
import net.corda.core.utilities.getOrThrow
import net.corda.node.services.Permissions
import net.corda.testing.common.internal.testNetworkParameters
import net.corda.testing.driver.DriverParameters
import net.corda.testing.driver.NodeParameters
import net.corda.testing.driver.driver
import net.corda.testing.node.User
import org.junit.Test
import java.util.*

class CGraphIOUDriverTesting {

    val DUMMY_LENDER_NAME = CordaX500Name("Lender", "London", "GB")
    val DUMMY_BORROWER_NAME = CordaX500Name("Borrower", "Hanoi", "VN")

    val DGRAPH_URL_LENDER = "https://dawn-sky.eu-west-1.aws.cloud.dgraph.io"
    val DGRAPH_URL_BORROWER = "https://proud-wave.eu-central-1.aws.cloud.dgraph.io"
    
    val DGRAPH_TOKEN_LENDER = "YjNkN2EwMThjMzhjMDMzM2I1MDFmNWM3ZDE2YTI5YWM="
    val DGRAPH_TOKEN_BORROWER = "MTgxOTkyZDcyMjcwYjg1OTE5MGRiNDQwOTUxYjgwMDc="

     val user = User(
        username = "user1",
        password = "test",
        permissions = setOf(Permissions.all())
    )

    @Test
    fun `graph enabled advanced iou cordapp e2e processing`() {
        driver(DriverParameters(
            startNodesInProcess = true,
            networkParameters = testNetworkParameters(minimumPlatformVersion = 4, notaries = emptyList())
        ))
        {
            val (lender, borrower) = listOf(
                startNode(
                    parameters = NodeParameters(
                        providedName = DUMMY_LENDER_NAME,
                        rpcUsers = listOf(user),
                        additionalCordapps = setOf(
                            CGraph.Cordapps.Example.withConfig(
                                mapOf(
                                    "braidServerPort" to 9090
                                )
                            ),
                            CGraph.Cordapps.Core.withConfig(
                                mapOf(
                                    "graphQLUrl" to DGRAPH_URL_LENDER,
                                    "graphQLToken" to DGRAPH_TOKEN_LENDER,
                                    "graphBraidServerPort" to 8080
                                )
                            )
                        )
                    )
                ),
                startNode(
                    parameters = NodeParameters(
                        providedName = DUMMY_BORROWER_NAME,
                        rpcUsers = listOf(user),
                        additionalCordapps = setOf(
                            CGraph.Cordapps.Example.withConfig(
                                mapOf(
                                    "braidServerPort" to 9091
                                )
                            ),
                            CGraph.Cordapps.Core.withConfig(
                                mapOf(
                                    "graphQLUrl" to DGRAPH_URL_BORROWER,
                                    "graphQLToken" to DGRAPH_TOKEN_BORROWER,
                                    "graphBraidServerPort" to 8081
                                )
                            )
                        )
                    )
                )
            ).map { it.getOrThrow() }

            val member1id = UUID.randomUUID().toString()
            val member2id = UUID.randomUUID().toString()

            val borrowerParty = borrower.nodeInfo.legalIdentities.first()

            println("Dropping data from graphs databases")
            borrower.rpc.startFlowDynamic(DropGraphDataFlow::class.java)
            lender.rpc.startFlowDynamic(DropGraphDataFlow::class.java)

            println("Writing Members to Graph")
            lender.rpc.startFlowDynamic(
                PostGraphQLFlow::class.java,
                UPSERT_MEMBER_GQL(
                    id = member1id,
                    cordaNodeName = lender.nodeInfo.legalIdentities.first().name.graphableString()
                ),
                GraphQLRequestType.MUTATION
            )
            lender.rpc.startFlowDynamic(
                PostGraphQLFlow::class.java,
                UPSERT_MEMBER_GQL(
                    id = member2id,
                    cordaNodeName = borrower.nodeInfo.legalIdentities.first().name.graphableString()
                ),
                GraphQLRequestType.MUTATION
            )

            borrower.rpc.startFlowDynamic(
                PostGraphQLFlow::class.java,
                UPSERT_MEMBER_GQL(
                    id = member2id,
                    cordaNodeName = borrower.nodeInfo.legalIdentities.first().name.graphableString()
                ),
                GraphQLRequestType.MUTATION
            )

            borrower.rpc.startFlowDynamic(
                PostGraphQLFlow::class.java,
                UPSERT_MEMBER_GQL(
                    id = member1id,
                    cordaNodeName = lender.nodeInfo.legalIdentities.first().name.graphableString()
                ),
                GraphQLRequestType.MUTATION
            )

            println("Writing Currency to Ledger and Graph of both parties")
            lender.rpc.startFlowDynamic(IssueCurrencyFlow::class.java,"Pound Sterling", "GBP", listOf(borrowerParty)).returnValue.getOrThrow()

            print("Wait for entry to reach both lender and borrower graphs")
            Thread.sleep(1000)

            lender.verifyCurrency("GBP")
            borrower.verifyCurrency("GBP")

            print("Issue balance into both ledgers and thus graphs")
            val lenderBalanceTxn = lender.rpc.startFlowDynamic(IssueBalanceFlow::class.java,"GBP", 100000).returnValue.getOrThrow()
            val borrowerBalanceTxn = borrower.rpc.startFlowDynamic(IssueBalanceFlow::class.java,"GBP", 0).returnValue.getOrThrow()

            lender.verifyBalance("Pound Sterling", "GBP", 100000)
            borrower.verifyBalance("Pound Sterling", "GBP", 0)

            print("Wait for balance entry to reach both lender and borrower graphs")
            Thread.sleep(1000)

            print("Writing IOU to both ledgers and graphs, with updated balances in both also")
            lender.rpc.startFlowDynamic(IssueIOUFlow::class.java, 50000, "Pound Sterling", borrowerParty).returnValue.getOrThrow()

            val lenderBalanceId = (lenderBalanceTxn.coreTransaction.outputStates.single() as BalanceState).linearId.id
            lender.rpc.startFlowDynamic(UpdateBalanceFlow::class.java, -5000, lenderBalanceId)

            val borrowerBalanceId = (lenderBalanceTxn.coreTransaction.outputStates.single() as BalanceState).linearId.id
            lender.rpc.startFlowDynamic(UpdateBalanceFlow::class.java, 5000, borrowerBalanceId)

            // Lender balance deducted. Borrower balance credited
            lender.verifyBalance("Pound Sterling", "GBP", 50000)
            borrower.verifyBalance("Pound Sterling", "GBP", 50000)
        }
    }

    @Test
    fun `currency persisted to corda and and then to the graph`() {
        driver(DriverParameters(
                startNodesInProcess = true,
                cordappsForAllNodes = setOf(
                        CGraph.Cordapps.Core
                ),
                networkParameters = testNetworkParameters(minimumPlatformVersion = 4, notaries = emptyList())
        )) {
            val (lender, borrower) = listOf(
                startNode(providedName = DUMMY_LENDER_NAME),
                startNode(providedName = DUMMY_BORROWER_NAME))
                .map { it.getOrThrow() }

            borrower.rpc.startFlowDynamic(DropGraphDataFlow::class.java)

            lender.rpc.startFlowDynamic(DropGraphDataFlow::class.java)

            lender.rpc.startFlowDynamic(IssueCurrencyFlow::class.java, 1, "CGraph Coin", "CG").returnValue.getOrThrow()

            // Query using @search annotation with predicate
            val cgraphCurrency = lender.rpc.startFlowDynamic(
                PostGraphQLFlow::class.java,
                QUERY_CURRENCY_BY_ISO("CG"))
                .returnValue.getOrThrow()
        }
    }
}


