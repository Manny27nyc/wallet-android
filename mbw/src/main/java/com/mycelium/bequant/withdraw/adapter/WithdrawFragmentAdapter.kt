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
package com.mycelium.bequant.withdraw.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import com.mycelium.bequant.withdraw.WithdrawAddressFragment
import com.mycelium.bequant.withdraw.WithdrawWalletFragment
import com.mycelium.bequant.withdraw.viewmodel.WithdrawViewModel


class WithdrawFragmentAdapter(fragment: Fragment, val vm: WithdrawViewModel, val supportedByMycelium: Boolean) : FragmentStatePagerAdapter(fragment.childFragmentManager) {
    override fun getCount(): Int = if (supportedByMycelium) 2 else 1

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> WithdrawAddressFragment().apply { parentViewModel = vm }
        1 -> WithdrawWalletFragment().apply { parentViewModel = vm }
        else -> TODO("not implemented")
    }

    override fun getPageTitle(position: Int): CharSequence? =
            when (position) {
                0 -> "Address"
                1 -> "Mycelium Wallet"
                else -> ""
            }
}