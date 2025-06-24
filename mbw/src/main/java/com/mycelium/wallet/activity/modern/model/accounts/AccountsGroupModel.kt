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

import android.content.Context
import com.mycelium.wallet.exchange.ValueSum
import com.mycelium.wapi.wallet.coins.CryptoCurrency

/**
 * Model for the accounts group on the accounts tab.
 */
class AccountsGroupModel(val titleId: Int, private val groupType: AccountListItem.Type,
                         val sum: ValueSum? = null,
                         val accountsList: List<AccountListItem>, val coinType: CryptoCurrency,
                         val isInvestmentAccount: Boolean) : AccountListItem {

    constructor(model: AccountsGroupModel) : this(model.titleId, model.groupType, model.sum,
            model.accountsList, model.coinType, model.isInvestmentAccount)

    var isCollapsed = false // Is only used to handle state between updates.

    /**
     * @param context - context must be passed, as with language change title might change.
     */
    fun getTitle(context: Context): String = context.getString(titleId)

    override fun getType(): AccountListItem.Type = groupType

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AccountsGroupModel

        if (isCollapsed != other.isCollapsed) return false
        if (titleId != other.titleId) return false
        if (groupType != other.groupType) return false
        if (accountsList != other.accountsList) return false

        return true
    }

    override fun hashCode(): Int {
        var result = titleId
        result = 31 * result + groupType.hashCode()
        result = 31 * result + accountsList.hashCode()
        return result
    }

}