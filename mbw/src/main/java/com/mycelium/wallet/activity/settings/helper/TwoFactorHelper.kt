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
package com.mycelium.wallet.activity.settings.helper

import com.mycelium.wallet.PinDialog
import com.mycelium.wallet.activity.util.Pin


class TwoFactorHelper(private val pinDialog: PinDialog) {
    var isFingerprintSuccess = false
    var enteredPin: Pin = Pin("")
    var listener: PinDialog.OnPinEntered? = null
    var needFingerCallback: (() -> Unit)? = null

    fun pinEntered(pin: Pin) {
        enteredPin = pin
        checkAndCall()
    }

    fun fingerprintSuccess() {
        isFingerprintSuccess = true
        checkAndCall()
    }

    private fun checkAndCall() {
        if (isFingerprintSuccess && enteredPin.isSet) {
            listener?.pinEntered(pinDialog, enteredPin)
        } else if (!isFingerprintSuccess && enteredPin.isSet) {
            needFingerCallback?.invoke()
        }
    }
}