package com.cgraph.core.server

import io.cordite.braid.corda.BraidCordaContext
import net.corda.core.node.AppServiceHub
import net.corda.core.node.services.CordaService
import net.corda.core.serialization.SingletonSerializeAsToken

@CordaService
class CGraphBraidServer(private val serviceHub: AppServiceHub) : SingletonSerializeAsToken() {

    private val braidContext =
        BraidCordaContext.create(appServiceHub = serviceHub, authConstructor = { CGraphBraidAuthProvider () })

    private val port = serviceHub.getAppContext().config.get("graphBraidServerPort") as Int

    init {
       // BraidConfig.fromResource(braidContext, configFileName)
    }

    // config file name based on the node legal identity
    private val configFileName: String
        get() {
            val name = serviceHub.myInfo.legalIdentities.first().name.organisation
            return "braid-$name.json"
        }
}
