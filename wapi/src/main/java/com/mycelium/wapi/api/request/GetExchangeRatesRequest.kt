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
package com.mycelium.wapi.api.request

import com.fasterxml.jackson.annotation.JsonProperty

import java.io.Serializable

class GetExchangeRatesRequest(
        @param:JsonProperty @field:JsonProperty var version: Int,
        @param:JsonProperty @field:JsonProperty var fromCurrency: String,
        @param:JsonProperty @field:JsonProperty var toCurrency: String) : Serializable {
    override fun toString() = "$fromCurrency-$toCurrency"

    // For Jackson
    @Suppress("unused")
    constructor() : this(0, "", "")

    companion object {
        private const val serialVersionUID = 1L
    }
}
