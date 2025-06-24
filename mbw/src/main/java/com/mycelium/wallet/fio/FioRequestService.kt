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
package com.mycelium.wallet.fio

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.mycelium.wallet.MbwManager
import com.mycelium.wallet.activity.StartupActivity
import com.mycelium.wallet.activity.fio.requests.ApproveFioRequestActivity
import com.mycelium.wallet.fio.FioRequestNotificator.FIO_REQUEST_ACTION

class FioRequestService : Service() {
    override fun onBind(p0: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
// TODO may be we need update fio request before move forward
        intent?.let {
            val activityClass =
                if (MbwManager.getInstance(applicationContext).isAppInForeground) ApproveFioRequestActivity::class.java
                else StartupActivity::class.java
            startActivity(
                Intent(applicationContext, activityClass)
                    .setAction(FIO_REQUEST_ACTION)
                    .putExtras(it)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
        return START_STICKY
    }
}