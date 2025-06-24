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
package com.mycelium.wapi.wallet.manager

/**
 * Interface define class as data class for {@link com.mycelium.wapi.wallet.WalletManager#createAccounts(Config)} method,
 * what depending from extended instance creates wallet account.
 * Extended config class may contains private key, public key, address, etc
 * {@link com.mycelium.wapi.wallet.manager.WalletModule} handle this interface and create instance of WalletAccount
 */
interface Config
