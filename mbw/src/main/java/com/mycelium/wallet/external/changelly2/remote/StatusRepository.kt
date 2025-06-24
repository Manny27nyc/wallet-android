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
package com.mycelium.wallet.external.changelly2.remote

import android.app.Activity
import androidx.core.content.edit
import com.mycelium.bequant.remote.model.UserStatus
import com.mycelium.wallet.WalletApplication
import com.mycelium.wallet.external.vip.VipRetrofitFactory
import com.mycelium.wallet.external.vip.model.ActivateVipRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StatusRepository {
    private val vipApi by lazy { VipRetrofitFactory().createApi() }
    private val preference = WalletApplication.getInstance()
        .getSharedPreferences(PREFERENCES_VIP_FILE, Activity.MODE_PRIVATE)

    private fun getLocalStatus() = UserStatus.fromName(preference.getString(VIP_STATUS_KEY, null))

    private val _statusFlow = MutableStateFlow(UserStatus.REGULAR)
    val statusFlow = _statusFlow.asStateFlow()

    init {
        val localStatus = getLocalStatus()
        if (localStatus != null) {
            _statusFlow.value = localStatus
        } else {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val checkResult = vipApi.check()
                    // if user is VIP than response contains his code else response contains empty string
                    val isVIP = checkResult.vipCode.isNotEmpty()
                    val status = if (isVIP) UserStatus.VIP else UserStatus.REGULAR
                    preference.edit { putString(VIP_STATUS_KEY, status.name) }
                    _statusFlow.value = status
                } catch (_: Exception) {
                }
            }
        }
    }

    suspend fun applyVIPCode(code: String): UserStatus {
        val response = vipApi.activate(ActivateVipRequest(code))
        val status = if (response.done) UserStatus.VIP else UserStatus.REGULAR
        _statusFlow.value = status
        preference.edit { putString(VIP_STATUS_KEY, status.name) }
        return status
    }

    fun dropStatus() {
        preference.edit { putString(VIP_STATUS_KEY, UserStatus.REGULAR.name) }
        _statusFlow.value = UserStatus.REGULAR
    }

    private companion object {
        const val PREFERENCES_VIP_FILE = "VIP_PREFERENCES"
        const val VIP_STATUS_KEY = "VIP_STATUS"
    }
}
