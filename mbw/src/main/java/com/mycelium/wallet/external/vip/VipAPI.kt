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
package com.mycelium.wallet.external.vip

import com.mycelium.wallet.external.DefaultJsonRpcRequest
import com.mycelium.wallet.external.vip.model.ActivateVipRequest
import com.mycelium.wallet.external.vip.model.ActivateVipResponse
import com.mycelium.wallet.external.vip.model.CheckVipResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interface to describing VIP mycelium API for retrofit2 library and providing retrofit object intialization.
 */
interface VipAPI {
    @POST("activate")
    suspend fun activate(@Body body: ActivateVipRequest): ActivateVipResponse

    @POST("check")
    suspend fun check(
        @Body body: DefaultJsonRpcRequest = DefaultJsonRpcRequest(),
    ): CheckVipResponse
}
