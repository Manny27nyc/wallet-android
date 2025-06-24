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
package com.mycelium.wallet.external.changelly.bch

import com.mycelium.wallet.MbwManager
import com.mycelium.wapi.wallet.WalletAccount
import java.math.BigDecimal


//TODO: call estimateFeeFromTransferrableAmount need refactoring, we should call account object
fun WalletAccount<*>.estimateFeeFromTransferrableAmount(mbwManager: MbwManager, amount: Long): BigDecimal? {
    return BigDecimal.valueOf(0)
}
