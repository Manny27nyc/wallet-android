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

import com.mrd.bitlib.model.BitcoinAddress
import com.mrd.bitlib.model.AddressType
import java.io.Serializable

enum class BipDerivationType(val purpose: Byte, val addressType: AddressType) : Serializable {
    BIP44(44, AddressType.P2PKH),
    BIP49(49, AddressType.P2SH_P2WPKH),
    BIP84(84, AddressType.P2WPKH),
    BIP86(86, AddressType.P2TR);

    fun getHardenedPurpose() = purpose + 0x80000000.toInt()

    companion object {
        fun getDerivationTypeByAddress(address: BitcoinAddress): BipDerivationType =
                getDerivationTypeByAddressType(address.type)

        fun getDerivationTypeByAddressType(addressType: AddressType): BipDerivationType =
                when (addressType) {
                    AddressType.P2PKH -> BIP44
                    AddressType.P2SH_P2WPKH -> BIP49
                    AddressType.P2WPKH -> BIP84
                    AddressType.P2TR -> BIP86
                }
    }
}