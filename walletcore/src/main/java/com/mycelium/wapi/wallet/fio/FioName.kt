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
package com.mycelium.wapi.wallet.fio

import fiofoundation.io.fiosdk.isFioAddress

/**
 * @param nameDomainString the fio name as "name@domain"
 */
class FioName(nameDomainString: String) {
    val name: String
    val domain: String

    constructor(name: String, domain: String): this("$name@$domain")

    init {
        if (!nameDomainString.isFioAddress()) {
            throw IllegalArgumentException("$nameDomainString is not a valid FioName.")
        }
        val parts = nameDomainString.split("@")
        name = parts[0]
        domain = parts[1]
    }

    override fun toString() = "$name@$domain"
}