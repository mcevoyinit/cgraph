package com.cgraph.core.future

import net.corda.core.crypto.SecureHash

/**
 * Corda and the graph can get out of sync.
 * We may wish to periodically check and reconcile the differences.
 *
 * Placeholder for future enhancements around reconciliation
 */
interface CGraphReconciler {
    fun scanCordaTxnIds() : List<SecureHash>
    fun scanDGraphTxnIds() : List<SecureHash>
    fun indexAndReconcile()
}