package com.cgraph.core.support

import net.corda.core.identity.CordaX500Name
import java.util.UUID

typealias MapOfMaps = Map<String, Any>

fun MapOfMaps.property(key: String) = this[key]

fun MapOfMaps.uuid() = UUID.fromString(this["id"] as String)