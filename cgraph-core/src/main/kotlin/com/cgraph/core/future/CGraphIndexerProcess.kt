package com.cgraph.core.future

import net.corda.core.crypto.SecureHash

/**
 * Placeholder for future enhancements around reconciliation
 *
 * DGraph and Corda can get out of sync.
 * We may wish to periodically check and reconcile the differences.
 */
interface Indexer {
    fun scanCordaTxnIds() : List<SecureHash>
    fun scanDGraphTxnIds() : List<SecureHash>
    fun indexAndReconcile()
}