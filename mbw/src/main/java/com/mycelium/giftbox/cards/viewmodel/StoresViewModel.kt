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
package com.mycelium.giftbox.cards.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mycelium.giftbox.client.model.MCProductInfo
import com.mycelium.giftbox.common.ListState
import com.mycelium.giftbox.common.ListStateViewModel


class StoresViewModel : ViewModel(), ListStateViewModel {
    var products = mutableListOf<MCProductInfo>()
    var productsSize = MutableLiveData<Long>(0L)
    override val state = MutableLiveData<ListState>(ListState.OK)
    var category: String? = null
    var search = MutableLiveData<String>("")
    var isLoaded = false

//    fun setProductsResponse(it: ProductsResponse?, append: Boolean = false) {
//        if (!append) {
//            products.clear()
//        }
//        products.addAll(it?.products ?: emptyList())
//        productsSize.value = it?.size ?: 0
//        state.value = if (products.isNotEmpty()) ListState.OK else ListState.NOT_FOUND
//    }

    fun setProducts(it: List<MCProductInfo>, append: Boolean = false) {
        if (!append) {
            products.clear()
        }
        products.addAll(it)
        productsSize.value = it.size.toLong()
    }
}