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
package com.mycelium.wallet.activity.getamount

import android.app.Application
import androidx.lifecycle.*
import com.mycelium.wallet.MbwManager
import com.mycelium.wallet.R
import com.mycelium.wallet.Utils
import com.mycelium.wallet.WalletApplication
import com.mycelium.wallet.activity.util.get
import com.mycelium.wallet.activity.util.toStringWithUnit
import com.mycelium.wapi.wallet.Address
import com.mycelium.wapi.wallet.WalletAccount
import com.mycelium.wapi.wallet.coins.AssetInfo
import com.mycelium.wapi.wallet.coins.Value


class GetAmountViewModel(application: Application) : AndroidViewModel(application) {
    val mbwManager = MbwManager.getInstance(application)
    var account: WalletAccount<Address>? = null
    val maxSpendableAmount = MutableLiveData<Value>()
    val amount = MutableLiveData<Value>()
    val currentCurrency = MutableLiveData<AssetInfo>()

    val maxAmount: LiveData<String> =
            MediatorLiveData<Pair<Value?, AssetInfo?>>().apply {
                addSource(maxSpendableAmount) {
                    value = Pair(it, currentCurrency.value)
                }
                addSource(currentCurrency) {
                    value = Pair(maxSpendableAmount.value, it)
                }
            }.switchMap {
                if (it.first != null && it.second != null) {
                    MutableLiveData(WalletApplication.getInstance().resources.getString(R.string.max_btc,
                            (convert(it.first!!, it.second!!) ?: Value.zeroValue(it.second!!))
                            .toStringWithUnit(mbwManager.getDenomination(account!!.coinType))))
                } else {
                    MutableLiveData("")
                }
            }

    val currencyCurrencyText: LiveData<String> =
            currentCurrency.switchMap {
                mbwManager.currencySwitcher.setCurrency(account!!.coinType, it)
                MutableLiveData(mbwManager.currencySwitcher.getCurrentCurrencyIncludingDenomination(account!!.coinType))
            }

    val howMaxSpendableCalculated: LiveData<Boolean> =
            maxAmount.switchMap {
                MutableLiveData(account?.coinType == Utils.getBtcCoinType() && it.isNotEmpty())
            }

    fun convert(value: Value, assetInfo: AssetInfo): Value? =
            mbwManager.exchangeRateManager.get(mbwManager.getWalletManager(false), value, assetInfo)

}
