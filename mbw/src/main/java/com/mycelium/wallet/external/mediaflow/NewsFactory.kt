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
package com.mycelium.wallet.external.mediaflow

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.mycelium.wallet.BuildConfig

import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

object NewsFactory {
    private val objectMapper: ObjectMapper = ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)

    val service by lazy {
        Retrofit.Builder()
                .baseUrl(BuildConfig.MEDIA_FLOW_URL)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build()
                .create(NewsApiService::class.java)
    }
}
