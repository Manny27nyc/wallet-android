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


class DocumentViewModel : ViewModel() {
    val identityCount = MutableLiveData<Int>()
    val poaCount = MutableLiveData<Int>()
    val selfieCount = MutableLiveData<Int>()
    val nextButton = MutableLiveData<Boolean>()

    fun isValid() =
            identityCount.value ?: 0 > 0 &&
                    poaCount.value ?: 0 > 0 &&
                    selfieCount.value ?: 0 > 0
}