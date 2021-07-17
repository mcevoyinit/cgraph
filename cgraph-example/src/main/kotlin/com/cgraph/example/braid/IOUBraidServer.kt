package com.cgraph.example.braid

import io.cordite.braid.corda.BraidCordaContext
import io.cordite.braid.core.config.BraidConfig
import io.cordite.braid.core.http.HttpServerConfig
import net.corda.core.node.AppServiceHub
import net.corda.core.node.services.CordaService
import net.corda.core.serialization.SingletonSerializeAsToken

@CordaService
class IOUBraidServer(private val serviceHub: AppServiceHub) : SingletonSerializeAsToken() {

  private val braidContext =
    BraidCordaContext.create(appServiceHub = serviceHub, authConstructor = { IOUAuthProvider() })

  private val port =
    serviceHub.getAppContext().config.get("braidServerPort") as Int

  init {
    BraidConfig.fromResource(braidContext, configFileName)?.bootstrap()
  }

  private fun BraidConfig.bootstrap() {
    this
      .withHttpServerConfig(HttpServerConfig(tlsEnabled = false))
      .withPort(port)
      .withService(braidContext, IOUServiceCore::class.java)
      .start(braidContext)
  }

   // config file name based on the node legal identity
  private val configFileName: String
    get() {
      val name = serviceHub.myInfo.legalIdentities.first().name.organisation
      return "braid-$name.json"
    }
}
