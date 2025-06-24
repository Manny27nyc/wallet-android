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

package com.mycelium.lt.api.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BtcSellPrice implements Serializable {
   private static final long serialVersionUID = 1L;

   @JsonProperty
   public final String currency;
   @JsonProperty
   public final int fiatTraded;
   @JsonProperty
   public final PriceFormula priceFormula;
   @JsonProperty
   public final long satoshisAtMarketPrice;
   @JsonProperty
   public final long satoshisFromSeller;
   @JsonProperty
   public final long satoshisForBuyer;

   public BtcSellPrice(@JsonProperty("currency") String currency, @JsonProperty("fiatTraded") int fiatTraded,
         @JsonProperty("priceFormula") PriceFormula priceFormula,
         @JsonProperty("satoshisAtMarketPrice") long satoshisAtMarketPrice,
         @JsonProperty("satoshisFromSeller") long satoshisFromSeller,
         @JsonProperty("satoshisForBuyer") long satoshisForBuyer) {
      this.currency = currency;
      this.fiatTraded = fiatTraded;
      this.satoshisAtMarketPrice = satoshisAtMarketPrice;
      this.priceFormula = priceFormula;
      this.satoshisFromSeller = satoshisFromSeller;
      this.satoshisForBuyer = satoshisForBuyer;
   }

}
