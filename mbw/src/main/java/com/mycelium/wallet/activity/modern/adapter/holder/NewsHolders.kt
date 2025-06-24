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
package com.mycelium.wallet.activity.modern.adapter.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mycelium.wallet.databinding.ItemMediaflowBannerBinding
import com.mycelium.wallet.databinding.ItemMediaflowNewsCategoryBtnBinding
import com.mycelium.wallet.databinding.ItemMediaflowTurnOffBinding


class NewsItemLoadingHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class NewsCategoryBtnHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding =  ItemMediaflowNewsCategoryBtnBinding.bind(itemView)
    val text = binding.text
    val icon = binding.icon
}

class NewsLoadingHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class NewsNoBookmarksHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class NewsTurnOff(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding = ItemMediaflowTurnOffBinding.bind(itemView)
}

class CurrencycomBannerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding = ItemMediaflowBannerBinding.bind(itemView)
}