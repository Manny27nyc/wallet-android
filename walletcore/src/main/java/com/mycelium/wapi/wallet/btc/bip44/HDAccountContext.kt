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

package com.mycelium.wapi.wallet.btc.bip44

import com.mrd.bitlib.crypto.BipDerivationType
import com.mrd.bitlib.model.AddressType
import com.mycelium.wapi.wallet.AccountIndexesContext
import com.mycelium.wapi.wallet.btc.Bip44BtcAccountBacking
import java.util.UUID

/**
 * The abstract context of an account
 */
class HDAccountContext @JvmOverloads constructor(
        val id: UUID,
        val accountIndex: Int,
        @Volatile private var isArchived: Boolean,
        private var blockHeight: Int = 0,
        private var lastDiscovery: Long = 0,
        val indexesMap: MutableMap<BipDerivationType, AccountIndexesContext> = createNewIndexesContexts(
            BipDerivationType.entries
            .asIterable()
        ),
        val accountType: Int = ACCOUNT_TYPE_FROM_MASTERSEED,
        val accountSubId: Int = 0,
        defaultAddressType: AddressType = AddressType.P2SH_P2WPKH
) {
    private var isDirty: Boolean = false
    var defaultAddressType = defaultAddressType
        set(value) {
            field = value
            isDirty = true
        }

    constructor(id: UUID, accountIndex: Int, isArchived: Boolean, defaultAddressTyp: AddressType) :
            this(id, accountIndex, isArchived, defaultAddressType = defaultAddressTyp)

    constructor(context: HDAccountContext) : this(context.id, context.accountIndex,
            context.isArchived(), context.getBlockHeight(), context.getLastDiscovery(), context.indexesMap,
            context.accountType, context.accountSubId, context.defaultAddressType)

    @JvmOverloads constructor(id: UUID, accountIndex: Int, isArchived: Boolean, accountType: Int, accountSubId: Int,
                              derivationTypes: Iterable<BipDerivationType>, defaultAddressType: AddressType = AddressType.P2SH_P2WPKH) :
            this(id, accountIndex, isArchived, 0, 0, createNewIndexesContexts(derivationTypes),
                    accountType, accountSubId, defaultAddressType)

    init {
        isDirty = false
    }

    /**
     * Is this account archived?
     */
    @Override
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

    fun reset() {
        blockHeight = 0
        lastDiscovery = 0
        val newIndexes = createNewIndexesContexts(indexesMap.keys)
        indexesMap.clear()
        indexesMap.putAll(newIndexes)
        isDirty = true
    }

    /**
     * Get the block chain height recorded for this context
     */
    fun getBlockHeight(): Int {
        return blockHeight
    }

    /**
     * Set the block chain height for this context
     */
    fun setBlockHeight(blockHeight: Int) {
        if (this.blockHeight != blockHeight) {
            isDirty = true
            this.blockHeight = blockHeight
        }
    }

    fun getLastExternalIndexWithActivity(type: BipDerivationType): Int =
            indexesMap[type]?.lastExternalIndexWithActivity ?: -1

    internal fun setLastExternalIndexWithActivity(type: BipDerivationType, lastExternalIndexWithActivity: Int) {
        if (indexesMap[type]!!.lastExternalIndexWithActivity != lastExternalIndexWithActivity) {
            isDirty = true
            indexesMap[type]!!.lastExternalIndexWithActivity = lastExternalIndexWithActivity
        }
    }

    fun getLastInternalIndexWithActivity(type: BipDerivationType): Int =
            indexesMap[type]?.lastInternalIndexWithActivity ?: -1

    internal fun setLastInternalIndexWithActivity(type: BipDerivationType, lastInternalIndexWithActivity: Int) {
        if (indexesMap[type]!!.lastInternalIndexWithActivity != lastInternalIndexWithActivity) {
            isDirty = true
            indexesMap[type]!!.lastInternalIndexWithActivity = lastInternalIndexWithActivity
        }
    }

    fun getFirstMonitoredInternalIndex(type: BipDerivationType): Int {
        return indexesMap[type]!!.firstMonitoredInternalIndex
    }

    internal fun setFirstMonitoredInternalIndex(type: BipDerivationType, firstMonitoredInternalIndex: Int) {
        if (indexesMap[type]!!.firstMonitoredInternalIndex != firstMonitoredInternalIndex) {
            isDirty = true
            indexesMap[type]!!.firstMonitoredInternalIndex = firstMonitoredInternalIndex
        }
    }

    fun getLastDiscovery(): Long {
        return lastDiscovery
    }

    internal fun setLastDiscovery(lastDiscovery: Long) {
        if (this.lastDiscovery != lastDiscovery) {
            isDirty = true
            this.lastDiscovery = lastDiscovery
        }
    }

    /**
     * Persist this context if it is marked as dirty
     */
    fun persistIfNecessary(backing: Bip44BtcAccountBacking) {
        if (isDirty) {
            persist(backing)
        }
    }

    /**
     * Persist this context
     */
    fun persist(backing: Bip44BtcAccountBacking) {
        backing.updateAccountContext(this)
        isDirty = false
    }

    companion object {
        const val ACCOUNT_TYPE_FROM_MASTERSEED = 0
        const val ACCOUNT_TYPE_UNRELATED_X_PRIV = 1
        const val ACCOUNT_TYPE_UNRELATED_X_PUB = 2
        const val ACCOUNT_TYPE_UNRELATED_X_PUB_EXTERNAL_SIG_TREZOR = 3
        const val ACCOUNT_TYPE_UNRELATED_X_PUB_EXTERNAL_SIG_LEDGER = 4
        const val ACCOUNT_TYPE_UNRELATED_X_PUB_EXTERNAL_SIG_KEEPKEY = 5

        private fun createNewIndexesContexts(derivationTypes: Iterable<BipDerivationType>) =
            derivationTypes.associateWith { AccountIndexesContext(-1, -1, 0) }
                    .toMutableMap()
    }
}
