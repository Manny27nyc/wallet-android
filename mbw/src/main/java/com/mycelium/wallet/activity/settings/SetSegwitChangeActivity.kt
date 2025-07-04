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
package com.mycelium.wallet.activity.settings

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup

import com.mycelium.wallet.MbwManager
import com.mycelium.wallet.R
import com.mycelium.wapi.wallet.btc.ChangeAddressMode

class SetSegwitChangeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_segwit_change)

        supportActionBar?.apply {
            setDisplayShowTitleEnabled(true)
            setTitle(R.string.segwit_change_mode_title)
            setHomeAsUpIndicator(R.drawable.ic_back_arrow)
            setDisplayHomeAsUpEnabled(true)
        }

        val mbwManager = MbwManager.getInstance(this)
        val radioGroup = findViewById<RadioGroup>(R.id.radio_group)

        // set selected. *2 because we have texviews
        val currentModeTag = mbwManager.changeAddressMode.toString()
        (radioGroup.findViewWithTag<View>(currentModeTag) as RadioButton).isChecked = true

        // click listener. Also works on text views
        for (i in 1 until radioGroup.childCount) {
            radioGroup.getChildAt(i).setOnClickListener { view ->
                val clickedTag = view.tag.toString()
                mbwManager.changeAddressMode = ChangeAddressMode.valueOf(clickedTag)
                (radioGroup.findViewWithTag<View>(clickedTag) as RadioButton).isChecked = true
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        @JvmStatic
        fun callMe(currentActivity: Activity) {
            val intent = Intent(currentActivity, SetSegwitChangeActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(currentActivity, R.anim.slide_right_in, R.anim.slide_left_out)
            currentActivity.startActivity(intent, options.toBundle())
        }
    }
}
