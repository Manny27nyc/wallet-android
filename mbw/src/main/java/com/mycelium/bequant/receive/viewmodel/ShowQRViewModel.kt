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
import com.mycelium.bequant.remote.trading.model.Address
import com.mycelium.bequant.remote.repositories.Api


class ShowQRViewModel : ViewModel() {

    fun createDepositAddress(currency:String, success: (Address?) -> Unit, error: (Int, String) -> Unit, finally: () -> Unit) {
        Api.accountRepository.cryptoAddressCurrencyPost(viewModelScope, currency, success = success, error = error, finally = finally)
    }

    val addressLabel = MutableLiveData<String>()
    val tagLabel = MutableLiveData<String>()
}