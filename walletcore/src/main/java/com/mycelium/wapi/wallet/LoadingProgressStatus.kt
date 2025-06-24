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

/**
 * This class represent current wallet loading statuses. Comments are supposed string representations in English.
 */
class LoadingProgressStatus(val state: State, val done: Int? = null, val total: Int? = null) {
    enum class State {
        STARTING, MIGRATING, LOADING, MIGRATING_N_OF_M_HD, LOADING_N_OF_M_HD, MIGRATING_N_OF_M_SA, LOADING_N_OF_M_SA
    }
}