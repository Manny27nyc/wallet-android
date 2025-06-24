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
package com.mycelium.giftbox.cards.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.mycelium.bequant.kyc.inputPhone.coutrySelector.CountryModel
import com.mycelium.giftbox.GiftboxPreference
import com.mycelium.wallet.R


class GiftBoxViewModel(application: Application) : AndroidViewModel(application) {
    val selectedCountries = MutableLiveData<List<CountryModel>>(GiftboxPreference.selectedCountries())
    val countries = MutableLiveData<List<CountryModel>>(emptyList())
    val categories = MutableLiveData<List<String>>(emptyList())
    val currentTab = MutableLiveData<String>()

    val orderLoading = MutableLiveData<Boolean>()
    var reloadStore = false

    fun currentCountries(): LiveData<String> =
            selectedCountries.switchMap {
                GiftboxPreference.setSelectedCountries(it)
                return@switchMap MutableLiveData<String>(when (it.size) {
                    0 -> getApplication<Application>().getString(R.string.all_countries)
                    1 -> it[0].name
                    else -> getApplication<Application>().resources.getQuantityString(R.plurals.d_countries, it.size, it.size)
                })
            }
}