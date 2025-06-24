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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.mycelium.wallet.MbwManager
import com.mycelium.wallet.R
import com.mycelium.wallet.activity.modern.Toaster
import com.mycelium.wapi.wallet.Address
import com.mycelium.wapi.wallet.coins.CryptoCurrency

class BankCardServiceDescription : BuySellServiceDescriptor(R.string.si_buy_sell, R.string.si_buy_sell_s_description, R.string.si_setting_show_button_summary, R.drawable.credit_card_buy) {

    override fun launchService(context: Activity, mbwManager: MbwManager, activeReceivingAddress: Address, currency: CryptoCurrency) {
        if (!mbwManager.selectedAccount.canSpend()) {
            Toaster(context).toast(R.string.lt_warning_watch_only_account, false)
            return
        }
        val receivingAddress = mbwManager.selectedAccount.receiveAddress
        if (receivingAddress != null) {
            context.startActivity(Intent(context, BuySellSelectCountryActivity::class.java)
                    .putExtra("address", activeReceivingAddress)
                    .putExtra("currency", currency))
        } else {
            Toaster(context).toast("Сannot start - no available address.", false)
        }
    }

    override fun isEnabled(mbwManager: MbwManager): Boolean =
            mbwManager.metadataStorage.simplexIsEnabled

    override fun setEnabled(mbwManager: MbwManager, enabledState: Boolean) {
        mbwManager.metadataStorage.simplexIsEnabled = enabledState
    }


    internal class Adapter(context: Context) : ArrayAdapter<String>(context, R.layout.item_dialog_simplex) {
        val content = mutableMapOf<String, Boolean>()

        fun setData(data: Map<String, Boolean>) {
            content.clear()
            content.putAll(data)
            clear()
            addAll(content.keys)
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
                super.getView(position, convertView, parent).apply {
                    isEnabled = content[getItem(position)] ?: true
                }
    }
}
