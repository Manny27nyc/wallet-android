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
package com.mycelium.wallet.activity.modern.model.accounts

import com.mycelium.wapi.SyncStatus
import com.mycelium.wapi.wallet.Address
import com.mycelium.wapi.wallet.WalletAccount


class AccountInvestmentViewModel(val account: WalletAccount<out Address>, val balance: String) : AccountListItem, SyncStatusItem {
    val accountId = account.id!!
    var label = "Trading Account"
    override var isSyncError = account.lastSyncStatus()?.status == SyncStatus.ERROR

    override fun getType(): AccountListItem.Type = AccountListItem.Type.INVESTMENT_TYPE
}