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

import android.app.Activity
import androidx.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mycelium.wallet.MbwManager
import com.mycelium.wallet.Utils
import com.mycelium.wallet.databinding.ExportAsQrActivityBinding
import com.mycelium.wallet.databinding.ExportAsQrActivityQrBinding
import com.mycelium.wallet.databinding.ExportAsQrBtcHdActivityBinding
import com.mycelium.wallet.databinding.ExportAsQrBtcSaActivityBinding
import com.mycelium.wapi.wallet.ExportableAccount
import com.mycelium.wapi.wallet.WalletAccount
import java.util.*
import androidx.activity.viewModels
import com.mycelium.wallet.activity.export.viewmodel.ExportAsQrFactory

class ExportAsQrActivity : AppCompatActivity() {
    private val viewModel: ExportAsQrViewModel by viewModels { ExportAsQrFactory(account, accountData) }
    private var bindingQR: ExportAsQrActivityQrBinding? = null

    lateinit var account: WalletAccount<*>
    lateinit var accountData: ExportableAccount.Data
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        accountData = intent.getSerializableExtra(ACCOUNT_DATA) as ExportableAccount.Data
        val accountUUID = intent.getSerializableExtra(ACCOUNT_UUID) as UUID
        account = MbwManager.getInstance(this)
                .getWalletManager(false)
            .getAccount(accountUUID) ?: throw IllegalStateException()

        if (accountData.publicDataMap?.size == 0 && !accountData.privateData.isPresent) {
            finish()
            return
        }

        if (!viewModel.isInitialized()) {
            viewModel.init(accountData)
        }

        // Inflate view and obtain an instance of the binding class.

        val binding = when(viewModel) {
            is ExportAsQrBtcHDViewModel -> ExportAsQrBtcHdActivityBinding.inflate(layoutInflater).also {
                bindingQR = it.layoutQR
                it.viewModel = viewModel as ExportAsQrMultiKeysViewModel
                it.activity = this
            }
            is ExportAsQrBtcSAViewModel -> ExportAsQrBtcSaActivityBinding.inflate(layoutInflater).also {
                bindingQR = it.layoutQR
                it.viewModel = viewModel as ExportAsQrMultiKeysViewModel
                it.activity = this
            }
            else -> ExportAsQrActivityBinding.inflate(layoutInflater).also {
                bindingQR = it.layoutQR
                it.viewModel = viewModel
                it.activity = this
            }
        }
        binding.lifecycleOwner = this
        setContentView(binding.root)

        // Prevent the OS from taking screenshots of this activity
        Utils.preventScreenshots(this)

        subscribeQR()
    }

    // sets key as qr and as textView
    private fun subscribeQR() =
        viewModel.getAccountDataString().observe(this, Observer { accountData ->
            bindingQR?.ivQrCode?.qrCode = accountData
        })

    override fun onPause() {
        // This way we finish the activity when home is pressed, so you are forced
        // to reenter the PIN to see the QR-code again
        finish()
        super.onPause()
    }

    companion object {
        private const val ACCOUNT_DATA = "accountData"
        private const val ACCOUNT_UUID = "accountUUID"

        @JvmStatic
        fun callMe(currentActivity: Activity, accountData: ExportableAccount.Data, account: WalletAccount<*>) {
            val intent = Intent(currentActivity, ExportAsQrActivity::class.java)
                    .putExtra(ACCOUNT_DATA, accountData)
                    .putExtra(ACCOUNT_UUID, account.id)
            currentActivity.startActivity(intent)
        }
    }
}