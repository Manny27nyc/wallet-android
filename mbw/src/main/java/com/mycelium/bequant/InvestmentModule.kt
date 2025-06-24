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
package com.mycelium.bequant

import com.mycelium.wapi.wallet.KeyCipher
import com.mycelium.wapi.wallet.WalletAccount
import com.mycelium.wapi.wallet.WalletManager
import com.mycelium.wapi.wallet.manager.Config
import com.mycelium.wapi.wallet.manager.WalletModule
import com.mycelium.wapi.wallet.metadata.IMetaDataStorage
import java.util.*


class InvestmentModule(metaDataStorage: IMetaDataStorage) : WalletModule(metaDataStorage) {
    override val id = ID
    val account = InvestmentAccount()

    override fun createAccount(config: Config): WalletAccount<*> {
        TODO("Not yet implemented")
    }

    override fun loadAccounts(): Map<UUID, WalletAccount<*>> =
            mapOf<UUID, WalletAccount<*>>(account.id to account)

    override fun canCreateAccount(config: Config): Boolean = false

    override fun deleteAccount(walletAccount: WalletAccount<*>, keyCipher: KeyCipher): Boolean = false

    override fun getAccounts(): List<WalletAccount<*>> = listOf(account)

    override fun getAccountById(id: UUID): WalletAccount<*> = account

    companion object {
        const val ID: String = "BEQUANT_MODULE"
    }
}

fun WalletManager.getInvestmentAccounts() = getAccounts().filter { it is InvestmentAccount && it.isVisible() }