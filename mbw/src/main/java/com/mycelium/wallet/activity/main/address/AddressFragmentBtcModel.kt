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
package com.mycelium.wallet.activity.main.address

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.fragment.app.FragmentActivity
import asShortStringRes
import com.google.common.base.Optional
import com.mrd.bitlib.model.AddressType
import com.mycelium.wallet.MbwManager
import com.mycelium.wallet.R
import com.mycelium.wallet.event.ReceivingAddressChanged
import com.mycelium.wapi.wallet.AddressContainer
import com.mycelium.wapi.wallet.btc.AbstractBtcAccount

class AddressFragmentBtcModel(val app: Application) : AddressFragmentViewModel(app) {
    lateinit var currentType: AddressType
    var nextTypeLabel: MutableLiveData<String> = MutableLiveData()

    override fun init() {
        super.init()
        currentType = model.type.value!!
        setNextLabel()
    }

    override fun qrClickReaction(activity: FragmentActivity) {
        currentType = getNextType()
        setNextLabel()


        (model.account as AddressContainer).setDefaultAddressType(currentType)

        MbwManager.getEventBus().post(ReceivingAddressChanged(model.accountAddress.value!!))
        model.onAddressChange()
    }

    private fun setNextLabel() {
        val nextTypeShort = app.getString(getNextType().asShortStringRes())
        nextTypeLabel.value = app.getString(R.string.tap_next, nextTypeShort)
    }

    private fun getNextType(): AddressType {
        var addressTypes = (model.account as AddressContainer).availableAddressTypes
        // TODO remove after full taproot implementation
//        addressTypes = addressTypes - AddressType.P2TR
        val currentAddressTypeIndex = addressTypes.lastIndexOf(currentType)
        return addressTypes[(currentAddressTypeIndex + 1) % addressTypes.size]
    }
}
