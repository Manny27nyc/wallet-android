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
package com.mycelium.wallet.activity.main.loader

import android.os.AsyncTask
import com.mycelium.wallet.MbwManager
import com.mycelium.wapi.wallet.TransactionSummary
import com.mycelium.wapi.wallet.WalletAccount
import com.mycelium.wapi.wallet.fio.FIOOBTransaction
import com.mycelium.wapi.wallet.fio.FioModule
import java.util.concurrent.atomic.AtomicBoolean


class Preloader(private val toAdd: MutableList<TransactionSummary>,
                private var fioMetadataMap: MutableMap<String, FIOOBTransaction>,
                private val account: WalletAccount<*>,
                private val mbwManager: MbwManager,
                private val offset: Int, private val limit: Int,
                private val success: AtomicBoolean)
    : AsyncTask<Void?, Void?, Void?>() {
    override fun doInBackground(vararg params: Void?): Void? {
        val preloadedData = account.getTransactionSummaries(offset, limit)
        for (txSummary in preloadedData) {
            (mbwManager.getWalletManager(false).getModuleById(FioModule.ID) as FioModule?)
                    ?.getFioTxMetadata(txSummary.idHex)?.let { data ->
                        fioMetadataMap[txSummary.idHex] = data
                    }
        }

        synchronized(toAdd) {
            toAdd.addAll(preloadedData)
            success.set(toAdd.size == limit)
        }
        return null
    }
}
