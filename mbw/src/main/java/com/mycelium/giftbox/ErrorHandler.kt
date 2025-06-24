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
package com.mycelium.giftbox

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.mycelium.giftbox.client.RetrofitFactory
import com.mycelium.giftbox.client.model.MCErrorWrap
import com.mycelium.wallet.R

class ErrorHandler {
    var cancelListener: (() -> Unit)? = null

    fun handle(context: Context, code: Int?, error: String?) {
        val alertDialog =
            AlertDialog.Builder(context, R.style.MyceliumModern_Dialog)
        val mcError = try {
            RetrofitFactory.objectMapper
                .readValue<MCErrorWrap>(error, MCErrorWrap::class.java).error
        } catch (e: Exception) {
            null
        }
        if (mcError != null) {
            alertDialog
                .setTitle(mcError.title)
                .setMessage(mcError.body)
                .setPositiveButton(R.string.button_ok) { _, _ -> }
        } else {
            alertDialog.setTitle(context.getString(R.string.gift_card_load_error_title))
                .setMessage(context.getString(R.string.gift_card_load_error_title))
                .setPositiveButton(R.string.try_again) { _, _ -> }
                .setNegativeButton(R.string.cancel) { _, _ ->
                    cancelListener?.invoke()
                }
        }
        alertDialog.create().apply {
            setOnShowListener {
                getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                    context.resources.getColor(R.color.bequant_green)
                )
            }
        }.show()
    }
}