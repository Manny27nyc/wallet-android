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
package com.mycelium.wallet.external.partner.model

import com.google.gson.annotations.SerializedName


data class PartnersLocalized(
    @SerializedName("header-title") val title: String,
    @SerializedName("header-text") val text: String,
    @SerializedName("Partners") val partners: List<Partner>
) : CommonContent()
