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

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mycelium.bequant.kyc.inputPhone.coutrySelector.CountryModel
import com.mycelium.bequant.remote.client.models.KycSaveMobilePhoneRequest


class InputPhoneViewModel : ViewModel() {
    val phoneNumber = MutableLiveData<String>()
    val countryModel = MutableLiveData<CountryModel>()

    fun getRequest(): KycSaveMobilePhoneRequest? {
        if (!isValidMobile(phoneNumber.value)) {
            return null
        }
        return KycSaveMobilePhoneRequest(phoneNumber.value!!, countryModel.value!!.code.toString())
    }

    private fun isValidMobile(phone: String?): Boolean {
        if (phone == null) {
            return false
        }
        return Patterns.PHONE.matcher(phone).matches()
    }
}
