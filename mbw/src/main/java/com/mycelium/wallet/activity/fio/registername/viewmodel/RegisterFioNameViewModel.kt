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
package com.mycelium.wallet.activity.fio.registername.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycelium.wapi.wallet.WalletAccount
import com.mycelium.wapi.wallet.coins.Value
import com.mycelium.wapi.wallet.fio.FIODomain
import com.mycelium.wapi.wallet.fio.FioAccount
import com.mycelium.wapi.wallet.fio.FioModule
import fiofoundation.io.fiosdk.errors.FIOError
import fiofoundation.io.fiosdk.errors.fionetworkprovider.PushTransactionError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

class RegisterFioNameViewModel : ViewModel() {
    val fioAccountToRegisterName = MutableLiveData<FioAccount>()
    val accountToPayFeeFrom = MutableLiveData<WalletAccount<*>>()
    val registrationFee = MutableLiveData<Value>()
    val address = MutableLiveData<String>("")
    val domain = MutableLiveData<FIODomain>(DEFAULT_DOMAIN)
    val addressWithDomain = MutableLiveData<String>()
    val isFioAddressAvailable = MutableLiveData<Boolean>(true)
    val isFioAddressValid = MutableLiveData<Boolean>(true)
    val isFioServiceAvailable = MutableLiveData<Boolean>(true)
    val isRenew = MutableLiveData(false)

    fun registerName(fioModule: FioModule, doOnSuccess: (String) -> Unit, doOnError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val expiration = fioAccountToRegisterName.value!!.registerFIOAddress(addressWithDomain.value!!)!!
                withContext(Dispatchers.Main) {
                    doOnSuccess(expiration)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Logger.getLogger(RegisterFioNameViewModel::class.simpleName).log(Level.WARNING, "failed to register fio name: ${e.localizedMessage}")
                    if (e is FIOError) {
                        fioModule.addFioServerLog(e.toJson())
                    }
                    doOnError("${(e.cause?.cause?.cause as? PushTransactionError)?.responseError?.fields?.firstOrNull()?.error ?: e.message}")
                }
            }
        }
    }

    companion object {
        val DEFAULT_DOMAIN = FIODomain("mycelium", Date(), true)
    }
}