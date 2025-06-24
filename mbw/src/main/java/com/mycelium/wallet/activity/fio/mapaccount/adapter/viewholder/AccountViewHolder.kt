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
package com.mycelium.wallet.activity.fio.mapaccount.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mycelium.wallet.databinding.ItemFioNameDetailsAccountBinding


class AccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding = ItemFioNameDetailsAccountBinding.bind(itemView)
    val icon = binding.icon
    val label = binding.title
    val type = binding.subtitle
    val balance = binding.balance
}