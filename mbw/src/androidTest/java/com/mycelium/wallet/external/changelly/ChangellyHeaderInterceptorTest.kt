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
package com.mycelium.wallet.external.changelly

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

@RunWith(AndroidJUnit4::class)
class ChangellyHeaderInterceptorTest {

    @Test
    fun testChandllyDefaultMethod() = runBlocking {
//        val api = retrofit.create(ChangellyAPIService::class.java)
//        val response = api.getFixRate("eth", "btc")
//        Assert.assertEquals(response.code(), 200)
    }

    @Test
    fun testChandllyExchangeAmountFix() = runBlocking {
//        val api = retrofit.create(ChangellyAPIService::class.java)
//        val response = api.exchangeAmountFix("eth", "btc", BigDecimal.ONE)
//        Assert.assertEquals(response.code(), 200)
    }
}