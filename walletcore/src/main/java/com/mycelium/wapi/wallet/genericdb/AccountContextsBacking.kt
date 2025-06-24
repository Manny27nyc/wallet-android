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
package com.mycelium.wapi.wallet.genericdb

import com.mycelium.generated.wallet.database.AccountContext
import com.mycelium.generated.wallet.database.WalletDB
import java.util.*

open class AccountContextsBacking(walletDB: WalletDB) : Backing<AccountContext> {
    private val queries = walletDB.accountContextQueries

    override fun loadAccountContexts() = queries.selectAll()
            .executeAsList()

    override fun loadAccountContext(accountId: UUID) = queries.selectByUUID(accountId)
            .executeAsOneOrNull()

    override fun createAccountContext(accountId: UUID, context: AccountContext) {
        queries.insertFullObject(context)
    }

    override fun updateAccountContext(accountId: UUID, context: AccountContext) {
        queries.update(context.accountName, context.balance, context.archived, context.blockHeight, context.uuid)
    }

    override fun deleteAccountContext(uuid: UUID) {
        queries.delete(uuid)
    }
}