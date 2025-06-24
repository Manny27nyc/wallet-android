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
package com.mycelium.wallet

import android.content.Context
import android.content.Intent
import android.util.Log
import com.mycelium.modularizationtools.ModuleMessageReceiver

class MbwMessageReceiver(private val context: Context) : ModuleMessageReceiver {
    override fun getIcon() = R.drawable.ic_launcher

    override fun onMessage(callingPackageName: String, intent: Intent) {
        Log.e(TAG, "Ignoring unexpected package $callingPackageName calling with intent $intent.")
    }

    companion object {
        private val TAG = MbwMessageReceiver::class.java.canonicalName
    }
}
