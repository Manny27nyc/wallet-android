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
package com.mycelium.wallet.activity.main

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.mycelium.wallet.activity.settings.SettingsPreference
import com.mycelium.wallet.databinding.LayoutTopBannerBinding
import com.mycelium.wallet.external.partner.startContentLink
import com.mycelium.wallet.randomOrNull

class AccountsTopBannerView : FrameLayout {
    private var binding: LayoutTopBannerBinding? = null
    private val preference by lazy { context.getSharedPreferences(ACCOUNTS_TOP_BANNER_PREF, Context.MODE_PRIVATE) }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        LayoutTopBannerBinding.inflate(LayoutInflater.from(context), this, true).apply {
            binding = this
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        SettingsPreference.getAccountsContent()?.bannersTop
                ?.filter {
                    it.isActive() && preference.getBoolean(it.parentId, true)
                            && SettingsPreference.isContentEnabled(it.parentId)
                }?.randomOrNull()?.let { banner ->
                    binding?.topBanner?.visibility = View.VISIBLE
                    Glide.with(binding?.bannerImage!!)
                            .load(banner.imageUrl)
                            .into(binding?.bannerImage!!)
                    binding?.topBanner?.setOnClickListener {
                        context.startContentLink(banner.link)
                    }
                    binding?.bannerClose?.setOnClickListener {
                        binding?.topBanner?.visibility = View.GONE
                        preference.edit().putBoolean(banner.parentId, false).apply()
                    }
                }
    }

    companion object {
        const val ACCOUNTS_TOP_BANNER_PREF = "accounts_top_banner"
    }
}