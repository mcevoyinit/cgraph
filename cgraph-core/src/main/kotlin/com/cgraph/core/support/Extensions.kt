package com.cgraph.core.support

import com.cgraph.core.services.CGraphService
import net.corda.core.identity.CordaX500Name
import net.corda.core.node.ServiceHub
import java.util.*

fun String.uuid(): UUID = UUID.fromString(this)

fun ServiceHub.graphService() = cordaService(CGraphService::class.java)

fun CordaX500Name?.graphableString() = toString().replace("\\s".toRegex(), "")

fun String.graphableString() = replace("\\s".toRegex(), "")

fun getResourceAsText(path: String): String {
    return object {}.javaClass.getResource(path).readText()
}