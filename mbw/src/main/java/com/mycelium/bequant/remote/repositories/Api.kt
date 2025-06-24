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


object Api {
    val accountRepository by lazy { AccountApiRepository() }
    val publicRepository by lazy { PublicApiRepository() }
    val kycRepository by lazy { KYCRepository() }
    val signRepository by lazy { SignRepository() }
    val tradingRepository by lazy { TradingApiRepository() }
}