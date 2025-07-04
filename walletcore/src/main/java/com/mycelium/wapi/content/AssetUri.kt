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
package com.mycelium.wapi.content

import com.mycelium.wapi.wallet.Address
import com.mycelium.wapi.wallet.coins.Value
import java.io.Serializable


abstract class AssetUri(open val address: Address?, open val value: Value?,
                        open val label: String?, open val scheme: String?)
    : Serializable {
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (this === other) return true
        if (other !is AssetUri) return false
        return address == other.address && value == other.value && label == other.label && scheme == other.scheme
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (address?.hashCode() ?: 0)
        result = 31 * result + (value?.hashCode() ?: 0)
        result = 31 * result + (label?.hashCode() ?: 0)
        result = 31 * result + (scheme?.hashCode() ?: 0)
        return result
    }

    override fun toString() = "GenericAssetUri(address=$address, value=$value, label=$label, scheme=$scheme)"
}

class PrivateKeyUri(val keyString: String, label: String?, scheme: String) : AssetUri(null, null, label, scheme)