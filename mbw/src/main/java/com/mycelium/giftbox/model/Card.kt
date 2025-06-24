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
package com.mycelium.giftbox.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Card(
        val clientOrderId: String,
        val productCode: String? = null,
        val productName: String? = null,
        val productImg: String? = null,
        val currencyCode: String? = null,
        val amount: String? = null,
        val expiryDate: String? = null,
        val code: String,
        val deliveryUrl: String,
        val pin: String,
        val timestamp: Date? = null,
        val isRedeemed: Boolean
) : Parcelable