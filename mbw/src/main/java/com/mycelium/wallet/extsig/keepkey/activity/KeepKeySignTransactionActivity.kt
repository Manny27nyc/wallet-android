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
package com.mycelium.wallet.extsig.keepkey.activity

import com.mycelium.wallet.MbwManager
import com.mycelium.wallet.R
import com.mycelium.wallet.activity.util.MasterseedPasswordSetter
import com.mycelium.wallet.databinding.SignExtSigTransactionActivityBinding
import com.mycelium.wallet.extsig.common.ExternalSignatureDeviceManager
import com.mycelium.wallet.extsig.common.ExternalSignatureDeviceManager.OnButtonRequest
import com.mycelium.wallet.extsig.common.ExternalSignatureDeviceManager.OnPinMatrixRequest
import com.mycelium.wallet.extsig.common.ExternalSignatureDeviceManager.OnStatusUpdate
import com.mycelium.wallet.extsig.common.activity.ExtSigSignTransactionActivity
import com.mycelium.wapi.wallet.AccountScanManager.OnPassphraseRequest
import com.mycelium.wapi.wallet.AccountScanManager.OnScanError
import com.mycelium.wapi.wallet.AccountScanManager.OnStatusChanged
import com.squareup.otto.Subscribe

class KeepKeySignTransactionActivity : ExtSigSignTransactionActivity(), MasterseedPasswordSetter {

    override val extSigManager: ExternalSignatureDeviceManager
        get() = MbwManager.getInstance(this).keepKeyManager

    override fun setView() {
        setContentView(SignExtSigTransactionActivityBinding.inflate(layoutInflater).apply {
            binding = this
        }.root)
        binding.ivConnectExtSig.setImageResource(
            R.drawable.connect_keepkey
        )
    }

    @Subscribe
    override fun onPassphraseRequest(event: OnPassphraseRequest) {
        super.onPassphraseRequest(event)
    }

    @Subscribe
    override fun onScanError(event: OnScanError) {
        super.onScanError(event)
    }

    @Subscribe
    override fun onStatusUpdate(event: OnStatusUpdate) {
        super.onStatusUpdate(event)
    }

    @Subscribe
    override fun onPinMatrixRequest(event: OnPinMatrixRequest) {
        super.onPinMatrixRequest(event)
    }

    @Subscribe
    override fun onButtonRequest(event: OnButtonRequest) {
        super.onButtonRequest(event)
    }

    @Subscribe
    override fun onStatusChanged(event: OnStatusChanged) {
        super.onStatusChanged(event)
    }
}
