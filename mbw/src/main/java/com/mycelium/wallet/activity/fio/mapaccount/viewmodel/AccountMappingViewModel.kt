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
import com.mycelium.wapi.wallet.fio.FioAccount
import com.mycelium.wapi.wallet.fio.RegisteredFIOName
import java.text.SimpleDateFormat
import java.util.*


class AccountMappingViewModel : ViewModel() {
    val fioAccount = MutableLiveData<FioAccount>()
    val fioName = MutableLiveData<RegisteredFIOName>()
    val acknowledge = MutableLiveData<Boolean>(false)
    val fewTransactionsLeft = MutableLiveData<Boolean>()
    private val shouldRenew = MutableLiveData<Boolean>()

    fun dateToString(date: Date) = DATE_FORMAT.format(date)!!

    fun intToString(int: Int) = int.toString()

    fun soonExpiring() = EXPIRATION_WARN_DATE.after(fioName.value?.expireDate)

    fun update() {
        val fewTransactions = fioName.value?.bundledTxsNum ?: 0 < 10
        fewTransactionsLeft.postValue(fewTransactions)
        shouldRenew.postValue(fewTransactions || soonExpiring())
    }

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("MMMM dd, yyyy\nK:mm a")
        private val EXPIRATION_WARN_DATE = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 30) }
    }
}