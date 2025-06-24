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
package com.mycelium.wapi.wallet.btc.bip44

import com.mrd.bitlib.crypto.HdKeyNode
import com.mycelium.wapi.wallet.manager.Config


data class UnrelatedHDAccountConfig(val hdKeyNodes: List<HdKeyNode>) : Config

class AdditionalHDAccountConfig : Config

data class ExternalSignaturesAccountConfig(val hdKeyNodes: List<HdKeyNode>,
                                            val provider: ExternalSignatureProvider,
                                           val accountIndex: Int) : Config

// config for migrate hd accounts to taproot, just addition bip86 private key
class TaprootMigrationHDAccountConfig(val account: HDAccount) : Config