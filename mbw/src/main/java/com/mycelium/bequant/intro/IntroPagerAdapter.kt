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
package com.mycelium.bequant.intro

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class IntroPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    val items = listOf(IntroPage1Fragment(), IntroPage2Fragment(), IntroPage3Fragment(), IntroPage4Fragment())

    override fun getItemCount(): Int = items.size

    override fun createFragment(position: Int): Fragment = items[position]
}