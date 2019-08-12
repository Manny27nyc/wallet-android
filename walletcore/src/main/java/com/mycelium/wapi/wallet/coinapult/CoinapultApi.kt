package com.mycelium.wapi.wallet.coinapult

import com.mycelium.wapi.wallet.GenericAddress
import com.mycelium.wapi.wallet.GenericTransactionSummary
import com.mycelium.wapi.wallet.btc.BtcAddress
import com.mycelium.wapi.wallet.coins.Balance
import java.math.BigDecimal


interface CoinapultApi {
    fun getTransactions(currency: Currency): List<GenericTransactionSummary>?
    fun getBalance(currency: Currency): Balance?
    fun getAddress(currency: Currency, currentAddress: GenericAddress?): GenericAddress?
    fun broadcast(amount: BigDecimal, currency: Currency, address: BtcAddress)
    fun setMail(mail: String): Boolean
    fun verifyMail(link: String, email: String): Boolean
}