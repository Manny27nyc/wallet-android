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
package com.mycelium.wapi.wallet

import com.mycelium.wapi.wallet.btc.coins.BitcoinMain
import com.mycelium.wapi.wallet.coins.Value
import org.junit.Test
import java.math.BigInteger

class UtilTest {
    private val coinType = BitcoinMain

    @Test
    fun strToBigInteger() {
        val valueBelowOne1 = "0.006"
        val valueBelowOne2 = "1.0E-7"
        val valueAboveOne = "5.0E8"
        assert(Util.strToBigInteger(coinType, valueBelowOne1) == BigInteger.valueOf(600000))
        assert(Util.strToBigInteger(coinType, valueBelowOne2) == BigInteger.valueOf(10))
        assert(Util.strToBigInteger(coinType, valueAboveOne) == BigInteger.valueOf(50000000000000000))
    }

    @Test
    fun valueToDouble() {
        val valueBelowOne = Value.valueOf(BitcoinMain, 60000)
        val valueAboveOne = Value.valueOf(BitcoinMain, 600000000000)
        assert(Util.valueToDouble(valueBelowOne) == 0.0006)
        assert(Util.valueToDouble(valueAboveOne) == 6000.0)
    }
}