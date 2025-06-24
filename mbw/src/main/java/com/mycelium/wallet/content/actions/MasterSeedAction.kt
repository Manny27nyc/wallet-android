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
import com.mrd.bitlib.util.HexUtils
import com.mycelium.wallet.activity.StringHandlerActivity
import com.mycelium.wallet.content.Action


class MasterSeedAction : Action {
    override fun handle(handlerActivity: StringHandlerActivity, content: String): Boolean {
        if (content.length % 2 != 0) {
            return false
        }
        try {
            val masterSeed = Bip39.MasterSeed.fromBytes(HexUtils.toBytes(content), false)
            if (masterSeed.isPresent) {
                handlerActivity.finishOk(masterSeed.get())
                return true
            }
        } catch (ignore: RuntimeException) {
        }
        return false
    }

    override fun canHandle(network: NetworkParameters, content: String): Boolean {
        return isMasterSeed(content)
    }

    private fun isMasterSeed(content: String): Boolean {
        return try {
            val bytes = HexUtils.toBytes(content)
            Bip39.MasterSeed.fromBytes(bytes, false).isPresent
        } catch (ex: RuntimeException) {
            // HexUtils.toBytes will throw a RuntimeException if the string contains invalid characters
            false
        }
    }
}