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
import kotlinx.parcelize.Parcelize

/**
 * List of products, and optionally countries and categories
 * @param categories List of all categories
 * @param countries List of all countries that have products in the catalog
 * @param products List of products
 * @param size Total number of results for the given parameters - can be used for paging
 */
@Parcelize
data class ProductsResponse(
    /* List of all categories */
    @JsonProperty("categories")
    var categories: List<String>? = null,
    /* List of all countries that have products in the catalog */
    @JsonProperty("countries")
    var countries: List<String>? = null,
    /* List of products */
    @JsonProperty("products")
    var products: List<ProductInfo>? = null,
    /* Total number of results for the given parameters - can be used for paging */
    @JsonProperty("size")
    var size: Long = 0
) : Parcelable

