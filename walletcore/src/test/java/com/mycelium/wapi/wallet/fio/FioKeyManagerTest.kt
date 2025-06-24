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
package com.mycelium.wapi.wallet.fio

import com.mrd.bitlib.crypto.Bip39
import com.mrd.bitlib.crypto.RandomSource
import com.mycelium.wapi.wallet.AesKeyCipher
import com.mycelium.wapi.wallet.SecureKeyValueStore
import com.mycelium.wapi.wallet.btc.InMemoryBtcWalletManagerBacking
import com.mycelium.wapi.wallet.masterseed.MasterSeedManager
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

class FioKeyManagerTest {
    val masterSeedWords = "valley alien library bread worry brother bundle hammer loyal barely dune brave".split(" ")
    val fioAddress = "FIO5kJKNHwctcfUM5XZyiWSqSTM5HTzznJP9F3ZdbhaQAHEVq575o"
    val sut: FioKeyManager

    init {
        val backing = InMemoryBtcWalletManagerBacking()
        val fakeRandomSource = Mockito.mock<RandomSource>(RandomSource::class.java)
        val store = SecureKeyValueStore(backing, fakeRandomSource)
        val masterSeedManager = MasterSeedManager(store).apply {
            val masterSeed = Bip39.generateSeedFromWordList(masterSeedWords, "")
            configureBip32MasterSeed(masterSeed, AesKeyCipher.defaultKeyCipher())
        }
        sut = FioKeyManager(masterSeedManager, store)
    }

    @Test
    fun formatPubKey() {
        val actualPublicKey = sut.getFioPublicKey(0)
        Assert.assertEquals(fioAddress, sut.formatPubKey(actualPublicKey))
    }
}