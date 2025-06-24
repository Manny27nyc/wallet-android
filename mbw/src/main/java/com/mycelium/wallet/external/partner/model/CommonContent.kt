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
package com.mycelium.wallet.external.partner.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*


abstract class CommonContent(@SerializedName("parentId") val parentId: String? = null,
                             @SerializedName("start-date") val startDate: Date? = null,
                             @SerializedName("end-date") val endDate: Date? = null,
                             @SerializedName("isEnabled") val isEnabled: Boolean? = true) : Serializable {
    fun isActive() = isEnabled != false &&
            Date().let { startDate?.before(it) != false && endDate?.after(it) != false }
}