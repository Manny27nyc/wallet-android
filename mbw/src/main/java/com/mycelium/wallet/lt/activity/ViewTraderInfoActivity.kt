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
package com.mycelium.wallet.lt.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mycelium.lt.api.model.PublicTraderInfo
import com.mycelium.wallet.R
import com.mycelium.wallet.databinding.LtViewTraderInfoActivityBinding

class ViewTraderInfoActivity : AppCompatActivity() {
    private var _traderInfo: PublicTraderInfo? = null

    /** Called when the activity is first created.  */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LtViewTraderInfoActivityBinding.inflate(layoutInflater).root)
        _traderInfo = intent.getSerializableExtra("traderInfo") as PublicTraderInfo?
    }

    override fun onResume() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.flTraderInfo, TraderInfoFragment.createInstance(_traderInfo))
                .commitAllowingStateLoss()
        super.onResume()
    }

    companion object {
        @JvmStatic
        fun callMe(currentActivity: Activity, traderInfo: PublicTraderInfo?) {
            val intent = Intent(currentActivity, ViewTraderInfoActivity::class.java)
            intent.putExtra("traderInfo", traderInfo)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            currentActivity.startActivity(intent)
        }
    }
}