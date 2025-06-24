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
package com.mycelium.wallet.external.partner.model

import com.google.gson.annotations.SerializedName


data class MediaFlowContent(@SerializedName("banner-in-list") val bannersInList: List<MediaFlowBannerInList>,
                            @SerializedName("banner-top") val bannersTop: List<MediaFlowBannerBannerTop>,
                            @SerializedName("banner-bottom-details") val bannersDetails: List<MediaFlowDetailsBannerBottom>)

data class MediaFlowBannerInList(val imageUrl: String,
                                 val link: String,
                                 val index: Int) : CommonContent()

data class MediaFlowBannerBannerTop(val imageUrl: String,
                                    val link: String) : CommonContent()

data class MediaFlowDetailsBannerBottom(val imageUrl: String,
                                        val link: String,
                                        val tag: String) : CommonContent()