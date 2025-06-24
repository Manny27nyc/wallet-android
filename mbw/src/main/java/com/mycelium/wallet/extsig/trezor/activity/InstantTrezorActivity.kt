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
package com.mycelium.wallet.extsig.trezor.activity

import android.app.Activity
import android.content.Intent
import com.mycelium.wallet.MbwManager
import com.mycelium.wallet.R
import com.mycelium.wallet.databinding.ActivityInstantExtSigBinding
import com.mycelium.wallet.extsig.common.ExternalSignatureDeviceManager.OnPinMatrixRequest
import com.mycelium.wallet.extsig.common.activity.InstantExtSigActivity
import com.mycelium.wallet.extsig.trezor.TrezorManager
import com.mycelium.wapi.wallet.AccountScanManager.OnAccountFound
import com.mycelium.wapi.wallet.AccountScanManager.OnPassphraseRequest
import com.mycelium.wapi.wallet.AccountScanManager.OnScanError
import com.mycelium.wapi.wallet.AccountScanManager.OnStatusChanged
import com.squareup.otto.Subscribe

class InstantTrezorActivity : InstantExtSigActivity<TrezorManager>() {
    override fun initMasterseedManager(): TrezorManager =
        MbwManager.getInstance(this).trezorManager

    override fun getFirmwareUpdateDescription(): String =
        getString(R.string.trezor_new_firmware_description)

    lateinit var binding: ActivityInstantExtSigBinding

    override fun setView() {
        setContentView(ActivityInstantExtSigBinding.inflate(layoutInflater).apply {
            binding = this
        }.root)
        binding.ivConnectExtSig.setImageResource(R.drawable.connect_trezor)
        binding.tvCaption.setText(R.string.trezor_cold_storage_header)
        binding.tvDeviceType.setText(R.string.trezor_name)
    }

    // Otto.EventBus does not traverse class hierarchy to find subscribers
    @Subscribe
    override fun onPinMatrixRequest(event: OnPinMatrixRequest?) {
        super.onPinMatrixRequest(event)
    }

    @Subscribe
    override fun onScanError(event: OnScanError) {
        super.onScanError(event)
    }

    @Subscribe
    override fun onStatusChanged(event: OnStatusChanged?) {
        super.onStatusChanged(event)
    }

    @Subscribe
    override fun onAccountFound(event: OnAccountFound) {
        super.onAccountFound(event)
    }

    @Subscribe
    override fun onPassphraseRequest(event: OnPassphraseRequest?) {
        super.onPassphraseRequest(event)
    }

    companion object {
        fun callMe(currentActivity: Activity, requestCode: Int) {
            currentActivity.selectCoin {
                currentActivity.startActivityForResult(
                    Intent(currentActivity, InstantTrezorActivity::class.java), requestCode
                )
            }
        }
    }
}
