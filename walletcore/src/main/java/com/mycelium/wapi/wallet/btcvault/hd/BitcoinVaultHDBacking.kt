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
package com.mycelium.wapi.wallet.btcvault.hd

import com.mrd.bitlib.crypto.BipDerivationType
import com.mrd.bitlib.model.AddressType
import com.mycelium.generated.wallet.database.AccountContext
import com.mycelium.generated.wallet.database.WalletDB
import com.mycelium.wapi.wallet.AccountIndexesContext
import com.mycelium.wapi.wallet.genericdb.Backing
import java.util.*

class BitcoinVaultHDBacking(walletDB: WalletDB,
                            private val generalBacking: Backing<AccountContext>)
    : Backing<BitcoinVaultHDAccountContext> {

    private val btcvQueries = walletDB.bTCVContextQueries

    override fun loadAccountContexts(): List<BitcoinVaultHDAccountContext> =
            btcvQueries.selectAllBTCVContexts { uuid, currency, accountName, archived, balance, blockHeight, accountIndex,
                                                indexContexts: Map<BipDerivationType, AccountIndexesContext>?,
                                                lastDiscovery: Long?,
                                                accountType: Int?,
                                                accountSubId: Int?,
                                                addressType: AddressType? ->
                BitcoinVaultHDAccountContext(uuid, currency, accountIndex, archived, accountName,
                    balance, { updateAccountContext(it.uuid, it) }, blockHeight,
                        lastDiscovery ?: 0,
                        indexContexts ?: mapOf(), accountType ?: 0,
                        accountSubId ?: 0, addressType ?: AddressType.P2SH_P2WPKH)
            }.executeAsList()

    override fun loadAccountContext(accountId: UUID): BitcoinVaultHDAccountContext? =
            btcvQueries.selectBTCVContextByUUID(accountId, mapper = { uuid, currency, accountName, archived, balance, blockHeight, accountIndex,
                                                                      indexContexts: Map<BipDerivationType, AccountIndexesContext>?,
                                                                      lastDiscovery: Long?,
                                                                      accountType: Int?,
                                                                      accountSubId: Int?,
                                                                      addressType: AddressType? ->
                BitcoinVaultHDAccountContext(uuid, currency, accountIndex, archived, accountName,
                    balance, { updateAccountContext(it.uuid, it) }, blockHeight,
                        lastDiscovery ?: 0,
                        indexContexts ?: mapOf(), accountType ?: 0,
                        accountSubId ?: 0, addressType ?: AddressType.P2SH_P2WPKH)
            }).executeAsOne()

    override fun createAccountContext(accountId: UUID, context: BitcoinVaultHDAccountContext) {
        generalBacking.createAccountContext(accountId, context.accountContext())
        btcvQueries.insert(context.id, context.accountIndex)
    }

    override fun updateAccountContext(accountId: UUID,context: BitcoinVaultHDAccountContext) {
        generalBacking.updateAccountContext(accountId, context.accountContext())
        btcvQueries.update(context.indexesMap, context.getLastDiscovery(), context.accountType, context.accountSubId, context.defaultAddressType, context.uuid)
    }

    override fun deleteAccountContext(uuid: UUID) {
        generalBacking.deleteAccountContext(uuid)
    }
}