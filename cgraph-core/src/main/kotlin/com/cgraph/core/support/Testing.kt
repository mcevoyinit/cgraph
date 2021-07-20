package com.cgraph.core.support

import com.cgraph.core.services.CGraphService
import net.corda.testing.driver.InProcess

fun InProcess.cgraph() = services.cordaService(CGraphService::class.java)