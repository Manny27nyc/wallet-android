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
/*
 * Copyright 2013, 2014 Megion Research & Development GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mycelium.wapi.wallet.btc.single

import com.mrd.bitlib.model.BitcoinAddress
import com.mrd.bitlib.model.AddressType
import com.mycelium.wapi.wallet.SingleAddressBtcAccountBacking

import java.util.*

/**
 * The context of an account
 */
class SingleAddressAccountContext(
        val id: UUID,
        var addresses: Map<AddressType, BitcoinAddress>,
        private var isArchived: Boolean,
        private var blockHeight: Int,
        defaultAddressType: AddressType
) {
    private var isDirty = false
    var defaultAddressType = defaultAddressType
        set(value) {
            field = value
            isDirty = true
        }

    constructor(context: SingleAddressAccountContext) :
            this(context.id, context.addresses, context.isArchived(), context.getBlockHeight(), context.defaultAddressType)

    /**
     * Is this account archived?
     */
    fun isArchived() = isArchived

    /**
     * Mark this account as archived
     */
    fun setArchived(isArchived: Boolean) {
        if (this.isArchived != isArchived) {
            isDirty = true
            this.isArchived = isArchived
        }
    }

    /**
     * Get the block chain height recorded for this context
     */
    fun getBlockHeight() = blockHeight

    /**
     * Set the block chain height for this context
     */
    fun setBlockHeight(blockHeight: Int) {
        if (this.blockHeight != blockHeight) {
            isDirty = true
            this.blockHeight = blockHeight
        }
    }

    /**
     * Persist this context if it is marked as dirty
     */
    fun persistIfNecessary(backing: SingleAddressBtcAccountBacking) {
        if (isDirty) {
            persist(backing)
        }
    }

    /**
     * Persist this context
     */
    fun persist(backing: SingleAddressBtcAccountBacking) {
        backing.updateAccountContext(this)
        isDirty = false
    }
}
