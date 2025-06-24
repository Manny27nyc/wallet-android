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
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mycelium.wallet.databinding.RecordRowBinding

class AccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding = RecordRowBinding.bind(itemView)
    val llAddress: View = binding.llAddress
    val tvLabel: TextView = binding.tvLabel
    val tvWhatIsIt: TextView = binding.tvWhatIsIt
    val icon: ImageView = binding.ivIcon
    val tvAddress: TextView = binding.tvAddress
    val tvAccountType: TextView = binding.tvAccountType
    val tvBalance: TextView = binding.tvBalance
    val backupMissing: TextView = binding.tvBackupMissingWarning
    val tvProgress: TextView = binding.tvProgress
    val layoutProgressTxRetreived: View = binding.progressTxRetreived
    val tvProgressLayout: View = binding.tvProgressLayout
    val ivWhatIsSync: ImageView = binding.ivWhatIsSync
    val progressBar: ProgressBar = binding.progressBar
    val lastSyncState: View = binding.lastSyncState
    val tvTraderKey: View = binding.tvTraderKey
}