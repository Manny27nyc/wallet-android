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
package com.mrd.bitlib.model

import java.io.Serializable

enum class AddressType : Serializable {
    P2PKH, // Legacy
    P2WPKH, // Supported
    P2TR, // Taproot
    P2SH_P2WPKH; // Default
    //P2PK, // Not supported
    //P2SH, // Not supported, use P2SH_P2WPKH
    //P2WSH, // Not supported
    //P2SH_P2WSH // Not supported

    companion object {
        private const val serialVersionUID = 1L
    }
}