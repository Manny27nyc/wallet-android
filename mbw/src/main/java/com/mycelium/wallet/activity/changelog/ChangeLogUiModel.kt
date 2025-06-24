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

internal sealed class ChangeLogUiModel(val type: Int) {

    class LatestRelease(val versionCode: Int, val version: String) : ChangeLogUiModel(VIEW_TYPE) {

        companion object {
            const val VIEW_TYPE = 0
        }
    }

    class Release(val versionCode: Int, val version: String) : ChangeLogUiModel(VIEW_TYPE) {

        companion object {
            const val VIEW_TYPE = 1
        }
    }

    class Change(val change: String) : ChangeLogUiModel(VIEW_TYPE) {

        companion object {
            const val VIEW_TYPE = 2
        }
    }
}
