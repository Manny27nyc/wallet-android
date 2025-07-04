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
 * Gift Cards Shop API
 * Products catalog, checkout and orders API - allows realtime purchase of products
 *
 * The version of the OpenAPI document: 1.0
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package com.mycelium.giftbox.client.models


import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

/**
 * Giftcard code, can be any of `code`, `code+pin`, `delivery_url`
 * @param amount
 * @param code
 * @param deliveryUrl
 * @param expiryDate
 * @param pin
 */
@Parcelize

data class Ecode(
    @JsonProperty("amount")
    var amount: String? = null,
    @JsonProperty("code")
    var code: String? = null,
    @JsonProperty("delivery_url")
    var deliveryUrl: String? = null,
    @JsonProperty("expiry_date")
    var expiryDate: String? = null,
    @JsonProperty("pin")
    var pin: String? = null
) : Parcelable

