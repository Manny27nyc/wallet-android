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
import com.mrd.bitlib.model.BitcoinAddress;
import com.mrd.bitlib.model.OutPoint;

import java.io.Serializable;

public class TransactionOutputSummary implements Comparable<TransactionOutputSummary>, Serializable {
   private static final long serialVersionUID = 1L;

   public final OutPoint outPoint;
   public final long value;
   public final int height;
   public final int confirmations;
   @JsonProperty
   public final BitcoinAddress address;

   public TransactionOutputSummary(OutPoint outPoint, long value,
                                   int height, int confirmations,
                                   BitcoinAddress address) {
      this.outPoint = outPoint;
      this.value = value;
      this.height = height;
      this.confirmations = confirmations;
      this.address = address;
   }

   @Override
   public int compareTo(TransactionOutputSummary other) {
      // First sort by confirmations
      if (confirmations < other.confirmations) {
         return 1;
      } else if (confirmations > other.confirmations) {
         return -1;
      } else {
         // Finally sort by value
         if (value < other.value) {
            return 1;
         } else if (value > other.value) {
            return -1;
         }
         return 0;
      }
   }

   @Override
   public int hashCode() {
      return outPoint.hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      }
      if (!(obj instanceof TransactionOutputSummary)) {
         return false;
      }
      TransactionOutputSummary other = (TransactionOutputSummary) obj;
      return other.outPoint.equals(this.outPoint);
   }
}
