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
package com.mycelium.bequant.receive.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycelium.bequant.remote.repositories.Api
import com.mycelium.wallet.MbwManager
import com.mycelium.wallet.WalletApplication


class FromMyceliumViewModel : ViewModel() {
    val oneCoinFiatRate = MutableLiveData<String>()
    val custodialBalance = MutableLiveData<String?>()
    val amount = MutableLiveData<String>()
    val amountFiat = MutableLiveData<String>()
    val address = MutableLiveData<String>()

    fun hasOneCoinFiatRate() = oneCoinFiatRate.value != null && oneCoinFiatRate.value?.isNotEmpty() == true

    val mbwManager = MbwManager.getInstance(WalletApplication.getInstance())
    fun getCryptocurrenciesSymbols(): List<String> {
        return mbwManager.getWalletManager(false).getCryptocurrenciesSymbols().map { it.removePrefix("t") }
    }

    fun loadBalance(currency:String, finally: () -> Unit) {
        Api.accountRepository.accountBalanceGet(viewModelScope, success = {
            val find = it?.find { it.currency == currency }
            custodialBalance.value = find?.available
        }, error = { _, message->

        }, finally = finally)
    }
}