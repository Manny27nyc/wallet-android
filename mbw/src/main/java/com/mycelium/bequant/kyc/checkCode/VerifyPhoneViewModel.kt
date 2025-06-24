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
package com.mycelium.bequant.kyc.checkCode

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mycelium.bequant.remote.client.models.KycCheckMobilePhoneRequest


class VerifyPhoneViewModel : ViewModel() {
    val code = MutableLiveData<String>()

    fun fillModel(): KycCheckMobilePhoneRequest? {
        val toInt = code.value?.toInt()
        toInt?.let {
            return@fillModel KycCheckMobilePhoneRequest(it)
        }
        return null
    }
}