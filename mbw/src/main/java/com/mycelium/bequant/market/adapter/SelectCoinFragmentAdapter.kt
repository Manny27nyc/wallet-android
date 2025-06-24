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
package com.mycelium.bequant.market.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mycelium.bequant.exchange.SelectCoinFragment
import com.mycelium.bequant.exchange.SelectCoinFragment.Companion.GET
import com.mycelium.bequant.exchange.SelectCoinFragment.Companion.SEND

class SelectCoinFragmentAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment =
            when (position) {
                0 -> SelectCoinFragment.newInstance(SEND)
                1 -> SelectCoinFragment.newInstance(GET)
                else -> TODO("not implemented")
            }
}