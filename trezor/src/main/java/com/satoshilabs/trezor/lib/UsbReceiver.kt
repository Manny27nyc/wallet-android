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
package com.satoshilabs.trezor.lib

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log

/**
 * https://developer.android.com/develop/connectivity/usb/host#kotlin
 * Receives broadcast when a supported USB device is attached, detached or
 * when a permission to communicate to the device has been granted.
 */
class UsbReceiver(val result: (Boolean) -> Unit) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (context.actionUsbPermission() == intent.action) {
            val usbDevice: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
            val deviceName = usbDevice?.deviceName

            val permission = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
            Log.d(TAG, "ACTION_USB_PERMISSION: $permission Device: $deviceName");

            result(permission)
            context.unregisterReceiver(this)
        }
    }

    companion object {
        const val TAG = "UsbReceiver"
        private const val ACTION_USB_PERMISSION = "USB_PERMISSION"

        @JvmStatic
        fun Context.actionUsbPermission() = "${this.packageName}.$ACTION_USB_PERMISSION"
    }
}
