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
package com.mycelium.wallet.activity.export

import android.os.Bundle
import android.view.View.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.common.base.Optional
import com.mrd.bitlib.crypto.BipDerivationType
import com.mycelium.wallet.MbwManager
import com.mycelium.wallet.R
import com.mycelium.wallet.activity.export.viewmodel.ExportFioAsQrViewModel
import com.mycelium.wallet.databinding.ActivityExportFioKeyBinding
import com.mycelium.wapi.wallet.ExportableAccount
import com.mycelium.wapi.wallet.btc.bip44.HDAccount
import com.mycelium.wapi.wallet.fio.FioKeyManager
import androidx.activity.viewModels

class ExportFioKeyActivity : AppCompatActivity() {
    private val viewModel: ExportFioAsQrViewModel by viewModels()
    private lateinit var binding: ActivityExportFioKeyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        val mbwManager = MbwManager.getInstance(this)
        val fioKeyManager = mbwManager.fioKeyManager
        val publicKey = fioKeyManager.getFioPublicKey((mbwManager.selectedAccount as HDAccount).accountIndex)
        val formatPubKey = fioKeyManager.formatPubKey(publicKey)

        if (!viewModel.isInitialized()) {
            viewModel.init(ExportableAccount.Data(Optional.absent(), mapOf(BipDerivationType.BIP44 to formatPubKey)))
        }

        setContentView(ActivityExportFioKeyBinding.inflate(layoutInflater).apply {
            binding = this
            this.viewModel = viewModel as ExportFioAsQrViewModel
            this.activity = this@ExportFioKeyActivity
        }.root)
        binding.lifecycleOwner = this
        subscribeQR()
        binding.layoutQR.tvWarning.visibility = GONE
        binding.layoutShare.btShare.text = getString(R.string.share_fio_public_key)
    }

    // sets key as qr and as textView
    private fun subscribeQR() =
            viewModel.getAccountDataString().observe(this, Observer { accountData ->
                binding.layoutQR.ivQrCode.qrCode = accountData
            })

}
