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
package com.mycelium.bequant.market.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycelium.bequant.remote.repositories.Api
import com.mycelium.bequant.remote.trading.model.Balance


class AccountViewModel : ViewModel() {
    val searchMode = MutableLiveData(false)
    val privateMode = MutableLiveData(false)
    val totalBalance = MutableLiveData<String>()
    val totalBalanceFiat = MutableLiveData<String>()

    val tradingBalances = MutableLiveData<Array<Balance>>()
    val accountBalances = MutableLiveData<Array<Balance>>()
}