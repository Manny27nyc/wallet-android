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
package com.mycelium.giftbox.client

import android.util.Log
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.mycelium.bequant.remote.NullOnEmptyConverterFactory
import com.mycelium.giftbox.client.GiftboxConstants.MC_API_KEY
import com.mycelium.wallet.BuildConfig
import com.mycelium.wallet.configureSSLSocket
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

interface SignatureProvider {
    fun address(): String
    fun signature(data: String): String
}

object RetrofitFactory {
    val objectMapper: ObjectMapper = ObjectMapper()
        .registerKotlinModule()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
        .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
        .configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true)
        .setDateFormat(SimpleDateFormat("yyyy-MM-dd", Locale.US))

    private fun getClientBuilder(signatureProvider: SignatureProvider? = null): OkHttpClient.Builder {

        return OkHttpClient().newBuilder()
            .configureSSLSocket()
            .addInterceptor {
                it.proceed(it.request().newBuilder().apply {
                    addHeader("Content-Type", "application/json")
                    addHeader("Accept-Language", Locale.getDefault().language)
                    addHeader("x-api-key", MC_API_KEY)
//                    addHeader("Authorization", "Basic ${GiftboxPreference.getAccessToken()}")
                    signatureProvider?.run {
                        addHeader("wallet-address", signatureProvider.address())
                        val body = try {
                            Buffer().apply {
                                it.request().body?.writeTo(this)
                            }.readUtf8()
                        } catch (e: Exception) {
                            Log.e("Giftbox", "Error getting signature", e)
                            ""
                        }
                        addHeader("wallet-signature", signatureProvider.signature(body))
                    }
                }.build())
            }
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                }
            }
    }


    fun getRetrofit(url: String, signatureProvider: SignatureProvider? = null): Retrofit =
        Retrofit.Builder()
            .callFactory(object : Call.Factory {
                //create client lazy on demand in background thread
                //see https://www.zacsweers.dev/dagger-party-tricks-deferred-okhttp-init/
                private val client by lazy {
                    getClientBuilder(signatureProvider)
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build()
                }

                override fun newCall(request: Request): Call = client.newCall(request)
            })
            .baseUrl(url)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .build()

}

inline fun <reified T> createApi(
    url: String = GiftboxConstants.ENDPOINT,
    signatureProvider: SignatureProvider? = null
): T =
    RetrofitFactory.getRetrofit(url, signatureProvider).create(T::class.java)
