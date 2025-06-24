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
package com.mycelium.wallet.extsig.trezor.activity

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivityForResult
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.mrd.bitlib.StandardTransactionBuilder
import com.mrd.bitlib.crypto.Bip39
import com.mrd.bitlib.crypto.BipDerivationType
import com.mrd.bitlib.crypto.HdKeyNode
import com.mrd.bitlib.model.hdpath.HdKeyPath
import com.mycelium.wallet.CommonKeys
import com.mycelium.wallet.MbwManager
import com.mycelium.wallet.activity.send.SendCoinsActivity
import com.mycelium.wallet.activity.send.SignTransactionActivity
import com.mycelium.wapi.wallet.btc.BtcAddress
import com.mycelium.wapi.wallet.btc.FeePerKbFee
import io.mockk.every
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TrezorSignTransactionActivityTest {
    lateinit var hdRootNodes: Map<BipDerivationType, HdKeyNode>

    lateinit var mbwManager: MbwManager

    @Before
    fun setup() {
        val masterSeed = Bip39.generateSeedFromWordList(CommonKeys.WORD_LIST, "")
        hdRootNodes = BipDerivationType.entries.associateWith {
            HdKeyNode.fromSeed(masterSeed.bip32Seed, it)
        }
        val appContext = getInstrumentation().targetContext.applicationContext
        mbwManager = MbwManager.getInstance(appContext)
    }

    @Test
    fun testSign() {
        mockkConstructor(StandardTransactionBuilder::class)
        every { anyConstructed<StandardTransactionBuilder>().addOutput(any()) } answers {

        }

        val trezorManager = mbwManager.trezorManager
        mockkObject(trezorManager)

        every { trezorManager["onBeforeScan"]() } returns true
        every {
            trezorManager.getAccountPubKeyNode(any(), any())
        } answers {
            val keyPath = firstArg<HdKeyPath>()
            val derivationType = secondArg<BipDerivationType>()
            hdRootNodes[derivationType]?.createChildNode(keyPath)
        }
        val hdPath = HdKeyPath.BIP44.getCoinTypeBitcoin(true).getAccount(0).getChain(true)
        val hdNodes = listOf(trezorManager.getAccountPubKeyNode(hdPath, BipDerivationType.BIP44))
            .filterNotNull()
        val uuid =
            trezorManager.createOnTheFlyAccount(hdNodes, mbwManager.getWalletManager(true), 0)
        val account = mbwManager.getWalletManager(true).getAccount(uuid)!!
        val address = account.receiveAddress as BtcAddress

        val transaction = account.createTx(
            address,
            account.coinType.value(0),
            FeePerKbFee(account.coinType.value(0)),
            null
        )
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            TrezorSignTransactionActivity::class.java
        )
            .putExtra(SendCoinsActivity.ACCOUNT, uuid)
            .putExtra(SendCoinsActivity.IS_COLD_STORAGE, true)
            .putExtra(SignTransactionActivity.TRANSACTION, transaction)

        launchActivityForResult<TrezorSignTransactionActivity>(intent).use { scenario ->
            scenario.moveToState(Lifecycle.State.CREATED)
            scenario.moveToState(Lifecycle.State.RESUMED)
        }
    }
}