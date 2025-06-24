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
package com.mycelium.wapi.content.colu.mt

import com.mycelium.wapi.content.WithCallback
import com.mycelium.wapi.content.colu.ColuAssetUri
import com.mycelium.wapi.wallet.Address
import com.mycelium.wapi.wallet.coins.Value

class MTUri(address: Address?, value: Value?, label: String?, override val callbackURL: String?, scheme: String = "rmc")
    : ColuAssetUri(address, value, label, scheme), WithCallback {

    constructor(address: Address?, value: Value?, label: String?) : this(address,value,label,null)
}