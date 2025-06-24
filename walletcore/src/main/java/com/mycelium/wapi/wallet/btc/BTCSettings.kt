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
package com.mycelium.wapi.wallet.btc

import com.mrd.bitlib.model.AddressType
import com.mycelium.wapi.wallet.CurrencySettings

data class BTCSettings(
        var defaultAddressType: AddressType,
        var changeAddressModeReference: Reference<ChangeAddressMode>
) : CurrencySettings {
    fun setChangeAddressMode(changeAddressMode: ChangeAddressMode) =
        changeAddressModeReference.set(changeAddressMode)
}


class Reference<T>(private var referent: T?) {
    fun set(newVal: T) {
        referent = newVal
    }

    fun get(): T? {
        return referent
    }
}