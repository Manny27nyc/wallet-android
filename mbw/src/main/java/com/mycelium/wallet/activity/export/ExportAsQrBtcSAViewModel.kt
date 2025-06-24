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
import com.mrd.bitlib.crypto.InMemoryPrivateKey
import com.mrd.bitlib.model.AddressType
import com.mycelium.wallet.MbwManager
import java.lang.IllegalStateException

class ExportAsQrBtcSAViewModel(context: Application) : ExportAsQrMultiKeysViewModel(context) {
    override fun updateData(privateDataSelected: Boolean) {
        super.updateData(privateDataSelected)
        onToggleClicked(2)
    }

    /**
     * Updates account data based on extra toggles
     */
    override fun onToggleClicked(toggleNum: Int) {
        val privateData = model.accountData.privateData

        model.accountDataString.value = if (model.privateDataSelected.value!!) {
            privateData.get()
        } else {
            val publicDataMap = model.accountData.publicDataMap
            publicDataMap?.get(when (toggleNum) {
                1 -> BipDerivationType.BIP44
                2 -> BipDerivationType.BIP49
                3 -> BipDerivationType.BIP84
                4 -> BipDerivationType.BIP86
                else -> throw IllegalStateException("Unexpected toggle position")
            })
        }
    }

    private fun publicData(privateData: String, toggleNum: Int): String {
        val network = MbwManager.getInstance(context).network
        val privateKey = InMemoryPrivateKey.fromBase58String(privateData, network)
        val publicKey = privateKey?.publicKey
        val addressType = when (toggleNum) {
            1 -> AddressType.P2PKH
            2 -> AddressType.P2SH_P2WPKH
            3 -> AddressType.P2WPKH
            4 -> AddressType.P2TR
            else -> throw  IllegalStateException("Unexpected toggle position")
        }
        return publicKey?.toAddress(network, addressType).toString()
    }
}