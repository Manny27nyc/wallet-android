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
package com.mycelium.wallet.exchange

import com.mycelium.generated.rates.database.RatesDB
import com.mycelium.wapi.model.ExchangeRate


class RatesBacking(val database: RatesDB) {
    private val queries = database.ratesQueries

    fun allExchangeRates(): List<ExchangeRate> =
            queries.selectAll(mapper = { from, to, market, rate, time ->
                ExchangeRate(market, time, rate, from, to)
            }).executeAsList()

    fun storeExchangeRates(fromCurrency: String, rates: List<ExchangeRate>) {
        queries.transaction {
            rates.forEach {
                queries.insertRate(fromCurrency, it.currency, it.name, it.price, it.time)
            }
        }
    }
}