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
package com.mycelium.wallet.activity.modern.adapter

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.mycelium.wallet.MbwManager
import com.mycelium.wallet.R
import com.mycelium.wallet.Utils
import com.mycelium.wallet.event.AssetSelected
import com.mycelium.wapi.wallet.Address
import com.squareup.otto.Bus

class SelectAssetDialog : DialogFragment() {
    private val bus: Bus = MbwManager.getEventBus()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val items = addressList!!.map { it.coinType.name }.toTypedArray()
        return AlertDialog.Builder(activity)
                .setCustomTitle(TextView(context).also {
                    it.text = String.format(getString(R.string.diff_type), Utils.getClipboardString(activity))
                    it.textSize = 16f
                    it.setPadding(20, 20, 20, 20)
                })
                .setIcon(R.drawable.ic_launcher)
                .setNegativeButton("cancel") {
                    dialog, which -> dialog.dismiss()
                }
                .setItems(items) { dialog, which ->
                    Log.d("selectassetlog", "setItems onClick: item selected: $which")
                    bus.post(AssetSelected(addressList!![which]))
                }
                .create()
    }

    override fun onResume() {
        super.onResume()
        bus.register(this)
    }

    override fun onPause() {
        super.onPause()
        bus.unregister(this)
    }

    companion object {
        private var addressList: List<Address>? = null
        private val instance: SelectAssetDialog = SelectAssetDialog()

        @JvmStatic
        fun getInstance(genericAddresses: List<Address>): SelectAssetDialog {
            addressList = genericAddresses
            return instance
        }
    }
}
