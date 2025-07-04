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
 * API
 * Create API keys in your profile and use public API key as username and secret as password to authorize.
 *
 * The version of the OpenAPI document: 2.19.0
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package com.mycelium.bequant.remote.trading.model


import com.fasterxml.jackson.annotation.JsonProperty

/**
 *
 * @param id
 * @param clientOrderId
 * @param orderId
 * @param symbol
 * @param side
 * @param quantity
 * @param fee
 * @param price
 * @param timestamp
 * @param positionId Margin position
 * @param pnl Margin profit or loss in currency
 * @param liquidation
 */

data class Trade(
        @JsonProperty("id")
        val id: kotlin.Long,
        @JsonProperty("clientOrderId")
        val clientOrderId: kotlin.String,
        @JsonProperty("orderId")
        val orderId: kotlin.Long,
        @JsonProperty("symbol")
        val symbol: kotlin.String,
        @JsonProperty("side")
        val side: Trade.Side,
        @JsonProperty("quantity")
        val quantity: kotlin.String,
        @JsonProperty("fee")
        val fee: kotlin.String,
        @JsonProperty("price")
        val price: kotlin.String,
        @JsonProperty("timestamp")
        val timestamp: java.util.Date,
        /* Margin position */
        @JsonProperty("positionId")
        val positionId: java.math.BigDecimal? = null,
        /* Margin profit or loss in currency */
        @JsonProperty("pnl")
        val pnl: kotlin.Double? = null,
        @JsonProperty("liquidation")
        val liquidation: kotlin.Boolean? = null
) {

    /**
     *
     * Values: sell,buy
     */

    enum class Side(val value: kotlin.String) {
        @JsonProperty("sell")
        sell("sell"),
        @JsonProperty("buy")
        buy("buy");
    }
}

