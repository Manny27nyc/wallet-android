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
import com.mycelium.wallet.R
import com.mycelium.wallet.activity.StringHandlerActivity
import com.mycelium.wallet.bitid.BitIDAuthenticationActivity
import com.mycelium.wallet.bitid.BitIDSignRequest
import com.mycelium.wallet.content.Action
import java.util.*


class BitIdAction : Action {
    override fun handle(handlerActivity: StringHandlerActivity, content: String): Boolean {
        if (!content.toLowerCase(Locale.US).startsWith("bitid:")) {
            return false
        }
        val request = BitIDSignRequest.parse(Uri.parse(content))
        if (!request.isPresent) {
            handlerActivity.finishError(R.string.unrecognized_format)
            //started with bitid, but unable to parse, so we handled it.
        } else {
            handlerActivity.finishOk(request.get())
        }
        return true
    }

    override fun canHandle(network: NetworkParameters, content: String): Boolean {
        return content.toLowerCase().startsWith("bitid:")
    }
}