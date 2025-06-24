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
package com.mycelium.wapi.api.jsonrpc

import com.google.gson.annotations.SerializedName
import kotlin.random.Random


class RpcRequestOut(
        @SerializedName(METHOD_KEY)
        val methodName: String,
        val params: RpcParams = RpcNoParams
) {
    var id: String = Random.nextInt().toString()

    @SerializedName(JSON_RPC_IDENTIFIER)
    var version = JSON_RPC_VERSION

    fun toJson(): String = RPC.toJson(this)
}
