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
package com.mycelium.bequant.receive.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import com.mycelium.bequant.receive.FromMyceliumFragment
import com.mycelium.bequant.receive.ShowQRFragment
import com.mycelium.bequant.receive.viewmodel.ReceiveCommonViewModel


class ReceiveFragmentAdapter(fa: Fragment, private val vm: ReceiveCommonViewModel, private val supportedByMycelium: Boolean) :
        FragmentStatePagerAdapter(fa.childFragmentManager) {
    override fun getItem(position: Int): Fragment = when (position) {
        0 -> ShowQRFragment().apply { parentViewModel = vm }
        1 -> FromMyceliumFragment().apply { parentViewModel = vm }
        else -> TODO("not implemented")
    }

    override fun getCount(): Int = if (supportedByMycelium) 2 else 1

    override fun getPageTitle(position: Int): CharSequence? =
            when (position) {
                0 -> "Show QR"
                1 -> "From Mycelium"
                else -> ""
            }
}