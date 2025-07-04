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
package com.mycelium.wapi.wallet.currency;

import com.google.common.collect.ImmutableMap;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class CurrencySum {
   private Map<String, CurrencyValue> buckets = new HashMap<String, CurrencyValue>();

   public synchronized void add(CurrencyValue valueToAdd) {
      CurrencyValue exactValue = valueToAdd.getExactValueIfPossible();
      CurrencyValue newValue;
      if (!buckets.containsKey(exactValue.getCurrency())) {
         newValue = exactValue;
      } else {
         CurrencyValue existing = buckets.get(exactValue.getCurrency());
         newValue = existing.add(exactValue, null);
      }
      buckets.put(exactValue.getCurrency(), newValue);
   }

   public synchronized void add(CurrencySum sumToAdd) {
      for (CurrencyValue value : sumToAdd.getAllValues().values()) {
         add(value);
      }
   }

   public synchronized Map<String, CurrencyValue> getAllValues() {
      return ImmutableMap.copyOf(buckets);
   }

   public synchronized CurrencyValue getSumAsCurrency(String targetCurrency, ExchangeRateProvider exchangeRateProvider) {
      CurrencyValue sum = ExactCurrencyValue.from(BigDecimal.ZERO, targetCurrency);
      for (Map.Entry<String, CurrencyValue> value : buckets.entrySet()) {
         if (value.getValue() != null) {
            CurrencyValue sumLocal = sum.add(value.getValue(), exchangeRateProvider);
            if(sumLocal.getValue() != null) {
               sum = sumLocal;
            }
         }
      }

      return sum;
   }

}
