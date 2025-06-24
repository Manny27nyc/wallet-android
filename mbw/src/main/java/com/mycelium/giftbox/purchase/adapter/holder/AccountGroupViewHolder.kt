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
package com.mycelium.giftbox.purchase.adapter.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mycelium.wallet.databinding.ItemGiftboxSelectAccountGroupBinding


class AccountGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding = ItemGiftboxSelectAccountGroupBinding.bind(itemView)
    val chevron = binding.chevron
    val label = binding.label
    val count = binding.count
    val value = binding.value
}