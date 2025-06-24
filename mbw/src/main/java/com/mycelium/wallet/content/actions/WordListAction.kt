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
package com.mycelium.wallet.content.actions

import com.mrd.bitlib.crypto.Bip39
import com.mrd.bitlib.model.NetworkParameters
import com.mycelium.wallet.activity.StringHandlerActivity
import com.mycelium.wallet.content.Action


class WordListAction : Action {
    override fun handle(handlerActivity: StringHandlerActivity, content: String): Boolean {
        val words = content.split()
        if (!Bip39.isValidWordList(words)) {
            return false
        }
        handlerActivity.finishOk(words)
        return true
    }

    override fun canHandle(network: NetworkParameters, content: String): Boolean =
        Bip39.isValidWordList(content.split())

    private fun String.split() =
        this.split("[ ,;]".toRegex()).filter { it.isNotEmpty() }.toTypedArray()
}