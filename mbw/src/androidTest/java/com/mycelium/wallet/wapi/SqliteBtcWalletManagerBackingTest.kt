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
package com.mycelium.wallet.wapi

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mrd.bitlib.util.HexUtils
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SqliteBtcWalletManagerBackingTest {
    @Test
    fun testValue() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val backing = SqliteBtcWalletManagerBacking(context)
        val key = HexUtils.toBytes("0123456789abcdef")
        val value = HexUtils.toBytes("fedcba9876543210")
        backing.setValue(key, value)

        val valueFromDb = backing.getValue(key)
        assert(HexUtils.toHex(valueFromDb) == HexUtils.toHex(value))
    }
}