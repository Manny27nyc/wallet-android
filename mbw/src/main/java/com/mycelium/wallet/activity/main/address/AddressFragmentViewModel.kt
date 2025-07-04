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
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.mycelium.wallet.MbwManager
import com.mycelium.wallet.R
import com.mycelium.wallet.Utils
import com.mycelium.wallet.WalletApplication
import com.mycelium.wallet.activity.modern.Toaster
import com.mycelium.wapi.wallet.WalletAccount
import com.mycelium.wapi.wallet.btc.AbstractBtcAccount
import com.mycelium.wapi.wallet.btcvault.AbstractBtcvAccount

abstract class AddressFragmentViewModel(val context: Application) : AndroidViewModel(context) {
    protected val mbwManager = MbwManager.getInstance(context)
    protected lateinit var model: AddressFragmentModel
    protected val showBip44Path: Boolean = mbwManager.metadataStorage.showBip44Path

    open fun init() {
        if (::model.isInitialized) {
            throw IllegalStateException("This method should be called only once.")
        }
        model = AddressFragmentModel(context, mbwManager.selectedAccount, showBip44Path)
    }

    fun getAccountLabel() = model.accountLabel
    fun isSyncError() = model.isSyncError
    fun getAccountAddress() = model.accountAddress
    fun getAddressPath() = model.addressPath
    fun isCompressedKey() = model.isCompressedKey
    fun getType() = model.type
    fun getAccountAddressType() = model.accountAddressType
    fun getAccount() = model.account
    fun getRegisteredFIONames() = model.registeredFIONames

    fun getDrawableForAccount(resources: Resources): Drawable? =
        Utils.getDrawableForAccount(model.account, true, resources)

    override fun onCleared() {
        model.onCleared()
    }

    fun addressClick() {
        Utils.setClipboardString(getAddressString(), context)
        Toaster(context).toast(R.string.copied_to_clipboard, true)
    }

    fun getAddressString(): String? = getAccountAddress().value?.toString()

    fun isLabelNullOrEmpty() =
        (getAccountLabel().value == null || getAccountLabel().value!!.toString() == "")

    abstract fun qrClickReaction(activity: FragmentActivity)

    fun isInitialized() = ::model.isInitialized

    companion object {
        private val mbwManager = MbwManager.getInstance(WalletApplication.getInstance())
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                if (accountSupportsMultipleBtcReceiveAddresses(mbwManager.selectedAccount)) {
                    AddressFragmentBtcModel(WalletApplication.getInstance()) as T
                } else {
                    AddressFragmentCoinsModel(WalletApplication.getInstance()) as T
                }
        }

        fun accountSupportsMultipleBtcReceiveAddresses(account: WalletAccount<*>): Boolean =
            (account is AbstractBtcAccount && account.availableAddressTypes.size > 1)
                    || (account is AbstractBtcvAccount && account.availableAddressTypes.size > 1)

    }
}