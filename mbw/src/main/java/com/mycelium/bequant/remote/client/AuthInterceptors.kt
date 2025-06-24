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
package com.mycelium.bequant.remote.client

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

typealias AuthGeneratorFun = (authName: String, Request) -> String?

sealed class AuthInterceptor(
    private val authName: String,
    private val generator: AuthGeneratorFun
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val apiKey = generator(authName, chain.request()) ?: return chain.proceed(chain.request())
        return handleApiKey(chain, apiKey)
    }

    protected abstract fun handleApiKey(chain: Interceptor.Chain, apiKey: String): Response
}

class HeaderParamInterceptor(
    authName: String,
    private val paramName: String,
    generator: AuthGeneratorFun
) : AuthInterceptor(authName, generator) {

    override fun handleApiKey(chain: Interceptor.Chain, apiKey: String): Response {
        val newRequest = chain.request()
            .newBuilder()
            .addHeader(paramName, apiKey)
            .build()

        return chain.proceed(newRequest)
    }
}

class QueryParamInterceptor(
    authName: String,
    private val paramName: String,
    generator: AuthGeneratorFun
) : AuthInterceptor(authName, generator) {

    override fun handleApiKey(chain: Interceptor.Chain, apiKey: String): Response {
        val newUrl = chain.request().url
            .newBuilder()
            .addQueryParameter(paramName, apiKey)
            .build()

        val newRequest = chain.request()
            .newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}