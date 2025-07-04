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
package com.mycelium.wallet.activity.util

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import androidx.fragment.app.FragmentManager
import com.google.common.base.Preconditions
import com.mrd.bitlib.crypto.Bip39
import com.mrd.bitlib.crypto.BipSss
import com.mrd.bitlib.crypto.HdKeyNode
import com.mrd.bitlib.crypto.InMemoryPrivateKey
import com.mycelium.wallet.activity.StringHandlerActivity.*
import com.mycelium.wallet.activity.modern.adapter.SelectAssetDialog
import com.mycelium.wallet.bitid.BitIDSignRequest
import com.mycelium.wallet.content.ResultType
import com.mycelium.wallet.pop.PopRequest
import com.mycelium.wapi.content.AssetUri
import com.mycelium.wapi.wallet.Address
import com.mycelium.wapi.wallet.WalletManager
import java.util.*


fun Intent.getPrivateKey(): InMemoryPrivateKey {
    checkType(ResultType.PRIVATE_KEY)
    return getSerializableExtra(RESULT_PRIVATE_KEY) as InMemoryPrivateKey
}

fun Intent.getHdKeyNode(): HdKeyNode {
    checkType(ResultType.HD_NODE)
    return getSerializableExtra(RESULT_HD_NODE) as HdKeyNode
}

fun Intent.getAddress(walletManager: WalletManager, fragmentManager: FragmentManager) {
    checkType(ResultType.ADDRESS_STRING)
    val address = getStringExtra(RESULT_ADDRESS_STRING_KEY)!!
    val addresses = walletManager.parseAddress(address)
    SelectAssetDialog.getInstance(addresses).show(fragmentManager, "dialog")
}

fun Intent.getAddress(): Address {
    checkType(ResultType.ADDRESS)
    return getSerializableExtra(RESULT_ADDRESS_KEY) as Address
}

fun Intent.getAssetUri(): AssetUri {
    checkType(ResultType.ASSET_URI)
    return getSerializableExtra(RESULT_URI_KEY) as AssetUri
}

fun Intent.getUri(): Uri {
    checkType(ResultType.URI)
    return getParcelableExtra(RESULT_URI_KEY)!!
}

fun Intent.getShare(): BipSss.Share {
    checkType(ResultType.SHARE)
    return getSerializableExtra(RESULT_SHARE_KEY) as BipSss.Share
}

fun Intent.getAccount(): UUID {
    checkType(ResultType.ACCOUNT)
    return getSerializableExtra(RESULT_ACCOUNT_KEY) as UUID
}

fun Intent.getMasterSeed(): Bip39.MasterSeed {
    checkType(ResultType.MASTER_SEED)
    return getSerializableExtra(RESULT_MASTER_SEED_KEY) as Bip39.MasterSeed
}

fun Intent.getPopRequest(): PopRequest {
    checkType(ResultType.POP_REQUEST)
    return getSerializableExtra(RESULT_POP_REQUEST) as PopRequest
}

fun Intent.getBitIdRequest(): BitIDSignRequest {
    checkType(ResultType.BIT_ID_REQUEST)
    return getSerializableExtra(RESULT_BIT_ID_REQUEST) as BitIDSignRequest
}
fun Intent.getWordList(): Array<String> {
    checkType(ResultType.WORD_LIST)
    return getStringArrayExtra(RESULT_WORD_LIST)!!
}

private fun Intent.checkType(type: ResultType) {
    Preconditions.checkState(type === getSerializableExtra(RESULT_TYPE_KEY))
}

fun Window.setupFullscreen() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        insetsController?.hide(WindowInsets.Type.statusBars())
    } else {
        setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }
}