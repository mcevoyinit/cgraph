package com.cgraph.core.support

import net.corda.testing.node.TestCordapp

object CGraph  {
    object Cordapps {
        val Core by lazy { TestCordapp.findCordapp("com.cgraph.core") }
        val Example by lazy { TestCordapp.findCordapp("com.cgraph.example") }
    }
}