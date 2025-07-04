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
import com.mycelium.bequant.BequantPreference.getLastKnownBalance
import com.mycelium.bequant.remote.trading.model.Balance
import com.mycelium.wallet.Utils
import com.mycelium.wapi.wallet.coins.Value


class ExchangeViewModel : ViewModel() {
    val available = MutableLiveData<Value>(getLastKnownBalance())
    val youSend = MutableLiveData(Value.valueOf(Utils.getBtcCoinType(), available.value!!.value))
    val youGet = MutableLiveData(Value.valueOf(Utils.getEthCoinType(), 0))
    val rate = MutableLiveData("")
    val accountBalances = MutableLiveData<Array<Balance>>()
    val tradingBalances = MutableLiveData<Array<Balance>>()
    val isEnoughFundsIncludingFees = MutableLiveData<Boolean>(true)
    val isExchangeEnabled = MutableLiveData<Boolean>(true)
    val youSendText = MutableLiveData<String>()
    val youGetText = MutableLiveData<String>()
}