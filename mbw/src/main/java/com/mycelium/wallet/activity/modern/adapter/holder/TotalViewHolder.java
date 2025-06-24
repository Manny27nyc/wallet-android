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
package com.mycelium.wallet.activity.modern.adapter.holder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.mycelium.wallet.R;
import com.mycelium.wallet.activity.util.TotalToggleableCurrencyButton;


public class TotalViewHolder extends RecyclerView.ViewHolder {
    public TotalToggleableCurrencyButton tcdBalance;

    public TotalViewHolder(View itemView) {
        super(itemView);
        tcdBalance = itemView.findViewById(R.id.tcdBalance);
    }
}