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
package com.mycelium.wapi.wallet.fio

import com.mycelium.generated.wallet.database.FioContext
import com.mycelium.wapi.wallet.btc.bip44.HDAccountContext
import com.mycelium.wapi.wallet.coins.Balance
import com.mycelium.wapi.wallet.coins.CryptoCurrency
import com.mycelium.wapi.wallet.genericdb.AccountContextImpl
import java.math.BigInteger
import java.util.*

class FioAccountContext(uuid: UUID,
                        currency: CryptoCurrency,
                        accountName: String,
                        balance: Balance,
                        val listener: (FioAccountContext) -> Unit,
                        val accountIndex: Int,
                        registeredFIONames: List<RegisteredFIOName>? = null,
                        registeredFIODomains: List<FIODomain>? = null,
                        archived: Boolean = false,
                        blockHeight: Int = 0,
                        val accountType: Int = ACCOUNT_TYPE_FROM_MASTERSEED,
                        actionSequenceNumber: BigInteger = BigInteger.ZERO) :
        AccountContextImpl(uuid, currency, accountName, balance, archived, blockHeight) {

    fun fioContext() = FioContext(uuid, accountIndex, accountType, actionSequenceNumber, registeredFIONames, registeredFIODomains)

    override fun onChange() {
        listener(this)
    }

    var actionSequenceNumber = actionSequenceNumber
        set(value) {
            field = value
            onChange()
        }

    var registeredFIONames = registeredFIONames
        set(value) {
            field = value
            onChange()
        }

    var registeredFIODomains = registeredFIODomains
        set(value) {
            field = value
            onChange()
        }

    companion object {
        const val ACCOUNT_TYPE_FROM_MASTERSEED = 0
        const val ACCOUNT_TYPE_UNRELATED_X_PRIV = 1
        const val ACCOUNT_TYPE_UNRELATED_X_PUB = 2
    }
}