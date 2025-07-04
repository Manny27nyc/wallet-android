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
/*
 * Copyright 2013, 2014 Megion Research and Development GmbH
 *
 * Licensed under the Microsoft Reference Source License (MS-RSL)
 *
 * This license governs use of the accompanying software. If you use the software, you accept this license.
 * If you do not accept the license, do not use the software.
 *
 * 1. Definitions
 * The terms "reproduce," "reproduction," and "distribution" have the same meaning here as under U.S. copyright law.
 * "You" means the licensee of the software.
 * "Your company" means the company you worked for when you downloaded the software.
 * "Reference use" means use of the software within your company as a reference, in read only form, for the sole purposes
 * of debugging your products, maintaining your products, or enhancing the interoperability of your products with the
 * software, and specifically excludes the right to distribute the software outside of your company.
 * "Licensed patents" means any Licensor patent claims which read directly on the software as distributed by the Licensor
 * under this license.
 *
 * 2. Grant of Rights
 * (A) Copyright Grant- Subject to the terms of this license, the Licensor grants you a non-transferable, non-exclusive,
 * worldwide, royalty-free copyright license to reproduce the software for reference use.
 * (B) Patent Grant- Subject to the terms of this license, the Licensor grants you a non-transferable, non-exclusive,
 * worldwide, royalty-free patent license under licensed patents for reference use.
 *
 * 3. Limitations
 * (A) No Trademark License- This license does not grant you any rights to use the Licensor’s name, logo, or trademarks.
 * (B) If you begin patent litigation against the Licensor over patents that you think may apply to the software
 * (including a cross-claim or counterclaim in a lawsuit), your license to the software ends automatically.
 * (C) The software is licensed "as-is." You bear the risk of using it. The Licensor gives no express warranties,
 * guarantees or conditions. You may have additional consumer rights under your local laws which this license cannot
 * change. To the extent permitted under your local laws, the Licensor excludes the implied warranties of merchantability,
 * fitness for a particular purpose and non-infringement.
 */

package com.mycelium.wapi.wallet.currency;


import java.util.Objects;

public class CurrencyBasedBalance {
   public final static CurrencyBasedBalance ZERO_BITCOIN_BALANCE = new CurrencyBasedBalance(
         ExactBitcoinValue.ZERO, ExactBitcoinValue.ZERO, ExactBitcoinValue.ZERO);

   public final static CurrencyBasedBalance ZERO_BITCOIN_CASH_BALANCE = new CurrencyBasedBalance(
         ExactBitcoinCashValue.ZERO, ExactBitcoinCashValue.ZERO, ExactBitcoinCashValue.ZERO);

   public final ExactCurrencyValue confirmed;
   public final ExactCurrencyValue sending;
   public final ExactCurrencyValue receiving;
   public final boolean isSynchronizing;

   public CurrencyBasedBalance(ExactCurrencyValue confirmed, ExactCurrencyValue sending, ExactCurrencyValue receiving) {
      this(confirmed, sending, receiving, false);
   }

   public CurrencyBasedBalance(ExactCurrencyValue confirmed, ExactCurrencyValue sending, ExactCurrencyValue receiving, boolean isSynchronizing) {
      this.confirmed = confirmed;
      this.sending = sending;
      this.receiving = receiving;
      this.isSynchronizing = isSynchronizing;
   }

   @Override
   public String toString() {
      return "CurrencyBasedBalance{" +
            "confirmed=" + confirmed +
            ", sending=" + sending +
            ", receiving=" + receiving +
            (isSynchronizing ? " [syncing]" : "") +
            '}';
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      CurrencyBasedBalance that = (CurrencyBasedBalance) o;
      return isSynchronizing == that.isSynchronizing &&
              Objects.equals(confirmed, that.confirmed) &&
              Objects.equals(sending, that.sending) &&
              Objects.equals(receiving, that.receiving);
   }

   @Override
   public int hashCode() {
      return Objects.hash(confirmed, sending, receiving, isSynchronizing);
   }
}
