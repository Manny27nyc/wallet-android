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
package com.mycelium.wapi.wallet.fio

import com.mycelium.net.HttpEndpoint
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.timerTask

class FioEndpoints(private var apiEndpoints: FioApiEndpoints, private var historyEndpoints: FioHistoryEndpoints)
    : ServerFioApiListChangedListener, ServerFioHistoryListChangedListener {
    private var curApiEndpointIndex = (Math.random() * this.apiEndpoints.endpoints.size).toInt()
    private var curHistoryEndpointIndex = (Math.random() * this.historyEndpoints.endpoints.size).toInt()

    fun getCurrentApiEndpoint(): HttpEndpoint = apiEndpoints.endpoints[curApiEndpointIndex]

    fun getCurrentHistoryEndpoint(): HttpEndpoint = historyEndpoints.endpoints[curHistoryEndpointIndex]

    private fun moveToNextApiEndpoint() {
        curApiEndpointIndex = (curApiEndpointIndex + 1) % apiEndpoints.endpoints.size
    }

    private fun moveToNextHistoryEndpoint() {
        curHistoryEndpointIndex = (curHistoryEndpointIndex + 1) % historyEndpoints.endpoints.size
    }

    fun rotateEndpoints() {
        moveToNextApiEndpoint()
        moveToNextHistoryEndpoint()
    }

    override fun apiServerListChanged(newEndpoints: Array<HttpEndpoint>) {
        apiEndpoints.endpoints = newEndpoints.toList()
        curApiEndpointIndex = (Math.random() * this.apiEndpoints.endpoints.size).toInt()
    }

    override fun historyServerListChanged(newEndpoints: Array<HttpEndpoint>) {
        historyEndpoints.endpoints = newEndpoints.toList()
        curHistoryEndpointIndex = (Math.random() * this.historyEndpoints.endpoints.size).toInt()
    }
}

interface ServerFioApiListChangedListener {
    fun apiServerListChanged(newEndpoints: Array<HttpEndpoint>)
}

interface ServerFioHistoryListChangedListener {
    fun historyServerListChanged(newEndpoints: Array<HttpEndpoint>)
}