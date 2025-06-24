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
package com.mycelium.wallet

import android.content.Context
import android.os.Handler
import com.mycelium.wallet.event.MigrationStatusChanged
import com.mycelium.wallet.event.MigrationPercentChanged
import com.mycelium.wallet.persistence.MetadataStorage
import com.mycelium.wapi.wallet.LoadingProgressStatus
import com.mycelium.wapi.wallet.LoadingProgressUpdater

class LoadingProgressTracker(val context: Context) : LoadingProgressUpdater {
    val eventBus = MbwManager.getEventBus()!!

    override var status = LoadingProgressStatus(LoadingProgressStatus.State.STARTING)
        set(value) {
            Handler(context.mainLooper).post {
                eventBus.post(MigrationStatusChanged(value))
            }
            field = value
        }
    override var percent = 0
        set(value) {
            Handler(context.mainLooper).post {
                eventBus.post(MigrationPercentChanged(value))
            }
            field = value
        }


    override fun clearLastFullUpdateTime() {
        MetadataStorage.setLastFullSync(0)
    }
}
