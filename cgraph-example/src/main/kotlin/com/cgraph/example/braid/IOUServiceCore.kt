package com.cgraph.example.braid

import com.cgraph.core.flows.DropGraphDataFlow
import com.cgraph.core.flows.PostGraphQLFlow
import com.cgraph.core.services.CGraphService
import com.cgraph.core.support.graphService
import com.cgraph.core.support.graphableString
import com.cgraph.example.flows.IssueBalanceFlow
import com.cgraph.example.flows.IssueCurrencyFlow
import com.cgraph.example.flows.IssueIOUFlow
import io.cordite.braid.corda.BraidCordaContext
import net.corda.core.concurrent.CordaFuture
import net.corda.core.identity.CordaX500Name
import net.corda.core.node.AppServiceHub
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.contextLogger
import java.util.*

@Suppress("unused") // bound at runtime by braid
class IOUServiceCore(braidContext: BraidCordaContext) : IOUService {

    companion object {
        private val log = contextLogger()
    }

    private val serviceHub: AppServiceHub = braidContext.serviceHub
    private val graphService: CGraphService = serviceHub.graphService()

    override fun echoFlow(text: String): String {
        return text
    }

    override fun postGraphQLFlow(graphql: String): CordaFuture<UUID?> {
        return serviceHub.startFlow(PostGraphQLFlow(graphql)).returnValue
    }

    override fun dropDataFlow(): Boolean {
        return serviceHub.startFlow(DropGraphDataFlow()).returnValue.get()
    }

    override fun issueCurrencyFlow(name: String,
                                   isoCode: String,
                                   observerX500Name: String): SignedTransaction {
        val observersX500 = CordaX500Name.parse(observerX500Name.graphableString())
        val party = serviceHub.networkMapCache.getPeerByLegalName(observersX500) ?: error("Party $observerX500Name not found")
        return serviceHub.startFlow(IssueCurrencyFlow(name, isoCode, listOf(party))).returnValue.get()
    }

    override fun issueBalanceFlow(isoCode: String, value: Int): SignedTransaction {
        return serviceHub.startFlow(IssueBalanceFlow(isoCode, value)).returnValue.get()
    }

    override fun issueIOUFlow(iouValue: Int, currencyName: String, borrowerX500Name: String): SignedTransaction {
        val x500Name = CordaX500Name.parse(borrowerX500Name.graphableString())
        val borrowerParty = serviceHub.networkMapCache.getPeerByLegalName(x500Name) ?: error("Party $borrowerX500Name not found")
        return serviceHub.startFlow(IssueIOUFlow(iouValue, currencyName, borrowerParty)).returnValue.get()
    }
}
