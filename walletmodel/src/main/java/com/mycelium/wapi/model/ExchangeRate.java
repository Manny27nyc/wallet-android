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
 * Copyright 2013, 2014 Megion Research & Development GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mycelium.wapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

public class ExchangeRate implements Serializable {
   private static final long serialVersionUID = 1L;

   @JsonProperty
   public final String name;
   @JsonProperty
   public final long time;
   @JsonProperty
   public final String currency;
   @JsonProperty
   public final Double price; // null if price is not available

   public String fromCurrency;

   public ExchangeRate(
           @JsonProperty("name") String name,
           @JsonProperty("time") long time,
           @JsonProperty("price") double price,
           @JsonProperty("currency") String currency) {
      this.name = name;
      this.time = time;
      this.currency = currency;
      this.price = price;
   }

   public ExchangeRate(
           String name,
           long time,
           double price,
           String fromCurrency,
           String toCurrency) {
      this(name, time, price, toCurrency);
      this.fromCurrency = fromCurrency;
   }

   public static ExchangeRate missingRate(String name, long time, String currency) {
      return new ExchangeRate(name, time, currency);
   }

   //explicit parameter instead of passing null, proguard did remove the null parameter otherwise
   private ExchangeRate(String name, long time, String currency) {
      this.name = name;
      this.time = time;
      this.currency = currency;
      this.price = null;
   }

   @Override
   public String toString() {
      return "Name: " + name +
              " time: " + new Date(time) +
              " currency: " + currency +
              " price: " + (price == null ? "<Not available>" : price);
   }

   @Override
   public int hashCode() {
      return name.hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      }
      if (!(obj instanceof ExchangeRate)) {
         return false;
      }
      ExchangeRate other = (ExchangeRate) obj;
      return other.time == time && other.name.equals(name) && other.currency.equals(currency);
   }
}
