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
package com.mycelium.wallet.activity.util;

import com.mycelium.wapi.wallet.WalletAccount;
import com.mycelium.wapi.wallet.bch.bip44.Bip44BCHAccount;
import com.mycelium.wapi.wallet.bch.single.SingleAddressBCHAccount;
import com.mycelium.wapi.wallet.btc.bip44.HDAccount;
import com.mycelium.wapi.wallet.btc.single.SingleAddressAccount;
import com.mycelium.wapi.wallet.colu.ColuAccount;

public enum AccountDisplayType {
    BTC_ACCOUNT("BTC"),
    BCH_ACCOUNT("BCH"),
    DASH_ACCOUNT("DASH"),
    COLU_ACCOUNT("COLU"),
    UNKNOWN_ACCOUNT("UNKNOWN");

    private final String accountLabel;

    AccountDisplayType(String accountLabel) {
        this.accountLabel = accountLabel;
    }

    public static AccountDisplayType getAccountType(WalletAccount account) {
        if (account instanceof HDAccount || account instanceof SingleAddressAccount) {
            return BTC_ACCOUNT;
        }
        if (account instanceof Bip44BCHAccount || account instanceof SingleAddressBCHAccount) {
            return BCH_ACCOUNT;
        }
        if (account instanceof ColuAccount) {
            return COLU_ACCOUNT;
        }
        return UNKNOWN_ACCOUNT;
    }

    public String getAccountLabel() {
        return accountLabel;
    }
}
