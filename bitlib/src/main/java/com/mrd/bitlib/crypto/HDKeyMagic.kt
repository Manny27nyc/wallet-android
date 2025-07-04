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
package com.mrd.bitlib.crypto

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.mrd.bitlib.crypto.BipDerivationType
import com.mrd.bitlib.util.HexUtils.toBytes

enum class HDKeyMagic(val isPrivate: Boolean, val isProdnet: Boolean, val typeToMagicMap: BiMap<BipDerivationType, ByteArray>) {
    PRODNET_PUBLIC(false, true,
            mapOf(BipDerivationType.BIP44 to toBytes("04 88 B2 1E"),    // xpub
                    BipDerivationType.BIP49 to toBytes("04 9d 7c b2"),    // ypub
                    BipDerivationType.BIP84 to toBytes("04 b2 47 46"),    // zpub
                    BipDerivationType.BIP86 to toBytes("00 00 00 00")
                    ).toBiMap()),
    TESTNET_PUBLIC(false, false,
            mapOf(BipDerivationType.BIP44 to toBytes("04 35 87 CF"),    // tpub
                    BipDerivationType.BIP49 to toBytes("04 4a 52 62"),    // upub
                    BipDerivationType.BIP84 to toBytes("04 5f 1c f6"),    // vpub
                    BipDerivationType.BIP86 to toBytes("00 00 00 00")
                    ).toBiMap()),
    PRODNET_PRIVATE(true, true,
            mapOf(BipDerivationType.BIP44 to toBytes("04 88 AD E4"),    // xprv
                    BipDerivationType.BIP49 to toBytes("04 9d 78 78"),    // yprv
                    BipDerivationType.BIP84 to toBytes("04 b2 43 0c"),    // zprv
                    BipDerivationType.BIP86 to toBytes("00 00 00 00")
                    ).toBiMap()),
    TESTNET_PRIVATE(true, false,
            mapOf(BipDerivationType.BIP44 to toBytes("04 35 83 94"),    // tprv
                    BipDerivationType.BIP49 to toBytes("04 4a 4e 28"),    // uprv
                    BipDerivationType.BIP84 to toBytes("04 5f 18 bc"),  // vprv
                    BipDerivationType.BIP86 to toBytes("00 00 00 00")
                    ).toBiMap())
}

fun <K, V> Map<K, V>.toBiMap() = HashBiMap.create(this)!!