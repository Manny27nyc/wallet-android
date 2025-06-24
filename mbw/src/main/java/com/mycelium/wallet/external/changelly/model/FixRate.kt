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

import com.mycelium.wallet.external.changelly2.ExchangeFragment.Companion.CHANGELLY_TERM_OF_USER
import java.math.BigDecimal

data class FixRate(
    val id: String,
    val result: BigDecimal,
    val from: String,
    val to: String,
    val maxFrom: BigDecimal,
    val maxTo: BigDecimal,
    val minFrom: BigDecimal,
    val minTo: BigDecimal,
    val amountFrom: BigDecimal?,
    val amountTo: BigDecimal?,
    val networkFee: BigDecimal?,
    val termsOfUseLink: String? = CHANGELLY_TERM_OF_USER
)
