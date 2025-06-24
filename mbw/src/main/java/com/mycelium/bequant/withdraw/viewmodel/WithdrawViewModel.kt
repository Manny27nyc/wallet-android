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
package com.mycelium.bequant.withdraw.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycelium.bequant.remote.repositories.Api
import com.mycelium.bequant.remote.trading.model.Balance
import com.mycelium.bequant.remote.trading.model.InlineResponse200
import com.mycelium.wallet.Utils
import java.math.BigDecimal


class WithdrawViewModel() : ViewModel() {
    val currency = MutableLiveData(Utils.getBtcCoinType().symbol)
    val custodialBalance = MutableLiveData<String>()
    val amount = MutableLiveData<String>()
    val address = MutableLiveData<String>()

    var accountBalance = MutableLiveData<BigDecimal>(BigDecimal.ZERO)
    var tradingBalance = MutableLiveData<BigDecimal>(BigDecimal.ZERO)

    fun loadBalance(success: (Array<Balance>?, Array<Balance>?) -> Unit, error: (Int, String) -> Unit, finally: () -> Unit) {
        Api.accountRepository.accountBalanceGet(
                viewModelScope,
                success = { accountBalances ->
                    Api.tradingRepository.tradingBalanceGet(
                            viewModelScope,
                            success = { tradingBalances ->
                                success(accountBalances, tradingBalances)
                            },
                            error = error)
                },
                error = error,
                finally = finally)
    }

    fun withdraw(success: (InlineResponse200?) -> Unit, error: (Int, String) -> Unit, finally: () -> Unit) {
        Api.accountRepository.accountCryptoWithdrawPost(
                scope = viewModelScope,
                currency = currency.value!!,
                amount = amount.value!!,
                address = address.value!!, success = success, error = error, finally = finally
        )
    }
}