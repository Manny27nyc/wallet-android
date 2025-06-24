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

import com.mrd.bitlib.model.hdpath.HdKeyPath
import com.mycelium.wapi.wallet.coins.CryptoCurrency

import java.io.Serializable

interface Address : Serializable {
    val coinType: CryptoCurrency

    // An address for the particular asset could have some subtypes.
    // For example, for BTC we have
    // To have an ability to detect and compare types in generic way
    // the subType is stored as string
    fun getSubType(): String

    fun getBytes(): ByteArray

    fun getBip32Path(): HdKeyPath?

    fun setBip32Path(bip32Path: HdKeyPath?)
}
