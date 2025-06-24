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
package com.mycelium.giftbox.client.model

import com.fasterxml.jackson.annotation.JsonProperty

data class MCProductResponse(
    @JsonProperty("total_count")
    val count: Int,
    /* List of all categories */
    @JsonProperty("all_categories")
    var categories: List<String> = listOf(),
    /* List of all countries that have products in the catalog */
    @JsonProperty("all_countries")
    var countries: List<String> = emptyList(),
    @JsonProperty("items")
    val items: List<MCProductInfo>
)

