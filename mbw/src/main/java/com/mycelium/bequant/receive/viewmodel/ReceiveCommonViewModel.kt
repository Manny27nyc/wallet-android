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
import com.mycelium.wallet.Utils
import java.io.Serializable


class ReceiveCommonViewModel : ViewModel(), Serializable {
    fun fetchDepositAddress(finally: () -> Unit) {
        currency.value?.let { currencySymbol ->
            Api.accountRepository.cryptoAddressCurrencyGet(
                    viewModelScope,
                    currencySymbol,
                    {
                        address.value = it?.address
                        tag.value = it?.paymentId
                    },
                    { _, message ->
                        error.value = message
                    },
                    finally)
        }
    }

    val error = MutableLiveData<String>()
    val address = MutableLiveData<String>()
    val tag = MutableLiveData<String?>()
    var currency = MutableLiveData(Utils.getBtcCoinType().symbol)
}