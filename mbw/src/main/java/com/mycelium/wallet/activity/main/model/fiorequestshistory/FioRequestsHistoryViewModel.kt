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
package com.mycelium.wallet.activity.main.model.fiorequestshistory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mycelium.wallet.MbwManager

/**
 * Model for [com.mycelium.wallet.activity.main.TransactionHistoryFragment]
 */
class FioRequestsHistoryViewModel(application: Application) : AndroidViewModel(application) {
    val mbwManager = MbwManager.getInstance(application)
    val fioRequestHistory = FioRequestsLiveData(mbwManager)
    val addressBook = mbwManager.metadataStorage.allAddressLabels

    fun cacheAddressBook() {
        addressBook.clear()
        addressBook.putAll(mbwManager.metadataStorage.allAddressLabels)
    }
}