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
package com.mycelium.bequant.remote

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

fun <T, R> doRequestModify(
    coroutineScope: CoroutineScope,
    request: suspend () -> Response<T>,
    successBlock: (R?) -> Unit,
    errorBlock: ((Int, String) -> Unit)? = null,
    finallyBlock: (() -> Unit)? = null,
    responseModifier: ((T?) -> R?)?
): Job =
    coroutineScope.launch {
        withContext(Dispatchers.IO) {
            try {
                val response = request()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        successBlock(responseModifier?.invoke(response.body()))
                    } else {
                        @Suppress("BlockingMethodInNonBlockingContext")
                        errorBlock?.invoke(response.code(), response.errorBody()?.string() ?: "")
                    }
                }
            } catch (e: Exception) {
                Log.w("Request", "exception on request", e)
                withContext(Dispatchers.Main) {
                    errorBlock?.invoke(400, e.message ?: "")
                }
            }
        }
    }.apply {
        invokeOnCompletion {
            finallyBlock?.invoke()
        }
    }

fun <T> doRequest(
    coroutineScope: CoroutineScope,
    request: suspend () -> Response<T>,
    successBlock: (T?) -> Unit,
    errorBlock: ((Int, String) -> Unit)? = null,
    finallyBlock: (() -> Unit)? = null,
    responseModifier: ((T?) -> T?)? = { it }
): Job =
    doRequestModify(coroutineScope, request, successBlock, errorBlock, finallyBlock, responseModifier)