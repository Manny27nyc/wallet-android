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
package com.mycelium.bequant.kyc.steps.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mycelium.bequant.remote.model.KYCRequest


class Step2ViewModel : ViewModel() {
    val addressLine1 = MutableLiveData<String?>()
    val addressLine2 = MutableLiveData<String?>()
    val city = MutableLiveData<String?>()
    val postcode = MutableLiveData<String?>()
    val country = MutableLiveData<String>()
    val countryAcronym = MutableLiveData<String?>()
    val nextButton = MutableLiveData<Boolean>()

    fun fromModel(kyc: KYCRequest) {
        addressLine1.value = kyc.address_1
        addressLine2.value = kyc.address_2
        city.value = kyc.city
        postcode.value = kyc.zip
        countryAcronym.value = kyc.country
    }

    fun fillModel(kyc: KYCRequest) {
        kyc.address_1 = addressLine1.value
        kyc.address_2 = addressLine2.value
        kyc.city = city.value
        kyc.zip = postcode.value
        kyc.country = countryAcronym.value
    }

    fun isValid(): Boolean = listOf(addressLine1, city, postcode, country).all {
        it.value?.trim()?.isNotEmpty() == true
    }
}