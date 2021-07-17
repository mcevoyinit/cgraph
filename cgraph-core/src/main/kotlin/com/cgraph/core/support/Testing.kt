package com.cgraph.core.support

import com.cgraph.core.services.CGraphService
import com.cgraph.core.services.GraphQLRequestType
import net.corda.testing.driver.InProcess
import kotlin.test.assertNotNull

fun InProcess.cgraph() = services.cordaService(CGraphService::class.java)