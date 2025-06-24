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

import com.mrd.bitlib.model.NetworkParameters
import com.mycelium.wallet.R
import com.mycelium.wallet.activity.StringHandlerActivity
import com.mycelium.wallet.content.Action
import com.mycelium.wallet.pop.PopRequest


class PopAction : Action {
    override fun handle(handlerActivity: StringHandlerActivity, content: String): Boolean {
        if (!isBtcpopURI(content)) {
            return false
        }
        val popRequest: PopRequest
        return try {
            popRequest = PopRequest(content)
            handlerActivity.finishOk(popRequest)
            true
        } catch (e: IllegalArgumentException) {
            handlerActivity.finishError(R.string.pop_invalid_pop_uri)
            false
        }
    }

    override fun canHandle(network: NetworkParameters, content: String): Boolean {
        return isBtcpopURI(content)
    }

    private fun isBtcpopURI(content: String): Boolean {
        return content.startsWith("btcpop:")
    }
}