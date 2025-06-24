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
package com.mycelium.wallet.activity.modern

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.mycelium.wallet.MbwManager
import com.mycelium.wallet.R
import com.mycelium.wallet.activity.modern.event.RemoveTab
import com.mycelium.wallet.activity.settings.SettingsPreference
import com.mycelium.wallet.databinding.FragmentMarginTradeBinding
import com.mycelium.wallet.external.partner.model.MainMenuPage
import com.mycelium.wallet.external.partner.startContentLink


class AdsFragment : Fragment() {

    var binding: FragmentMarginTradeBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentMarginTradeBinding.inflate(inflater, container, false)
        .apply {
            binding = this
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pageData = arguments?.get("page") as MainMenuPage?
        binding?.banner?.let { banner ->
            Glide.with(banner)
                .load(pageData?.imageUrl)
                .into(banner)
            banner.setOnClickListener {
                startContentLink(pageData?.link)
            }
        }
        binding?.close?.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage(getString(R.string.hide_this_ad))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    SettingsPreference.setEnabled(pageData?.parentId ?: "", false)
                    MbwManager.getEventBus().post(RemoveTab(arguments?.getString("tag") ?: ""))
                }
                .setNegativeButton(R.string.no) { _, _ -> }
                .show()
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}