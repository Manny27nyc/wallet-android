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
import com.mycelium.wallet.databinding.RecordRowInvestmentBinding


class InvestmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = RecordRowInvestmentBinding.bind(view)
    val balance = binding.tvBalance
    val activateLink = binding.activateLink
}