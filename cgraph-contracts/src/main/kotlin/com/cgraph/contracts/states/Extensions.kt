package com.cgraph.contracts.states

import net.corda.core.identity.CordaX500Name
import java.util.*

fun String.uuid(): UUID = UUID.fromString(this)

fun CordaX500Name?.graphableString() = toString().replace("\\s".toRegex(), "")

fun String.graphableString() = replace("\\s".toRegex(), "")

fun getResourceAsText(path: String): String {
    return object {}.javaClass.getResource(path).readText()
}