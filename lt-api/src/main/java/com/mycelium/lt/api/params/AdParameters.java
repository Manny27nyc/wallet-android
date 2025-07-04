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

package com.mycelium.lt.api.params;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mycelium.lt.api.model.AdType;
import com.mycelium.lt.api.model.GpsLocation;

//todo rename TadeParameters / TraderParameters confusing
public class AdParameters implements Serializable {
   private static final long serialVersionUID = 1L;

   @JsonProperty
   public AdType type;
   @JsonProperty
   public GpsLocation location;
   @JsonProperty
   public String currency;
   @JsonProperty
   public int minimumFiat;
   @JsonProperty
   public int maximumFiat;
   @JsonProperty
   public String priceFormulaId;
   @JsonProperty
   public double premium;
   @JsonProperty
   public String description;

   public AdParameters(@JsonProperty("type") AdType type, @JsonProperty("location") GpsLocation location,
         @JsonProperty("currency") String currency, @JsonProperty("minimumFiat") int minimumFiat,
         @JsonProperty("maximumFiat") int maximumFiat, @JsonProperty("priceFormulaId") String priceFormulaId,
         @JsonProperty("premium") double premium, @JsonProperty("description") String description) {
      this.type = type;
      this.location = location;
      this.currency = currency;
      this.minimumFiat = minimumFiat;
      this.maximumFiat = maximumFiat;
      this.priceFormulaId = priceFormulaId;
      this.premium = premium;
      this.description = description;
   }

   @SuppressWarnings("unused")
   private AdParameters() {
      // For Jackson
   }
}
