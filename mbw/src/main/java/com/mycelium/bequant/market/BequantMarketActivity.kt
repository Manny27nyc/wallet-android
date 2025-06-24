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
package com.mycelium.bequant.market

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.mycelium.wallet.MbwManager
import com.mycelium.wallet.R
import com.mycelium.wallet.activity.modern.ModernMain
import com.mycelium.wallet.databinding.ActivityBequantMarketBinding
import com.mycelium.wallet.event.AccountListChanged
import com.squareup.otto.Bus


class BequantMarketActivity : AppCompatActivity(R.layout.activity_bequant_market) {

    companion object {

        const val IS_DEMO_KEY = "is_demo_key"

        @JvmStatic
        fun start(context: Context, isDemo: Boolean = false) {
            val starter = Intent(context, BequantMarketActivity::class.java)
                    .putExtra(IS_DEMO_KEY, isDemo)
            context.startActivity(starter)
        }
    }

    private val eventBus: Bus = MbwManager.getEventBus()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.bequant_main)
        navHostFragment.findNavController().setGraph(graph, intent.extras)

        val binding = ActivityBequantMarketBinding.inflate(layoutInflater)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val logoMenuClick = { _: View ->
            val isOpened = binding.logoMenu.visibility == VISIBLE
            binding.logoMenu.visibility = if (isOpened) GONE else VISIBLE
            binding.logoArrow.setImageDrawable(binding.logoArrow.resources.getDrawable(
                    if (isOpened) R.drawable.ic_arrow_drop_down else R.drawable.ic_arrow_drop_down_active))
        }
        binding.logoButton.setOnClickListener(logoMenuClick)
        binding.logoMenu.setOnClickListener(logoMenuClick)
        binding.myceliumWallet.setOnClickListener {
            finish()
            startActivity(Intent(this, ModernMain::class.java))
        }
        update()
    }

    private fun update() {
        eventBus.post(AccountListChanged())
    }
}