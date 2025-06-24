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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mycelium.wallet.event.NetworkConnectionStateChanged
import java.util.logging.Level
import java.util.logging.Logger

class NetworkChangedReceiver : BroadcastReceiver() {
    // We receive this event on wallet start, but this would start heavy init, which we don't want to.
    var wasInited: Boolean = false

    val logger = Logger.getLogger(NetworkChangedReceiver::class.java.simpleName)

    override fun onReceive(context: Context, intent: Intent) {
        if (!wasInited) {
            wasInited = true
            return
        }
        if (intent.action == "android.net.conn.CONNECTIVITY_CHANGE") {
            val mbwManager = MbwManager.getInstance(context)
            val connected = Utils.isConnected(context, "CONNECTIVITY_CHANGE")
            logger.log(Level.INFO, "Connectivity status has been changed. Connected: $connected")
            mbwManager.getWalletManager(false).isNetworkConnected = connected
            mbwManager.wapi.setNetworkConnected(connected)
            mbwManager.btcvWapi.setNetworkConnected(connected)
            MbwManager.getEventBus().post(NetworkConnectionStateChanged(connected))
        }
    }
}
