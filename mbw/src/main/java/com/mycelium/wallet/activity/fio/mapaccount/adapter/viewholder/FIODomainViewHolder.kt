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
import com.mycelium.wallet.R
import com.mycelium.wallet.databinding.ItemFioNameBinding


class FIODomainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding = ItemFioNameBinding.bind(itemView)
    val title = binding.fioName
    val expireDate = binding.expireDate

    init {
        title.setCompoundDrawablesRelativeWithIntrinsicBounds(
                title.resources.getDrawable(R.drawable.ic_fio_domain),
                null, null, null)
    }
}