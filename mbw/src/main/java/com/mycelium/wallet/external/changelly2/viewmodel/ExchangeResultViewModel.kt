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
package com.mycelium.wallet.external.changelly2.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.mycelium.wallet.MbwManager
import com.mycelium.wallet.R
import com.mycelium.wallet.WalletApplication
import com.mycelium.wallet.activity.util.toStringFriendlyWithUnit
import com.mycelium.wallet.external.changelly.model.ChangellyTransaction
import java.math.BigDecimal
import java.text.DateFormat


class ExchangeResultViewModel : ViewModel() {
    val mbwManager = MbwManager.getInstance(WalletApplication.getInstance())
    val spendValue = MutableLiveData<String>()
    val spendValueFiat = MutableLiveData<String>()
    val getValue = MutableLiveData<String>()
    val getValueFiat = MutableLiveData<String>()
    val txId = MutableLiveData<String>()
    val date = MutableLiveData<String>()
    val fromAddress = MutableLiveData("")
    val toAddress = MutableLiveData("")
    val trackLink = MutableLiveData("")
    val trackLinkText = MutableLiveData("")
    val trackInProgress = MutableLiveData(false)
    val isExchangeComplete = MutableLiveData(false)

    val more = MutableLiveData(true)
    val moreText = more.map {
        WalletApplication.getInstance().getString(
                if (it) {
                    R.string.show_transaction_details
                } else {
                    R.string.show_transaction_details_hide
                })
    }

    fun setTransaction(result: ChangellyTransaction) {
        txId.value = result.id
        spendValue.value = "${result.amountExpectedFrom} ${result.currencyFrom.toUpperCase()}"
        getValue.value = "${result.amountExpectedTo} ${result.currencyTo.toUpperCase()}"
        date.value = DateFormat.getDateInstance(DateFormat.LONG).format(result.createdAt)
        spendValueFiat.value = getFiatValue(result.amountExpectedFrom, result.currencyFrom)
        getValueFiat.value = getFiatValue(result.amountExpectedTo, result.currencyTo)
        isExchangeComplete.value = result.status == "finished"
        trackInProgress.value = result.status !in LINK_TEXT_LIST
        trackLinkText.value =
                if (!trackInProgress.value!!) WalletApplication.getInstance().getString(R.string.link_to_track_transaction)
                else WalletApplication.getInstance().getString(R.string.exchange_in_progress)

    }

    private fun getFiatValue(amount: BigDecimal?, currency: String) =
            mbwManager.getWalletManager(false).getAssetTypes()
                    .firstOrNull { it.symbol.equals(currency, true) }
                    ?.let {
                        amount?.let { amount ->
                            mbwManager.exchangeRateManager
                                    .get(it.value(amount.toPlainString()), mbwManager.getFiatCurrency(it))
                                    ?.toStringFriendlyWithUnit()?.let { "≈$it" }
                        }
                    } ?: ""

    companion object {
        val LINK_ACTIVE_LIST = arrayOf("finished", "refunded", "failed", "expired", "hold", "sending", "exchanging")
        val LINK_TEXT_LIST = arrayOf("finished", "refunded", "failed", "expired", "hold")
    }
}