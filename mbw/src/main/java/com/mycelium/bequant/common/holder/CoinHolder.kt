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
package com.mycelium.bequant.common.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mycelium.wallet.databinding.ItemBequantAccountBinding
import com.mycelium.wallet.databinding.ItemBequantCoinExpandedBinding
import com.mycelium.wallet.databinding.ItemBequantSearchBinding


class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding = ItemBequantCoinExpandedBinding.bind(itemView)
}

class ItemAccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding = ItemBequantAccountBinding.bind(itemView)
}

class SearchHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding = ItemBequantSearchBinding.bind(itemView)
}

class SpaceHolder(itemView: View) : RecyclerView.ViewHolder(itemView)