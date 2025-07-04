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
/**
 * Auth API
 * Auth API<br> <a href='/changelog'>Changelog</a>
 *
 * The version of the OpenAPI document: v0.0.50
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package com.mycelium.bequant.remote.client.apis

import com.mycelium.bequant.BequantConstants
import com.mycelium.bequant.remote.client.createApi
import com.mycelium.bequant.remote.client.models.ApiKey
import com.mycelium.bequant.remote.client.models.ApiKeyDeleteRequest
import com.mycelium.bequant.remote.client.models.ApiKeyRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiKeyApi {

    /**
     * Get API key
     * Get API key &lt;br&gt;&lt;b style&#x3D;\&quot;color:red;\&quot;&gt;Bearer access token required!&lt;/b&gt;
     * Responses:
     *  - 200: OK
     *  - 401: Api key is missing or invalid
     *  - 403: Api key is missing or invalid
     *  - 420: Session issues
     *  - 421: Token expired
     *  - 0: error
     *
     * @param apiKeyRequest  (optional)
     * @return ApiKey
     */
    @POST("api-key")

    suspend fun postApiKey(
            @Body apiKeyRequest: ApiKeyRequest = ApiKeyRequest()
    ): Response<ApiKey>

    /**
     * Delete API key
     * Delete API key &lt;br&gt;&lt;b style&#x3D;\&quot;color:red;\&quot;&gt;Bearer access token required!&lt;/b&gt;
     * Responses:
     *  - 200: OK
     *  - 401: Api key is missing or invalid
     *  - 403: Api key is missing or invalid
     *  - 420: Session issues
     *  - 421: Token expired
     *  - 0: error
     *
     * @param apiKeyDeleteRequest  (optional)
     * @return void
     */
    @POST("api-key/delete")

    suspend fun postApiKeyDelete(
            @Body apiKeyDeleteRequest: ApiKeyDeleteRequest? = null
    ): Response<Unit>

    companion object {
        fun create(): ApiKeyApi = createApi(BequantConstants.AUTH_ENDPOINT, true)
    }
}
