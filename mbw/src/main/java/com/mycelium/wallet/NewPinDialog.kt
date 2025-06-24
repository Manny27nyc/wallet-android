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
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import com.mycelium.wallet.databinding.EnterNewPinDialogBinding

class NewPinDialog(context: Context, hidden: Boolean) : PinDialog(context, hidden, true) {
    private val cbResettablePin: CheckBox?

    init {
        this.setTitle(R.string.pin_enter_new_pin)
        val mbwManager = MbwManager.getInstance(context)
        cbResettablePin = findViewById<View>(R.id.cb_resettable_pin) as CheckBox?
        cbResettablePin!!.isChecked = mbwManager.pin.isSet
        cbResettablePin.setOnCheckedChangeListener { compoundButton: CompoundButton?, b: Boolean ->
            updateResetInfo(context)
        }
        updateResetInfo(context)
        isFingerprintEnabled = false
    }

    private fun updateResetInfo(context: Context) {
        val txtInfo = findViewById<View>(R.id.tv_resettable_pin_info) as TextView?
        if (cbResettablePin!!.isChecked) {
            txtInfo!!.text = context.getString(
                R.string.pin_resettable_pin_info,
                Utils.formatBlockcountAsApproxDuration(
                    MbwManager.getInstance(context),
                    Constants.MIN_PIN_BLOCKHEIGHT_AGE_RESET_PIN,
                    Constants.BTC_BLOCK_TIME_IN_SECONDS
                )
            )
        } else {
            txtInfo!!.text = context.getString(R.string.pin_unresettable_pin_info)
        }
    }

    override fun loadLayout() {
        setContentView(EnterNewPinDialogBinding.inflate(layoutInflater).apply {
            numpadBinding = this.numPad
            pinBinding = this.pinDisplay
        }.root)
    }

    val isResettable: Boolean
        get() = cbResettablePin!!.isChecked
}