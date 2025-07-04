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
import com.mrd.bitlib.model.OutPoint;
import com.mrd.bitlib.model.BitcoinTransaction;
import com.mrd.bitlib.model.BitcoinTransaction.TransactionParsingException;
import com.mrd.bitlib.model.TransactionOutput;
import com.mrd.bitlib.util.ByteReader;
import com.mrd.bitlib.util.Sha256Hash;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.Nullable;

public class TransactionEx implements Serializable, Comparable<TransactionEx> {
   private static final long serialVersionUID = 1L;

   @JsonProperty
   public final Sha256Hash txid;
   @JsonProperty
   public final Sha256Hash hash;
   @JsonProperty
   public final int height; // -1 means unconfirmed
   @JsonProperty
   public final int time;
   @JsonProperty
   public final byte[] binary;

   public TransactionEx(@JsonProperty("txid") Sha256Hash txid, @JsonProperty("txid") Sha256Hash hash, @JsonProperty("height") int height,
                        @JsonProperty("time") int time, @JsonProperty("binary") byte[] binary) {
      this.txid = txid;
      this.hash = hash;
      this.height = height;
      this.time = time;
      this.binary = binary;
   }

   @Override
   public String toString() {
      return "txid:" + txid + " height:" + height + " byte-length: " + binary.length +
              " time:" + new Date(time * 1000L);
   }

   @Override
   public int hashCode() {
      return txid.hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      }
      if (!(obj instanceof TransactionEx)) {
         return false;
      }
      TransactionEx other = (TransactionEx) obj;
      return txid.equals(other.txid);
   }

   public static TransactionEx fromUnconfirmedTransaction(BitcoinTransaction t) {
      int now = (int) (System.currentTimeMillis() / 1000);
      return new TransactionEx(t.getId(), t.getHash(), -1, now, t.toBytes());
   }

   public static BitcoinTransaction toTransaction(TransactionEx tex) {
      if (tex == null) {
         return null;
      }
      try {
         return BitcoinTransaction.fromByteReader(new ByteReader(tex.binary));
      } catch (TransactionParsingException e) {
         return null;
      }
   }

   @Nullable
   public static TransactionOutputEx getTransactionOutput(TransactionEx tex, int index) {
      if (index < 0) {
         return null;
      }
      BitcoinTransaction t = toTransaction(tex);
      if (t == null) {
         return null;
      }
      if (index >= t.outputs.length) {
         return null;
      }
      TransactionOutput output = t.outputs[index];
      return new TransactionOutputEx(new OutPoint(tex.txid, index), tex.height, output.value,
            output.script.getScriptBytes(), t.isCoinbase());
   }

   public int calculateConfirmations(int blockHeight) {
      if (height == -1) {
         return 0;
      } else {
         return Math.max(0, blockHeight - height + 1);
      }
   }

   @Override
   public int compareTo(TransactionEx other) {
      // Make pending transaction have maximum height
      int myHeight = height == -1 ? Integer.MAX_VALUE : height;
      int otherHeight = other.height == -1 ? Integer.MAX_VALUE : other.height;

      if (myHeight < otherHeight) {
         return 1;
      } else if (myHeight > otherHeight) {
         return -1;
      } else {
         // sort by time
         if (time < other.time) {
            return 1;
         } else if (time > other.time) {
            return -1;
         }
         return 0;
      }
   }
}
