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

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.math.BigInteger

class FioBalanceService(private val ownerPublicKey: String, private val fioEndpoints: FioEndpoints) {
    private val mapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun getBalance(): BigInteger {
        val client = OkHttpClient()
        val requestBody = """{"fio_public_key":"$ownerPublicKey"}"""
        val request = Request.Builder()
                .url(fioEndpoints.getCurrentApiEndpoint().baseUrl + "chain/get_fio_balance")
                .post(RequestBody.create("application/json".toMediaType(), requestBody))
                .build()

        val response = client.newCall(request).execute()
        val result = mapper.readValue(response.body!!.string(), GetFioBalanceResponse::class.java)
        return result.balance
    }
}

class GetFioBalanceResponse {
    val balance: BigInteger = BigInteger.ZERO
}