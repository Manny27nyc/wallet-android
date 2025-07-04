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
package com.mycelium.wapi.api.jsonrpc

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.stream.JsonReader
import java.io.BufferedReader
import java.lang.reflect.Type

const val JSON_RPC_VERSION = "2.0"
const val JSON_RPC_IDENTIFIER = "jsonrpc"
const val METHOD_KEY = "method"
const val PARAMS_KEY = "params"
const val ID_KEY = "id"

const val DATA_KEY = "data"
const val MESSAGE_KEY = "message"
const val METHOD_NOT_FOUND_KEY = "method not found"
const val PARSE_ERROR_KEY = "parse error"
const val INVALID_REQUEST_KEY = "invalid request"
const val CODE_KEY = "code"
const val ERROR_KEY = "error"
const val RESULT_KEY = "result"

val NO_ID = Unit

const val METHOD_NOT_FOUND_CODE = -32601
const val PARSE_ERROR_CODE = -32700
const val INVALID_REQUEST_CODE = -32600
const val INVALID_PARAMS_CODE = -32602
const val INTERNAL_ERROR_CODE = -32603

object RPC {
    private val builder = GsonBuilder()
            .registerTypeAdapter(RpcParams::class.java, RpcParamsTypeAdapter())
            .serializeNulls()

    val jsonParser: Gson by lazy {
        builder.create()
    }

    fun toJson(`object`: Any?): String = jsonParser.toJson(`object`)

    internal fun toJsonTree(any: Any): JsonElement = jsonParser.toJsonTree(any)

    fun <T> fromJson(json: String, type: Class<T>): T = jsonParser.fromJson(json, type)

    @JvmStatic
    fun registerTypeAdapter(type: Type, typeAdapter: Any) {
        builder.registerTypeAdapter(type, typeAdapter)
    }
}
