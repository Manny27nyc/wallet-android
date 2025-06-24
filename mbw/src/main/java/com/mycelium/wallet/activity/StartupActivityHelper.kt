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
package com.mycelium.wallet.activity

import android.content.Context
import com.mycelium.wallet.R
import com.mycelium.wapi.wallet.LoadingProgressStatus
import com.mycelium.wapi.wallet.LoadingProgressStatus.State.*

/**
 * This function is supposed to help convert status of wallet loading into human readable text.
 */
fun LoadingProgressStatus.format(context: Context) = when (state) {
        STARTING -> context.getString(R.string.starting_application)
        MIGRATING -> context.getString(R.string.migrating_accounts)
        LOADING -> context.getString(R.string.loading_accounts)
        MIGRATING_N_OF_M_HD -> context.getString(R.string.migrating_n_of_m_hd, done, total)
        LOADING_N_OF_M_HD -> context.getString(R.string.loading_n_of_m_hd, done, total)
        MIGRATING_N_OF_M_SA -> context.getString(R.string.migrating_n_of_m_sa, done, total)
        LOADING_N_OF_M_SA -> context.getString(R.string.loading_n_of_m_sa, done, total)
    }