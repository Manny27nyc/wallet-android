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
package com.mycelium.wapi.content


class ContentResolver {
    private val uriParsers = mutableListOf<UriParser>()

    fun add(parser: UriParser) {
        uriParsers.add(parser)
    }

    fun resolveUri(content: String): AssetUri? {
        return uriParsers.asSequence().mapNotNull { it.parse(content) }.firstOrNull()
    }
}

interface UriParser {
    fun parse(content: String): AssetUri?
}
