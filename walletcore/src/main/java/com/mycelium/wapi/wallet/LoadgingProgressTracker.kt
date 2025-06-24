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

object LoadingProgressTracker {
    val subscribers: MutableList<LoadingProgressUpdater> = ArrayList()

    fun setStatus(status: LoadingProgressStatus) = subscribers.forEach { it.status = status }

    fun setPercent(percent: Int) = subscribers.forEach { it.percent = percent }

    fun subscribe(tracker: LoadingProgressUpdater) = subscribers.add(tracker)

    fun clearLastFullUpdateTime() = subscribers.forEach(LoadingProgressUpdater::clearLastFullUpdateTime)
}