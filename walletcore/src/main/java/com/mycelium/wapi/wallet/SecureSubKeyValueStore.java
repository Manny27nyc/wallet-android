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
package com.mycelium.wapi.wallet;

import com.mrd.bitlib.crypto.RandomSource;

// SubKeystore - stores data in its accountBacking but prefixes all Ids with its own subId
// Use it to store unrelated HD-Accounts in one accountBacking
public class SecureSubKeyValueStore extends SecureKeyValueStore {
   private final int subId;

   public SecureSubKeyValueStore(SecureKeyValueStoreBacking backing, RandomSource randomSource, int subId) {
      super(backing, randomSource);
      this.subId = subId;
   }

   @Override
   protected synchronized byte[] getValue(byte[] realId){
      return _backing.getValue(realId, subId);
   }

   @Override
   protected synchronized void setValue(byte[] realId, byte[] value){
      _backing.setValue(realId, subId, value);
   }

   public int getSubId() {
      return subId;
   }

   public void deleteAllData() {
      _backing.deleteSubStorageId(subId);
   }
}
