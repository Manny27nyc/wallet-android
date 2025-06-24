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

data class MCError(
    @JsonProperty("code")
    val code: Int,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("body")
    val body: String
)

data class MCErrorWrap(
    @JsonProperty("detail")
    val error: MCError
)