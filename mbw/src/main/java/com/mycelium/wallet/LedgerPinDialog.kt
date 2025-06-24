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
import android.widget.TextView
import com.google.common.base.Strings
import com.mycelium.wallet.databinding.EnterLedgerPinDialogBinding

class LedgerPinDialog(context: Context, hidden: Boolean) : PinDialog(context, hidden, true) {
    private var pinDisp: TextView? = null

    override fun loadLayout() {
        setContentView(EnterLedgerPinDialogBinding.inflate(layoutInflater).apply {
            numpadBinding = this.keyboard.numPad
        }.root)
    }

    override fun initPinPad() {
        super.initPinPad()
        disps = listOf()
        pinDisp = findViewById<View>(R.id.pin_display) as TextView?
        numpadBinding?.pinBack?.setText(R.string.ok)
        numpadBinding?.pinBack?.setOnClickListener { acceptPin() }
    }

    override fun updatePinDisplay() {
        pinDisp!!.text = Strings.repeat(PLACEHOLDER_TYPED, enteredPin.length)
        checkPin()
    }

    override fun checkPin() {
        if (enteredPin.length >= MAX_PIN_LENGTH) {
            acceptPin()
        }
    }

    companion object {
        const val MAX_PIN_LENGTH = 32
    }
}