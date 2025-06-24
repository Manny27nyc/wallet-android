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
package com.mycelium.wapi.wallet.btc.bip44;

import com.mrd.bitlib.UnsignedTransaction;
import com.mrd.bitlib.model.BitcoinTransaction;

/**
 * Hardware wallets provide signatures so accounts can work without the private keys themselves.
 */
public interface ExternalSignatureProvider {
   BitcoinTransaction getSignedTransaction(UnsignedTransaction unsigned, HDAccountExternalSignature forAccount);
   int getBIP44AccountType();
   String getLabelOrDefault();
}
