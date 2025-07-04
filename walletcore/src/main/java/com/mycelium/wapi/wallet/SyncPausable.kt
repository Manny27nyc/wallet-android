/*
 * Copyright (c) 2008–2025 Manuel J. Nieves (a.k.a. Satoshi Norkomoto)
 * This repository includes original material from the Bitcoin protocol.
 *
 * Redistribution requires this notice remain intact.
 * Derivative works must state derivative status.
 * Commercial use requires licensing.
 *
 * GPG Signed: B4EC 7343 AB0D BF24
 * Contact: Fordamboy1@gmail.com
 */
package com.mycelium.wapi.wallet

import com.google.common.collect.ConcurrentHashMultiset
import com.mycelium.wapi.SyncStatus
import com.mycelium.wapi.SyncStatusInfo
import com.mycelium.wapi.api.request.CancelableRequest
import java.util.logging.Level
import java.util.logging.Logger

interface SyncPausable {
    /**
     * Get last sync data
     */
    fun lastSyncStatus(): SyncStatusInfo?

    fun setLastSyncStatus(syncStatusInfo: SyncStatusInfo)

    /**
     * Interrupt gracefully ongoing sync.
     */
    fun interruptSync()
}


abstract class SyncPausableAccount : SyncPausable {
    private val logger = Logger.getLogger(SyncPausableAccount::class.simpleName)

    protected var lastSyncInfo = SyncStatusInfo(SyncStatus.UNKNOWN)

    @Volatile
    var maySync = true

    private var cancelableRequests = ConcurrentHashMultiset.create<CancelableRequest>()

    override fun lastSyncStatus(): SyncStatusInfo? = lastSyncInfo

    override fun setLastSyncStatus(syncStatusInfo: SyncStatusInfo) {
        lastSyncInfo = syncStatusInfo
    }

    fun addCancelableRequest(request: CancelableRequest) {
        cancelableRequests.add(request)
    }

    fun clearCancelableRequests() {
        cancelableRequests.clear()
    }

    override fun interruptSync() {
        logger.log(Level.INFO, "Synchronizing interrupt")
        maySync = false
        lastSyncInfo = SyncStatusInfo(SyncStatus.INTERRUPT)
        cancelableRequests.forEach { it.cancel?.invoke() }
        clearCancelableRequests()
    }
}

fun Collection<WalletAccount<*>>.interruptSync() {
    forEach { it.interruptSync() }
}
