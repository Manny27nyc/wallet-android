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
package com.mycelium.bequant.remote.repositories

import com.mycelium.bequant.BequantConstants.EXCLUDE_COIN_LIST
import com.mycelium.bequant.BequantConstants.changeCoinToServer
import com.mycelium.bequant.remote.doRequest
import com.mycelium.bequant.remote.trading.api.TradingApi
import com.mycelium.bequant.remote.trading.model.Balance
import com.mycelium.bequant.remote.trading.model.Order
import kotlinx.coroutines.CoroutineScope

class TradingApiRepository {
    private val api = TradingApi.create()

    fun tradingBalanceGet(scope: CoroutineScope,
                          success: (Array<Balance>?) -> Unit, error: (Int, String) -> Unit, finally: (() -> Unit)? = null) {
        doRequest(scope, {
            api.tradingBalanceGet()
        }, successBlock = success, errorBlock = error, finallyBlock = finally,
                responseModifier = { result ->
                    result?.filterNot { EXCLUDE_COIN_LIST.contains(it.currency) }?.apply {
                        firstOrNull { it.currency == "USD" }?.currency = "USDT"
                    }?.toTypedArray()
                })
    }

    fun orderPost(scope: CoroutineScope, symbol: String, side: String, quantity: String, clientOrderId: String,
                          type: String, timeInForce: String, price: String, stopPrice: String,
                          expireTime: java.util.Date?, strictValidate: Boolean, postOnly: Boolean,
                          success: (Order?) -> Unit, error: (Int, String) -> Unit, finally: () -> Unit) {
        doRequest(scope, {
            api.orderPost(changeCoinToServer(symbol), side, quantity, clientOrderId, type, timeInForce, price, stopPrice,
                    expireTime, strictValidate, postOnly)
        }, successBlock = success, errorBlock = error, finallyBlock = finally)
    }
}