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
package com.mycelium.wallet.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mrd.bitlib.crypto.InMemoryPrivateKey
import com.mrd.bitlib.model.AddressType
import com.mycelium.wallet.MbwManager
import com.mycelium.wallet.databinding.CreateKeyActivityBinding
import com.mycelium.wapi.wallet.AddressUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateKeyActivity : AppCompatActivity() {
    private lateinit var binding: CreateKeyActivityBinding
    private var manager: MbwManager? = null
    private var key: InMemoryPrivateKey? = null

    /** Called when the activity is first created.  */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        manager = MbwManager.getInstance(application)
        setContentView(CreateKeyActivityBinding.inflate(layoutInflater).apply {
            binding = this
        }.root)
        supportActionBar!!.hide()
        binding.btShuffle.setOnClickListener { createNewKey() }
        binding.btUse.setOnClickListener {
            this@CreateKeyActivity.setResult(Activity.RESULT_OK,
                    Intent().putExtra("base58key", key!!.getBase58EncodedPrivateKey(manager!!.network)))
            finish()
        }
        createNewKey()
    }

    private fun createNewKey() {
        binding.btUse.isEnabled = false
        binding.btShuffle.isEnabled = false
        lifecycleScope.launch(Dispatchers.Default) {
            key = InMemoryPrivateKey(manager!!.randomSource, true)
            val addresses = key!!.publicKey.getAllSupportedAddresses(manager!!.network)
            launch(Dispatchers.Main) {
                binding.tvAddressP2PKH.address = AddressUtils.fromAddress(addresses[AddressType.P2PKH])
                binding.tvAddressP2SH.address = AddressUtils.fromAddress(addresses[AddressType.P2SH_P2WPKH])
                binding.tvAddressBech.address = AddressUtils.fromAddress(addresses[AddressType.P2WPKH])
                binding.btShuffle.isEnabled = true
                binding.btUse.isEnabled = true
            }
        }
    }
}