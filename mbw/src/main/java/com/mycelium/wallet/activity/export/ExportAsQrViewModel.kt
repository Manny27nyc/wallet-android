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
package com.mycelium.wallet.activity.export

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import com.mycelium.wallet.R
import com.mycelium.wallet.Utils
import com.mycelium.wallet.activity.modern.Toaster
import com.mycelium.wapi.wallet.ExportableAccount


open class ExportAsQrViewModel(val context: Application) : AndroidViewModel(context) {
    protected lateinit var model: ExportAsQrModel

    fun init(accountData: ExportableAccount.Data) {
        if (::model.isInitialized) {
            throw IllegalStateException("This method should be called only once.")
        }
        model = ExportAsQrModel(context, accountData)
        model.accountDataString.value = accountData.publicDataMap!!.values.first()

        updateData(false)
    }

    fun hasPrivateData(): Boolean = model.hasPrivateData()

    open fun updateData(privateDataSelected: Boolean) {
        model.updateData(privateDataSelected)
    }

    fun isPrivateDataSelected() = model.privateDataSelected

    fun getShowRedWarning() = model.showRedWarning

    fun getAccountDataString() = model.accountDataString

    // must return false to bind with onLongClick in xml
    fun acceptWarning(): Boolean {
        model.acceptWaring()
        return false
    }

    /**
     * Returns private/public key as string with spaces for readability
     */
    fun getReadableData(data: String): String {
        // split the date in fragments with 8chars and a newline after three parts
        val builder = StringBuilder()
        var cnt = 0
        for (part in Utils.stringChopper(data, 8)) {
            cnt++
            builder.append(part)
            builder.append(if (cnt % 3 == 0) "\n" else " ")
        }

        return builder.toString()
    }

    fun exportDataToClipboard(activity: AppCompatActivity) {
        val accountDataString = model.accountDataString.value
        if (model.privateDataSelected.value!!) {
            val builder = AlertDialog.Builder(activity)
            builder.setMessage(R.string.export_to_clipboard_warning).setCancelable(false)
                    .setPositiveButton(R.string.yes) { dialog, id ->
                        Utils.setClipboardString(accountDataString, context)
                        Toaster(context).toast(R.string.copied_to_clipboard, true)
                        dialog.dismiss()
                    }.setNegativeButton(R.string.no) { dialog, id -> }
            val alertDialog = builder.create()
            alertDialog.show()
        } else {
            Utils.setClipboardString(accountDataString, context)
            Toaster(context).toast(R.string.copied_to_clipboard, true)
        }
    }

    fun shareData(activity: AppCompatActivity) {
        val sharedData = model.accountDataString.value
        if (model.privateDataSelected.value!!) {
            val builder = AlertDialog.Builder(activity)
            builder.setMessage(R.string.export_share_warning).setCancelable(false)
                    .setPositiveButton(R.string.yes) { dialog, id ->
                        val sendIntent = Intent(Intent.ACTION_SEND)
                        sendIntent.type = "text/plain"
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, context.resources.getString(R.string.xpriv_title))
                        sendIntent.putExtra(Intent.EXTRA_TEXT, sharedData)
                        activity.startActivity(Intent.createChooser(sendIntent, context.resources.getString(R.string.share_xpriv)))
                        dialog.dismiss()
                    }.setNegativeButton(R.string.no) { dialog, id -> }
            val alertDialog = builder.create()
            alertDialog.show()
        } else {
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.type = "text/plain"
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, context.resources.getString(R.string.xpub_title))
            sendIntent.putExtra(Intent.EXTRA_TEXT, sharedData)
            activity.startActivity(Intent.createChooser(sendIntent, context.resources.getString(R.string.share_xpub)))
        }
    }

    fun isInitialized() = ::model.isInitialized
}