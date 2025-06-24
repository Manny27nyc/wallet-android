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
package com.mycelium.wallet.external.changelly.model

import java.io.Serializable
import java.math.BigDecimal


class ChangellyTransactionOffer : Serializable {
    @JvmField
    var id: String? = null
    @JvmField
    var payinExtraId: String? = null
    var status: String? = null
    @JvmField
    var currencyFrom: String? = null
    var currencyTo: String? = null
    @JvmField
    var amountTo:BigDecimal = BigDecimal.ZERO
    @JvmField
    var payinAddress: String? = null
    var payoutAddress: String? = null
    var payoutExtraId: String? = null
    var createdAt: String? = null

    val amountExpectedFrom:BigDecimal = BigDecimal.ZERO
    val amountExpectedTo:BigDecimal = BigDecimal.ZERO

    var trackUrl: String? = null
    var type: String? = null
    var refundAddress: String? = null
    var refundExtraId: String? = null
}