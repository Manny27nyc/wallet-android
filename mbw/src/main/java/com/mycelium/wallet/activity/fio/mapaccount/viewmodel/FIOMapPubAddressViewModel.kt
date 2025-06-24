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
package com.mycelium.wallet.activity.fio.mapaccount.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mycelium.wallet.activity.fio.mapaccount.Mode
import com.mycelium.wapi.wallet.WalletAccount
import com.mycelium.wapi.wallet.fio.FioAccount
import java.text.SimpleDateFormat
import java.util.*

class FIOMapPubAddressViewModel : ViewModel() {
    val accountList = MutableLiveData<List<FioAccount>>()
    val fioAddress = MutableLiveData<String>("")
    val fioNameExpireDate = MutableLiveData<Date>(Date())
    val mode = MutableLiveData<Mode>(Mode.LIST)
    val extraAccount = MutableLiveData<WalletAccount<*>>()

    val DATE_FORMAT = SimpleDateFormat("MMMM dd, yyyy\nK:mm a")

    fun dateToString(date: Date) = DATE_FORMAT.format(date)
}