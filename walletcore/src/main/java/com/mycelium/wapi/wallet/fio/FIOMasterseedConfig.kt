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

import com.mrd.bitlib.crypto.HdKeyNode
import com.mrd.bitlib.crypto.InMemoryPrivateKey
import com.mycelium.wapi.wallet.manager.Config

class FIOMasterseedConfig : Config

class FIOUnrelatedHDConfig @JvmOverloads constructor(val hdKeyNodes: List<HdKeyNode>, val labelBase: String = "") : Config

class FIOAddressConfig(val address: FioAddress) : Config

class FIOPrivateKeyConfig(val privkey: InMemoryPrivateKey) : Config