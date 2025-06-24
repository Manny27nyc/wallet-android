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
package com.mycelium.wapi.api.lib;

import com.megiontechnologies.Bitcoins;

import java.util.HashMap;
import java.util.Map;

public class FeeEstimationMap extends HashMap<Integer, Bitcoins> {
   public FeeEstimationMap(Map<? extends Integer, ? extends Bitcoins> m) {
      super(m);
   }

   public FeeEstimationMap() {
   }

   public FeeEstimationMap(int initialCapacity) {
      super(initialCapacity);
   }

   public FeeEstimationMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
   }

   public Bitcoins put(Integer key, Bitcoins value, double correction) {
      Bitcoins valueAdjusted = Bitcoins.valueOf((long)((double)value.getLongValue() * correction)) ;
      return super.put(key, valueAdjusted);
   }
}
