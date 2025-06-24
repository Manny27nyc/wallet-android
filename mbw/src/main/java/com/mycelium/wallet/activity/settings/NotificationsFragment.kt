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

import android.os.Bundle
import android.view.MenuItem
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceFragmentCompat
import com.mycelium.wallet.R

class NotificationsFragment : PreferenceFragmentCompat() {
    private var newsAllPreference: CheckBoxPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences_notifications)

        setHasOptionsMenu(true)
        (activity as SettingsActivity).supportActionBar?.run {
            setTitle(R.string.notifications)
            setHomeAsUpIndicator(R.drawable.ic_back_arrow)
            setDisplayShowHomeEnabled(false)
            setDisplayHomeAsUpEnabled(true)
        }

        newsAllPreference = findPreference("news_all_notification")
    }

    override fun onBindPreferences() {
        newsAllPreference?.isChecked = SettingsPreference.mediaFLowNotificationEnabled && SettingsPreference.mediaFlowEnabled
        newsAllPreference?.setOnPreferenceClickListener { preference ->
            val p = preference as CheckBoxPreference
            SettingsPreference.mediaFLowNotificationEnabled = p.isChecked
            true
        }
        newsAllPreference?.isEnabled = SettingsPreference.mediaFlowEnabled
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            fragmentManager?.popBackStack()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
