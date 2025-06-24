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

import com.mycelium.wallet.exchange.ValueSum

/**
 * Model for the total item on the accounts tab.
 */
class TotalViewModel(val balance: ValueSum) : AccountListItem {
    override fun getType() = AccountListItem.Type.TOTAL_BALANCE_TYPE

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TotalViewModel

        if (balance != other.balance) return false

        return true
    }

    override fun hashCode(): Int {
        return balance.hashCode()
    }
}