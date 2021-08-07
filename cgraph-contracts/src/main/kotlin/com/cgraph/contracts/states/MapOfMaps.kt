package com.cgraph.contracts.states

import java.util.*

typealias MapOfMaps = Map<String, Any>

fun MapOfMaps.property(key: String) = this[key]

fun MapOfMaps.uuid() = UUID.fromString(this["id"] as String)

fun MapOfMaps.remove(key: String): MapOfMaps {
    val mutableMap = toMutableMap()
    mutableMap.remove(key)
    return mutableMap.toMap()
}