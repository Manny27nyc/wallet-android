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

import java.io.Serializable


data class MainMenuContent(val pages: List<MainMenuPage>)

data class MainMenuPage(val tabName: String,
                        val tabIndex: Int,
                        val imageUrl: String,
                        val link: String) : CommonContent(), Serializable