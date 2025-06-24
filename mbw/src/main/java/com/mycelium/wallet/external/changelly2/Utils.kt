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
package com.mycelium.wallet.external.changelly2

import com.mycelium.wallet.external.changelly.model.ChangellyTransaction


fun ChangellyTransaction.getReadableStatus(prefix: String = "") =
        when (status) {
            "waiting" -> "%sin progress"
            "confirming" -> "%sin progress"
            "exchanging" -> "%sin progress"
            "sending" -> "%sin progress"
            "finished" -> "%scompleted"
            "failed" -> "%sfailed"
            "refunded" -> "%sfailed"
            "hold" -> "Hold"
            "expired" -> "%sexpired"
            else -> "Unknown tx status"
        }.let {
            if (prefix.isNotEmpty()) it.format("$prefix ")
            else it.format("")
        }.capitalize()