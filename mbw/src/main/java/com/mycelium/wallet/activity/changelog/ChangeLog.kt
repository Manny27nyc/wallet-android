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
package com.mycelium.wallet.activity.changelog

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mycelium.wallet.activity.changelog.datasource.ChangeLogDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

internal object ChangeLog {

    /**
     * Show the [Change Log] popup.
     */
    fun show(fragmentManager: FragmentManager, tag: String? = null): BottomSheetDialogFragment =
        ChangeLogBottomSheetDialogFragment().also { it.show(fragmentManager, tag) }

    /**
     * Show the [Change Log] bottom sheet in case when the app has been updated and change logs exist for the new
     * version.
     *
     * @return [BottomSheetDialogFragment] if displayed, otherwise [null].
     */
    suspend fun showIfNewVersion(
        context: Context,
        fragmentManager: FragmentManager,
        tag: String? = null
    ): BottomSheetDialogFragment? {
        val hasNewReleaseChangeLog = ChangeLogDataSource(context.applicationContext).hasNewReleaseChangeLog()
        return if (
            hasNewReleaseChangeLog &&
            !fragmentManager.isStateSaved &&
            !fragmentManager.isDestroyed
        ) {
            withContext(Dispatchers.Main.immediate) {
                if (isActive) show(fragmentManager, tag) else null
            }
        } else null
    }
}
