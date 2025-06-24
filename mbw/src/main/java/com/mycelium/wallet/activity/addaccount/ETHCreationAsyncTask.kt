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
package com.mycelium.wallet.activity.addaccount

import android.os.AsyncTask
import com.mycelium.wallet.MbwManager
import com.mycelium.wallet.event.AccountChanged
import com.mycelium.wallet.event.AccountCreated
import com.mycelium.wapi.wallet.eth.EthereumMasterseedConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class ETHCreationAsyncTask(val mbwManager: MbwManager,
                           val startListener: () -> Unit,
                           val endListener: (UUID?) -> Unit) : AsyncTask<Void, Int, UUID>() {

    override fun onPreExecute() {
        super.onPreExecute()
        startListener()
    }

    override fun doInBackground(vararg params: Void?): UUID =
            mbwManager.getWalletManager(false)
                    .createAccounts(EthereumMasterseedConfig()).first()


    override fun onPostExecute(accountId: UUID?) {
        MbwManager.getEventBus().post(AccountCreated(accountId))
        MbwManager.getEventBus().post(AccountChanged(accountId))
        endListener(accountId)
    }
}

fun MbwManager.createETH(startListener: () -> Unit, endListener: (UUID?) -> Unit) {
    GlobalScope.launch(Dispatchers.Default) {
        withContext(Dispatchers.Main) {
            startListener()
        }
        val accountId = getWalletManager(false).createAccounts(EthereumMasterseedConfig()).first()
        withContext(Dispatchers.Main) {
            MbwManager.getEventBus().post(AccountCreated(accountId))
            MbwManager.getEventBus().post(AccountChanged(accountId))
            endListener(accountId)
        }
    }
}