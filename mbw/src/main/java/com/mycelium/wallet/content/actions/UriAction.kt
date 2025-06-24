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

import android.net.Uri
import com.mrd.bitlib.model.NetworkParameters
import com.mycelium.wallet.MbwManager
import com.mycelium.wallet.R
import com.mycelium.wallet.activity.StringHandlerActivity
import com.mycelium.wallet.content.Action


class UriAction @JvmOverloads constructor(private val onlyWithAddress: Boolean = false) : Action {
    override fun handle(handlerActivity: StringHandlerActivity, content: String): Boolean {
        val manager = MbwManager.getInstance(handlerActivity)
        val uri = manager.contentResolver.resolveUri(content)
        if (uri != null) {
            if (onlyWithAddress && uri.address == null) {
                return false
            }
            handlerActivity.finishOk(uri)
            return true
        }
        return false
    }

    override fun canHandle(network: NetworkParameters, content: String): Boolean {
        return Uri.parse(content) != null
    }
}