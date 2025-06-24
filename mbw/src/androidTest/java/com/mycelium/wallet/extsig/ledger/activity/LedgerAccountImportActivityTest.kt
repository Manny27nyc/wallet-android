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
package com.mycelium.wallet.extsig.ledger.activity

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.widget.ListView
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivityForResult
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mrd.bitlib.crypto.Bip39
import com.mrd.bitlib.crypto.BipDerivationType
import com.mrd.bitlib.crypto.HdKeyNode
import com.mrd.bitlib.model.hdpath.HdKeyPath
import com.mycelium.wallet.CommonKeys
import com.mycelium.wallet.R
import com.mycelium.wallet.Utils
import com.mycelium.wallet.activity.HdAccountSelectorActivity.Companion.COIN_TYPE
import com.mycelium.wallet.activity.awaitCondition
import io.mockk.every
import io.mockk.mockkObject
import org.hamcrest.CoreMatchers.anything
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LedgerAccountImportActivityTest {

    lateinit var hdRootNodes: Map<BipDerivationType, HdKeyNode>

    @Before
    fun setup() {
        val masterSeed = Bip39.generateSeedFromWordList(CommonKeys.WORD_LIST, "")
        hdRootNodes = BipDerivationType.entries.associateWith {
            HdKeyNode.fromSeed(masterSeed.bip32Seed, it)
        }
    }

    @Test
    fun testAccountSelection() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            LedgerAccountImportActivity::class.java
        ).putExtra(COIN_TYPE, Utils.getBtcCoinType())

        launchActivityForResult<LedgerAccountImportActivity>(intent).use { scenario ->
            scenario.moveToState(Lifecycle.State.CREATED)
            scenario.onActivity { activity ->
                mockkObject(activity.masterseedScanManager!!)

                every { activity.masterseedScanManager!!["onBeforeScan"]() } returns true
                every {
                    activity.masterseedScanManager!!.getAccountPubKeyNode(any(), any())
                } answers {
                    val keyPath = firstArg<HdKeyPath>()
                    val derivationType = secondArg<BipDerivationType>()
                    hdRootNodes[derivationType]?.createChildNode(keyPath)
                }
            }
            scenario.moveToState(Lifecycle.State.RESUMED)

            // click first account from loaded list
            awaitCondition(30000) {
                var count = 0
                onView(withId(R.id.lvAccounts)).check { view, _ ->
                    val listView = view as ListView
                    count = listView.adapter.count
                }
                count > 0
            }
            onData(anything())
                .atPosition(0)
                .perform(click())


            val resultCode = scenario.result.resultCode
            val resultData = scenario.result.resultData

            assert(resultCode == RESULT_OK)
            assert(resultData.hasExtra("account"))
            scenario.close()
        }
    }
}