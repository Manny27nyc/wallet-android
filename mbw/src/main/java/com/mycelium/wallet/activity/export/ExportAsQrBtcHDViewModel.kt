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
import com.mrd.bitlib.crypto.BipDerivationType

class ExportAsQrBtcHDViewModel(context: Application) : ExportAsQrMultiKeysViewModel(context) {
    override fun updateData(privateDataSelected: Boolean) {
        super.updateData(privateDataSelected)
        onToggleClicked(1)
    }

    /**
     * Updates account data based on extra toggles
     */
    override fun onToggleClicked(toggleNum: Int) {
        val privateData = model.privateDataSelected.value!!
        val privateDataMap = model.accountData.privateDataMap
        val publicDataMap = model.accountData.publicDataMap
        val dataMap = if (privateData) privateDataMap else publicDataMap

        val data = dataMap?.get(when (toggleNum) {
            1 -> BipDerivationType.BIP44
            2 -> BipDerivationType.BIP49
            3 -> BipDerivationType.BIP84
            4 -> BipDerivationType.BIP86
            else -> throw  java.lang.IllegalStateException("Unexpected toggle position")
        })
        model.accountDataString.value = data
    }
}