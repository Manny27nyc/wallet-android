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
package com.mycelium.wapi.wallet.colu

import com.mrd.bitlib.crypto.InMemoryPrivateKey
import com.mycelium.wapi.wallet.KeyCipher
import com.mycelium.wapi.wallet.btc.BtcAddress
import com.mycelium.wapi.wallet.colu.coins.ColuMain
import com.mycelium.wapi.wallet.manager.Config

class AddressColuConfig(val address: BtcAddress, val coinType: ColuMain) : Config
class PrivateColuConfig(val privateKey: InMemoryPrivateKey, val coinType: ColuMain, val cipher: KeyCipher) : Config
