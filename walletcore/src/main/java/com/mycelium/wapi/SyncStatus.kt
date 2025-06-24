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
package com.mycelium.wapi

import java.util.Date

enum class SyncStatus {
    UNKNOWN, SUCCESS, ERROR, INTERRUPT
    , ERROR_INTERNET_CONNECTION
}

data class SyncStatusInfo @JvmOverloads constructor(val status: SyncStatus, val timestamp: Date = Date())

