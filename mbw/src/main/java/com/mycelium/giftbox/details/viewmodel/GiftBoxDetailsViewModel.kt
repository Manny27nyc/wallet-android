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
package com.mycelium.giftbox.details.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.mycelium.giftbox.client.models.ProductInfo
import com.mycelium.giftbox.client.models.ProductResponse
import com.mycelium.giftbox.common.AmountViewModel
import com.mycelium.giftbox.common.DescriptionViewModel
import com.mycelium.giftbox.getDateTimeString
import com.mycelium.giftbox.model.Card


class GiftBoxDetailsViewModel(application: Application) : AndroidViewModel(application), AmountViewModel, DescriptionViewModel {
    val cardAmount = MutableLiveData<String>()
    val expireDate = MutableLiveData<String>()

    val redeemCode = MutableLiveData<String>("")
    val cardCode = MutableLiveData<String>("")
    val cardPin = MutableLiveData<String>("")

    override val amount = MutableLiveData<String>()
    override val amountFiat = MutableLiveData<String>()
    override val minerFee = MutableLiveData<String>()
    override val date = MutableLiveData<String>()

    override val description = MutableLiveData<String>()
    override val more = MutableLiveData<Boolean>(false)
    override val moreVisible = MutableLiveData<Boolean>(false)
    override val termsLink = MutableLiveData<String?>()
    override val redeemInstruction = MutableLiveData<String?>()
    val expiry = MutableLiveData<String>()
    var productInfo: ProductInfo? = null
    var orderResponse: Card? = null

    fun setCard(card: Card) {
        this.orderResponse = card
        cardAmount.value = "${card.amount} ${card.currencyCode}"
        amount.value = "${card.amount} ${card.currencyCode}"
        date.value = card.timestamp?.getDateTimeString(getApplication<Application>().resources)
        setCodes(card)
    }

    fun setProduct(product: ProductResponse) {
        productInfo = product.product
        description.value = product.product?.description.orEmpty()
        termsLink.value = product.product?.termsAndConditionsPdfUrl
        redeemInstruction.value = product.product?.redeemInstructionsHtml
        expiry.value = if (product.product?.expiryInMonths != null) "${product.product?.expiryDatePolicy} (${product.product?.expiryInMonths} months)" else "Does not expire"
        expireDate.value = expiry.value
    }

    fun setCodes(code: Card) {
        redeemCode.value = code.deliveryUrl
        //        if(URLUtil.isValidUrl(code.code))
//            redeemCode.value =
//    }

        cardCode.value = code.code
        cardPin.value = code.pin
    }
}