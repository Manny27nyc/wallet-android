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
package com.mycelium.wallet.external.mediaflow

import android.os.AsyncTask
import com.mycelium.wallet.external.mediaflow.database.NewsDatabase
import com.mycelium.wallet.external.mediaflow.model.Category


class GetCategoriesTask(val listener: ((List<Category>) -> Unit)) : AsyncTask<Void, Void, List<Category>>() {
    override fun doInBackground(vararg p0: Void?): List<Category> {
        return NewsDatabase.getCategories()
    }

    override fun onPostExecute(result: List<Category>) {
        super.onPostExecute(result)
        listener.invoke(result)
    }
}