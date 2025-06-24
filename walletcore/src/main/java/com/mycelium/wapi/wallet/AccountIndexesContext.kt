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
package com.mycelium.wapi.wallet

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable


data class AccountIndexesContext(@JsonProperty("lastExternalIndexWithActivity") var lastExternalIndexWithActivity: Int = 0,
                                 @JsonProperty("lastInternalIndexWithActivity") var lastInternalIndexWithActivity: Int = 0,
                                 @JsonProperty("firstMonitoredInternalIndex") var firstMonitoredInternalIndex: Int = 0)
    : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
