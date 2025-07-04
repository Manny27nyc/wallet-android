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

import com.mrd.bitlib.crypto.BipSss
import com.mrd.bitlib.model.NetworkParameters
import com.mycelium.wallet.R
import com.mycelium.wallet.activity.StringHandlerActivity
import com.mycelium.wallet.content.Action


class SssShareAction : Action {
    override fun handle(handlerActivity: StringHandlerActivity, content: String): Boolean {
        if (!isShare(content)) {
            return false
        }
        val share = BipSss.Share.fromString(content)
        if (null == share) {
            handlerActivity.finishError(R.string.error_invalid_sss_share)
        } else {
            handlerActivity.finishOk(share)
        }
        return true
    }

    override fun canHandle(network: NetworkParameters, content: String): Boolean =
        isShare(content)

    private fun isShare(content: String): Boolean =
        content.startsWith(BipSss.Share.SSS_PREFIX) ||
                content.startsWith(BipSss.Share.SSS_EXT_PREFIX) ||
                content.startsWith(BipSss.Share.SSS_EXT2_PREFIX)

}