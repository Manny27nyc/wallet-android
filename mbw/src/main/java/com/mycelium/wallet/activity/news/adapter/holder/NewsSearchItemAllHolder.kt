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
package com.mycelium.wallet.activity.news.adapter.holder

import android.content.SharedPreferences
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mycelium.wallet.activity.modern.adapter.holder.NewsV2ListHolder
import com.mycelium.wallet.databinding.ItemAllNewsSearchBinding


class NewsSearchItemAllHolder(val preferences: SharedPreferences, itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    val binding = ItemAllNewsSearchBinding.bind(itemView)
    val category = binding.tvCategory as TextView
    val showAll = binding.viewMore
    val listHolder = NewsV2ListHolder(preferences, binding.list)
}