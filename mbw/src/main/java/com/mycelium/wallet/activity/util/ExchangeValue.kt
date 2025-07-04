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
package com.mycelium.wallet.activity.util

import com.mycelium.wallet.ExchangeRateManager
import com.mycelium.wallet.exchange.GetExchangeRate
import com.mycelium.wapi.wallet.WalletManager
import com.mycelium.wapi.wallet.coins.AssetInfo
import com.mycelium.wapi.wallet.coins.Value
import java.math.BigDecimal
import java.math.MathContext

/**
 * this need for get amount activity for case not correct exchange with small value(BTC->USD->BTC)
 */
class ExchangeValue(value: Value, val baseValue: Value) : Value(value.type, value.value) {
    override fun isZero() = baseValue.isZero()
}

fun ExchangeRateManager.get(walletManager: WalletManager, value: Value, toCurrency: AssetInfo): Value? {
    if(toCurrency == value.type) {
        return value
    }
    var fromValue = value
    if (value is ExchangeValue) {
        fromValue = value.baseValue
    }
    val rate = GetExchangeRate(walletManager, toCurrency.symbol, fromValue.type.symbol, this).invoke()
    return rate.rate?.let { rateValue ->
        val bigDecimal = rateValue.multiply(BigDecimal(fromValue.value))
                .movePointLeft(fromValue.type.unitExponent)
                .round(MathContext.DECIMAL128)
        ExchangeValue(Value.parse(toCurrency, bigDecimal), fromValue)
    }
}
