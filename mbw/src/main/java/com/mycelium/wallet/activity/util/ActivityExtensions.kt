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
package com.mycelium.wallet.activity.util

import android.app.Activity
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException

fun Activity.fileProviderAuthority(): String {
    try {
        val packageManager = this.application.packageManager
        val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PROVIDERS)
        packageInfo.providers?.forEach { info ->
            if (info.name.equals("androidx.core.content.FileProvider")) {
                return info.authority;
            }
        }
    } catch (e: NameNotFoundException) {
        throw RuntimeException(e);
    }
    throw RuntimeException("No file provider authority specified in manifest");
}