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
package com.mycelium.wallet.external

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.mycelium.wallet.MbwManager
import com.mycelium.wallet.R
import com.mycelium.wallet.activity.modern.Toaster
import com.mycelium.wapi.wallet.btc.bip44.HDAccount
import com.mycelium.wapi.wallet.fio.FioKeyManager


object Ads {
    fun doAction(key: String, context: Context) {
        when (key) {
            "open-fio" -> openFio(context)
        }
    }

    @JvmStatic
    fun openFio(context: Context) {
        val mbwManager = MbwManager.getInstance(context)
        val account = mbwManager.selectedAccount
        if (account is HDAccount && account.isDerivedFromInternalMasterseed()) {
            AlertDialog.Builder(context)
                    .setMessage(context.getString(R.string.confirm_fio_link, (account.accountIndex + 1).toString()))
                    .setPositiveButton(R.string.yes) { _, _ ->
                        val fioKeyManager = mbwManager.fioKeyManager
                        val fpk = fioKeyManager.getFioPublicKey(account.accountIndex)
                        val fpkString = fioKeyManager.formatPubKey(fpk)
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://addresses.fio.foundation/fiorequest/mycelium/$fpkString")))
                    }
                    .setNegativeButton(R.string.no, null)
                    .create()
                    .show()
        } else {
            Toaster(context).toast(R.string.fio_requires_hd_account, false)
        }
    }
}
