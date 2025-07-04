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
import com.mycelium.wallet.R
import com.mycelium.wallet.WalletApplication
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

/**
 * Extended product information
 * @param availableDenominations For 'fixed' (denomination_type) products. List of available values for product. Display for user to choose from.
 * @param cardImageUrl Url of card image
 * @param categories List of categories this product is listed in
 * @param code Unique product code/id
 * @param countries List of countries this product is available in
 * @param currencyCode Currency code of this product
 * @param denominationType Type of denomination
 * @param description Text description of the product. In html format.
 * @param expiryDatePolicy Product expiration policy info
 * @param expiryInMonths Product months till expiration
 * @param maximumValue Maximum value for this product
 * @param minimumValue Minimum value for this product
 * @param name Product name
 * @param redeemInstructionsHtml Text with redeem instructions. In html format.
 * @param termsAndConditionsPdfUrl URL of pdf file with terms and conditions for product.
 */
@Parcelize

data class ProductInfo(
    /* For 'fixed' (denomination_type) products. List of available values for product. Display for user to choose from. */
    @JsonProperty("available_denominations")
    var availableDenominations: List<BigDecimal>? = null,
    /* Url of card image */
    @JsonProperty("card_image_url")
    var cardImageUrl: String? = null,
    /* List of categories this product is listed in */
    @JsonProperty("categories")
    var categories: List<String>? = null,
    /* Unique product code/id */
    @JsonProperty("code")
    var code: String? = null,
    /* List of countries this product is available in */
    @JsonProperty("countries")
    var countries: List<String>? = null,
    /* Currency code of this product */
    @JsonProperty("currency_code")
    var currencyCode: String? = null,
    /* Type of denomination */
    @JsonProperty("denomination_type")
    var denominationType: DenominationType? = null,
    /* Text description of the product. In html format. */
    @JsonProperty("description")
    var description: String? = null,
    /* Product expiration policy info */
    @JsonProperty("expiry_date_policy")
    var expiryDatePolicy: String? = null,
    /* Product months till expiration */
    @JsonProperty("expiry_in_months")
    var expiryInMonths: kotlin.Int? = null,
    /* Maximum value for this product */
    @JsonProperty("maximum_value")
    var maximumValue: BigDecimal = BigDecimal.ZERO,
    /* Minimum value for this product */
    @JsonProperty("minimum_value")
    var minimumValue: BigDecimal = BigDecimal.ZERO,
    /* Product name */
    @JsonProperty("name")
    var name: String? = null,
    /* Text with redeem instructions. In html format. */
    @JsonProperty("redeem_instructions_html")
    var redeemInstructionsHtml: String? = null,
    /* URL of pdf file with terms and conditions for product. */
    @JsonProperty("terms_and_conditions_pdf_url")
    var termsAndConditionsPdfUrl: String? = null
) : Parcelable {

    /**
     * Type of denomination
     * Values: fixed,open
     */

    enum class DenominationType(val value: String) {
        @JsonProperty(value = "fixed")
        fixed("fixed"),

        @JsonProperty(value = "open")
        `open`("open")
    }
}

fun ProductInfo.getCardValue(): String =
    if (denominationType == ProductInfo.DenominationType.fixed && availableDenominations?.size ?: 100 < 6) {
        availableDenominations!!.joinToString {
            "${it.stripTrailingZeros().toPlainString()} $currencyCode"
        }
    } else {
        WalletApplication.getInstance().getString(R.string.from_s_to_s,
                "${minimumValue?.stripTrailingZeros()?.toPlainString()} $currencyCode",
                "${maximumValue?.stripTrailingZeros()?.toPlainString()} $currencyCode")
    }
