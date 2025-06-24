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
import com.mycelium.wapi.wallet.coins.Balance
import com.mycelium.wapi.wallet.coins.CryptoCurrency
import java.util.*

abstract class AccountContextImpl(val uuid: UUID,
                                  val currency: CryptoCurrency,
                                  accountName: String,
                                  balance: Balance,
                                  archived: Boolean = false,
                                  blockHeight: Int = 0) {

    fun accountContext() =
        AccountContext(uuid, currency, accountName, balance, archived, blockHeight)

    abstract fun onChange()

    open var archived = archived
        set(value) {
            field = value
            onChange()
        }

    var accountName = accountName
        set(value) {
            field = value
            onChange()
        }

    var balance = balance
        set(value) {
            field = value
            onChange()
        }

    var blockHeight = blockHeight
        set(value) {
            field = value
            onChange()
        }
}

