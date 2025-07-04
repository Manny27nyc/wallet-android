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

package com.mycelium.wallet.lt.activity;

import java.math.BigDecimal;
import java.util.*;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.mrd.bitlib.StandardTransactionBuilder.InsufficientBtcException;
import com.mrd.bitlib.StandardTransactionBuilder.BtcOutputTooSmallException;
import com.mrd.bitlib.StandardTransactionBuilder.UnableToBuildTransactionException;
import com.mrd.bitlib.UnsignedTransaction;
import com.mrd.bitlib.model.BitcoinAddress;
import com.mycelium.lt.api.model.PriceFormula;
import com.mycelium.lt.api.model.TradeSession;
import com.mycelium.wallet.Constants;
import com.mycelium.wallet.MbwManager;
import com.mycelium.wallet.R;
import com.mycelium.wallet.Utils;
import com.mycelium.wapi.wallet.WalletAccount;
import com.mycelium.wapi.wallet.btc.BtcReceiver;
import com.mycelium.wapi.wallet.btc.WalletBtcAccount;

import static com.mrd.bitlib.TransactionUtils.MINIMUM_OUTPUT_VALUE;

public class TradeActivityUtil {

   public static boolean canAffordTrade(TradeSession ts, MbwManager mbwManager) {
      WalletAccount account = mbwManager.getSelectedAccount();

      if (!account.canSpend()) {
         // this is a watch-only account
         return false;
      }
      BitcoinAddress address = BitcoinAddress.getNullAddress(mbwManager.getNetwork());
      BtcReceiver receiver = null;
      receiver = new BtcReceiver(address, ts.satoshisFromSeller);
      try {
         long estimatedFee = mbwManager
                 .getFeeProvider(account.getCoinType())
                 .getEstimation()
                 .getNormal()
                 .getValueAsLong();
         ((WalletBtcAccount)account).createUnsignedTransaction(Collections.singletonList(receiver), estimatedFee);
      } catch (BtcOutputTooSmallException e) {
         throw new RuntimeException(e);
      } catch (InsufficientBtcException e) {
         return false;
      } catch (UnableToBuildTransactionException e) {
         return false;
      }
      return true;
   }

   public static UnsignedTransaction createUnsignedTransaction(long satoshisFromSeller, long satoshisForBuyer,
                                                               BitcoinAddress buyerAddress, BitcoinAddress feeAddress, WalletAccount acc, long minerFeeToUse) {
      Preconditions.checkArgument(satoshisForBuyer > MINIMUM_OUTPUT_VALUE);
      Preconditions.checkArgument(satoshisFromSeller >= satoshisForBuyer);
      long localTraderFee = satoshisFromSeller - satoshisForBuyer;
      List<BtcReceiver> receiver = new ArrayList<>();
      receiver.add(new BtcReceiver(buyerAddress, satoshisForBuyer));
      if (localTraderFee >= MINIMUM_OUTPUT_VALUE) {
         receiver.add(new BtcReceiver(feeAddress, localTraderFee));
      }
      try {
         return ((WalletBtcAccount)acc).createUnsignedTransaction(receiver, minerFeeToUse);
      } catch (BtcOutputTooSmallException | InsufficientBtcException | UnableToBuildTransactionException e) {
         throw new RuntimeException(e);
      }
   }

   public static void populatePriceDetails(Context context, View root, boolean isBuyer, boolean isSelf,
         String currency, PriceFormula priceFormula, long satoshisAtMarketPrice, long satoshisTraded, int fiatTraded,
         MbwManager mbwManager) {
      Locale locale = new Locale("en", "US");

      // Price
      double oneBtcPrice = (double) fiatTraded * Constants.ONE_BTC_IN_SATOSHIS / (double) satoshisTraded;
      String price = Utils.getFiatValueAsString(Constants.ONE_BTC_IN_SATOSHIS, oneBtcPrice);
      ((TextView) root.findViewById(R.id.tvPriceValue)).setText(context.getResources().getString(R.string.lt_btc_price,
            price, currency));

      String fiatAmountString = String.format(locale, "%s %s", fiatTraded, currency);
      String btcAmountString = mbwManager.getBtcValueString(satoshisTraded);

      // You Pay / You Get Values
      if (isBuyer) {
         ((TextView) root.findViewById(R.id.tvPayValue)).setText(fiatAmountString);
         ((TextView) root.findViewById(R.id.tvGetValue)).setText(btcAmountString);
      } else {
         ((TextView) root.findViewById(R.id.tvPayValue)).setText(btcAmountString);
         ((TextView) root.findViewById(R.id.tvGetValue)).setText(fiatAmountString);
      }
      // You Pay / You Get Labels
      if (isSelf) {
         if (isBuyer) {
            ((TextView) root.findViewById(R.id.tvPriceLabel)).setText(R.string.lt_you_buy_at_label);
         } else {
            ((TextView) root.findViewById(R.id.tvPriceLabel)).setText(R.string.lt_you_sell_at_label);
         }
         ((TextView) root.findViewById(R.id.tvPayLabel)).setText(R.string.lt_you_pay_label);
         ((TextView) root.findViewById(R.id.tvGetLabel)).setText(R.string.lt_you_get_label);
      } else {
         if (isBuyer) {
            ((TextView) root.findViewById(R.id.tvPriceLabel)).setText(R.string.lt_buyer_buys_at_label);
            ((TextView) root.findViewById(R.id.tvPayLabel)).setText(R.string.lt_buyer_pays_label);
            ((TextView) root.findViewById(R.id.tvGetLabel)).setText(R.string.lt_buyer_gets_label);
         } else {
            ((TextView) root.findViewById(R.id.tvPriceLabel)).setText(R.string.lt_seller_sells_at_label);
            ((TextView) root.findViewById(R.id.tvPayLabel)).setText(R.string.lt_seller_pays_label);
            ((TextView) root.findViewById(R.id.tvGetLabel)).setText(R.string.lt_seller_gets_label);
         }
      }

   }

   @SuppressWarnings("unused")
   private static double calculatePremium(long satoshisTraded, long satoshisAtMarketPrice) {
      double traded = (double) satoshisTraded;
      double market = (double) satoshisAtMarketPrice;
      double premium = 1D - traded / market;
      double premiumInPercent = premium * 100;
      return premiumInPercent;
   }

   @SuppressWarnings("unused")
   private static double roundDoubleHalfUp(double value, int decimals) {
      return BigDecimal.valueOf(value).setScale(decimals, BigDecimal.ROUND_HALF_UP).doubleValue();
   }

}
