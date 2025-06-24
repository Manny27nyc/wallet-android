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
package com.mycelium.wapi.api

object WapiConst {
    object Function {
        const val GET_EXCHANGE_RATES = "getExchangeRates"
        const val PING = "ping"
        const val COLLECT_ERROR = "collectError"
        const val GET_VERSION_INFO_EX = "getVersionEx"
    }

    const val WAPI_BASE_PATH = "/wapi"
}
