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
package com.mycelium.wapi.api.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.mycelium.wapi.model.ExchangeRate

import java.io.Serializable

class GetExchangeRatesResponse(
        @param:JsonProperty @field:JsonProperty val fromCurrency: String,
        @param:JsonProperty @field:JsonProperty val toCurrency: String,
        @param:JsonProperty @field:JsonProperty val exchangeRates: List<ExchangeRate>) : Serializable {
    override fun toString() = "$fromCurrency-$toCurrency(${exchangeRates.size} rates)"

    // For Jackson
    @Suppress("unused")
    constructor() : this("", "", emptyList())

    companion object {
        private const val serialVersionUID = 1L
    }
}
