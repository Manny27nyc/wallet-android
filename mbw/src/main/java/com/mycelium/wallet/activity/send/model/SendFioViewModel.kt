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
package com.mycelium.wallet.activity.send.model

import android.app.Activity
import android.app.Application
import android.content.Intent
import com.mycelium.wallet.activity.util.EthFeeFormatter
import com.mycelium.wallet.activity.util.toStringWithUnit
import com.mycelium.wapi.wallet.WalletAccount
import com.mycelium.wapi.wallet.btc.bip44.HDAccountExternalSignature
import com.mycelium.wapi.wallet.coins.Value
import java.util.regex.Pattern

class SendFioViewModel(application: Application) : SendCoinsViewModel(application) {
    override val uriPattern = Pattern.compile("[a-zA-Z0-9]+")!!

    override fun init(account: WalletAccount<*>, intent: Intent) {
        super.init(account, intent)
        model = SendFioModel(getApplication(), account, intent)
    }

    override fun sendTransaction(activity: Activity) {
        if (isColdStorage() || model.account is HDAccountExternalSignature) {
            // We do not ask for pin when the key is from cold storage or from a external device (trezor,...)
            model.signTransaction(activity)
            sendFioObtData()
        } else {
            mbwManager.runPinProtectedFunction(activity) {
                model.signTransaction(activity)
                sendFioObtData()
            }
        }
    }

    override fun getFeeFormatter() = EthFeeFormatter()

    fun fee(value: Value?): String = value?.toStringWithUnit() ?: SendFioModel.DEFAULT_FEE

    fun feeFiat(value: Value?): String {
        val rate = mbwManager.exchangeRateManager.get(value, mbwManager.getFiatCurrency(model.account.coinType))?.toStringWithUnit()
        return if (rate != null) "~$rate" else ""
    }
}