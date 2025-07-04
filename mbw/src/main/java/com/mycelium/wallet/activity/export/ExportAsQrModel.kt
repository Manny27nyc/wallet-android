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
package com.mycelium.wallet.activity.export

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.mycelium.wapi.wallet.ExportableAccount

class ExportAsQrModel(val context: Application,
                      val accountData: ExportableAccount.Data) {
    val accountDataString: MutableLiveData<String> = MutableLiveData()
    val privateDataSelected: MutableLiveData<Boolean> = MutableLiveData()  // whether user switched to private
    val showRedWarning: MutableLiveData<Boolean> = MutableLiveData()       // show warning instead of qr for private
    private var hasWarningAccepted = false

    fun hasPrivateData(): Boolean = accountData.privateData.isPresent

    fun updateData(privateDataSelected: Boolean) {
        this.privateDataSelected.value = privateDataSelected
        if (privateDataSelected) {
            showRedWarning.value = !hasWarningAccepted
            accountDataString.value = accountData.privateData.get()
        } else {
            showRedWarning.value = false
            accountDataString.value = accountData.publicDataMap!!.values.first()
        }
    }

    fun acceptWaring() {
        hasWarningAccepted = true
        showRedWarning.value = false
    }
}