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
package com.mycelium.wallet.extsig.common.activity

import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import com.mycelium.wallet.activity.HdAccountSelectorActivity.HdAccountWrapper
import com.mycelium.wallet.activity.send.SendInitializationActivity.Companion.getIntent
import com.mycelium.wallet.activity.util.AbstractAccountScanManager

abstract class InstantExtSigActivity<AccountScanManager : AbstractAccountScanManager> :
    ExtSigAccountSelectorActivity<AccountScanManager>() {
    override fun accountClickListener(): OnItemClickListener? =
        object : OnItemClickListener {
            override fun onItemClick(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                (adapterView.getItemAtPosition(i) as? HdAccountWrapper)?.run {
                    val intent = getIntent(this@InstantExtSigActivity, id!!, true)
                    this@InstantExtSigActivity.startActivityForResult(intent, REQUEST_SEND)
                }
            }
        }
}
