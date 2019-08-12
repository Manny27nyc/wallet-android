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

package com.mycelium.wallet;

import com.google.api.client.util.Lists;
import com.mycelium.view.Denomination;
import com.mycelium.wallet.exchange.ValueSum;
import com.mycelium.wapi.model.ExchangeRate;
import com.mycelium.wapi.wallet.bch.coins.BchMain;
import com.mycelium.wapi.wallet.bch.coins.BchTest;
import com.mycelium.wapi.wallet.coinapult.Currency;
import com.mycelium.wapi.wallet.coins.GenericAssetInfo;
import com.mycelium.wapi.wallet.coins.Value;
import com.mycelium.wapi.wallet.fiat.coins.FiatType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class CurrencySwitcher {
    private final ExchangeRateManager exchangeRateManager;

    private List<GenericAssetInfo> fiatCurrencies;
    private List<GenericAssetInfo> walletCurrencies;
    private Denomination denomination;

    // the last selected/shown fiat currency
    private GenericAssetInfo currentFiatCurrency;

    // the last shown currency (usually same as fiat currency, but in some spots we cycle through all currencies including Bitcoin)
    private GenericAssetInfo currentCurrency;
    private GenericAssetInfo defaultCurrency = Utils.getBtcCoinType();

   public CurrencySwitcher(final ExchangeRateManager exchangeRateManager, final Set<GenericAssetInfo> fiatCurrencies
           , GenericAssetInfo currentCurrency, final Denomination denomination) {
      this.exchangeRateManager = exchangeRateManager;
      ArrayList<GenericAssetInfo> currencies = Lists.newArrayList(fiatCurrencies);
      Collections.sort(currencies, new Comparator<GenericAssetInfo>() {
          @Override
          public int compare(GenericAssetInfo cryptoCurrency, GenericAssetInfo t1) {
              return cryptoCurrency.getSymbol().compareTo(t1.getSymbol());
          }
      });
      this.fiatCurrencies = currencies;
      this.denomination = denomination;

      this.currentCurrency = currentCurrency;

      // if BTC is selected or current currency is not in list of available currencies (e.g. after update)
      // select a default one or none
      if (currentCurrency.equals(Utils.getBtcCoinType())
              || currentCurrency.equals(BchMain.INSTANCE) || currentCurrency.equals(BchTest.INSTANCE)
              || !fiatCurrencies.contains(currentCurrency)) {
         if (fiatCurrencies.size() == 0) {
            this.currentFiatCurrency = null;  // no fiat currency selected
         } else {
            this.currentFiatCurrency = currencies.get(0);
         }
      } else {
         this.currentFiatCurrency = currentCurrency;
      }
   }

   public ExchangeRateManager getExchangeRateManager() {
      return exchangeRateManager;
   }

   public void setCurrency(GenericAssetInfo setToCurrency) {
      if (isFiatCurrency(setToCurrency)) {
         currentFiatCurrency = setToCurrency;
      }
      currentCurrency = setToCurrency;
   }

   public boolean isFiatCurrency(GenericAssetInfo currency) {
      return currency instanceof FiatType;
   }

   public GenericAssetInfo getDefaultCurrency() {
      return defaultCurrency;
   }

   public GenericAssetInfo getCurrentFiatCurrency() {
      return currentFiatCurrency;
   }

   public GenericAssetInfo getCurrentCurrency() {
      return currentCurrency;
   }

    public String getCurrentCurrencyIncludingDenomination() {
        if (currentCurrency instanceof FiatType || currentCurrency instanceof Currency) {
            return currentCurrency.getSymbol();
        } else {
            return denomination.getUnicodeString(currentCurrency.getSymbol());
        }
    }

   public List<GenericAssetInfo> getCurrencyList(GenericAssetInfo ... additions) {
      //make a copy to prevent others from changing our internal list
      List<GenericAssetInfo> result = new ArrayList<>(fiatCurrencies);
      Collections.addAll(result, additions);
      return result;
   }

    public List<GenericAssetInfo> getCurrencyList(List<GenericAssetInfo> additions) {
        return getCurrencyList(additions.toArray(new GenericAssetInfo[0]));
    }

   public void setCurrencyList(final Set<GenericAssetInfo> fiatCurrencies) {
      // convert the set to a list and sort it
      ArrayList<GenericAssetInfo> currencies = Lists.newArrayList(fiatCurrencies);
      Collections.sort(currencies, new Comparator<GenericAssetInfo>() {
          @Override
          public int compare(GenericAssetInfo abstractAsset, GenericAssetInfo t1) {
              return abstractAsset.getSymbol().compareTo(t1.getSymbol());
          }
      });

      //if we de-selected our current active currency, we switch it
      if (!currencies.contains(currentFiatCurrency)) {
         if (currencies.isEmpty()) {
            //no fiat
            setCurrency(null);
         } else {
            setCurrency(currencies.get(0));
         }
      }
      //copy to prevent changes by caller
      this.fiatCurrencies = new ArrayList<>(currencies);
   }

   public void setDefaultCurrency(GenericAssetInfo currency) {
//      Set<GenericAssetInfo> currencies = new HashSet<>(getCurrencyList());
//      if (!defaultCurrency.equals(currency.getSymbol())) {
//         currencies.remove(defaultCurrency);
//         currencies.add(currency);
//      }
      defaultCurrency = currency;
   }

    public List<GenericAssetInfo> getWalletCurrencies() {
        return walletCurrencies;
    }

    public void setWalletCurrencies(List<GenericAssetInfo> walletCurrencies) {
        this.walletCurrencies = walletCurrencies;
    }

    public GenericAssetInfo getNextCurrency(boolean includeBitcoin) {
      List<GenericAssetInfo> currencies = getCurrencyList();

      //just to be sure we dont cycle through a single one
      if (!includeBitcoin && currencies.size() <= 1) {
         return currentFiatCurrency;
      }

      int index = currencies.indexOf(currentCurrency);
      index++; //hop one forward

      if (index >= currencies.size()) {
         // we are at the end of the fiat-list. return BTC if we should include Bitcoin, otherwise wrap around
         if (includeBitcoin) {
            // only set currentCurrency, but leave currentFiat currency as it was
            currentCurrency = defaultCurrency;
         } else {
            index -= currencies.size(); //wrap around
            currentCurrency = currencies.get(index);
            currentFiatCurrency = currentCurrency;
         }
      } else {
         currentCurrency = currencies.get(index);
         currentFiatCurrency = currentCurrency;
      }

      exchangeRateManager.requestOptionalRefresh();

      return currentCurrency;
   }

   public Denomination getDenomination() {
      return denomination;
   }

   public void setDenomination(Denomination _denomination) {
      this.denomination = _denomination;
   }

   public boolean isFiatExchangeRateAvailable() {
      if (currentFiatCurrency == null) {
         // we dont even have a fiat currency...
         return false;
      }

      // check if there is a rate available
      ExchangeRate rate = exchangeRateManager.getExchangeRate(getCurrentFiatCurrency().getSymbol());
      return rate != null && rate.price != null;
   }


    public Value getAsFiatValue(Value value) {
        if (value == null) {
            return null;
        }
        if (currentFiatCurrency == null) {
            return null;
        }
        return exchangeRateManager.get(value, getCurrentFiatCurrency());
    }

   /**
    * Get the exchange rate price for the currently selected currency.
    * <p>
    * Returns null if the current rate is too old or for a different currency.
    * In that the case the caller could choose to call refreshRates() and supply a handler to get a callback.
    */
   public synchronized Double getExchangeRatePrice() {
      ExchangeRate rate = exchangeRateManager.getExchangeRate(currentFiatCurrency.getSymbol());
      return rate == null ? null : rate.price;
   }

    public Value getValue(ValueSum sum) {
        Value result = Value.zeroValue(getCurrentCurrency());
        for (Value value : sum.getValues()) {
            Value value1 = exchangeRateManager.get(value, result.type);
            if (value1 != null) {
                result = result.plus(value1);
            }
        }
        return result;
    }
}
