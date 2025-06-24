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
package com.mycelium.wapi.wallet.btc.single

import com.mrd.bitlib.crypto.InMemoryPrivateKey
import com.mrd.bitlib.crypto.PublicKey
import com.mrd.bitlib.model.AddressType
import com.mycelium.wapi.wallet.KeyCipher
import com.mycelium.wapi.wallet.btc.BtcAddress
import com.mycelium.wapi.wallet.manager.Config

open class LabeledConfig(val label: String = ""): Config

open class AddressSingleConfig @JvmOverloads constructor(val address: BtcAddress, label: String = "") : LabeledConfig(label)

open class PublicSingleConfig @JvmOverloads constructor(val publicKey: PublicKey, label: String = "") : LabeledConfig(label)

open class PrivateSingleConfig @JvmOverloads constructor(val privateKey: InMemoryPrivateKey, val
        cipher: KeyCipher, label: String = "", val addressType: AddressType? = null) : LabeledConfig(label)

