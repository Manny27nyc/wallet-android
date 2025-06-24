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
package com.mycelium.wapi.wallet.erc20

import com.mycelium.generated.wallet.database.AccountContext
import com.mycelium.generated.wallet.database.WalletDB
import com.mycelium.wapi.wallet.coins.Balance
import com.mycelium.wapi.wallet.coins.CryptoCurrency
import com.mycelium.wapi.wallet.genericdb.Backing
import java.math.BigInteger
import java.util.*

open class ERC20Backing(walletDB: WalletDB, private val generalBacking: Backing<AccountContext>)
    : Backing<ERC20AccountContext> {
    private val erc20Queries = walletDB.eRC20ContextQueries

    private fun migrateSymbols(symbol: String): String =
            when (symbol) {
                "USDT" -> "USDT20"
                else -> symbol
            }

    val accountMapper = { uuid: UUID,
                          currency: CryptoCurrency,
                          accountName: String,
                          archived: Boolean,
                          balance: Balance,
                          blockHeight: Int,
                          nonce: BigInteger,
                          contractAddress: String,
                          unitExponent: Int,
                          symbol: String,
                          ethAccountId: UUID ->
        ERC20AccountContext(uuid, currency, accountName, balance,
            { updateAccountContext(it.uuid, it) },
                contractAddress, migrateSymbols(symbol), unitExponent, ethAccountId, archived, blockHeight, nonce)
    }

    override fun loadAccountContexts() =
            erc20Queries.selectAllERC20Contexts(accountMapper)
                    .executeAsList()

    override fun loadAccountContext(accountId: UUID) =
            erc20Queries.selectERC20ContextByUUID(accountId, accountMapper)
                    .executeAsOneOrNull()

    override fun createAccountContext(accountId: UUID, context: ERC20AccountContext) {
        generalBacking.createAccountContext(accountId, context.accountContext())
        erc20Queries.insert(context.uuid, context.nonce, context.contractAddress, context.unitExponent, context.symbol, context.ethAccountId)
    }

    override fun updateAccountContext(accountId: UUID, context: ERC20AccountContext) {
        generalBacking.updateAccountContext(accountId, context.accountContext())
        erc20Queries.update(context.nonce, context.uuid)
    }

    override fun deleteAccountContext(uuid: UUID) {
        generalBacking.deleteAccountContext(uuid)
    }
}