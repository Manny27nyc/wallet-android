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
package com.mycelium.wapi.wallet.btc.bip44;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class ExternalSignatureProviderProxy {

   private final Map<Integer, ExternalSignatureProvider> signatureProviders;

   public ExternalSignatureProviderProxy(ExternalSignatureProvider... signatureProviders) {
      ImmutableMap.Builder<Integer, ExternalSignatureProvider> mapBuilder
            = new ImmutableMap.Builder<Integer, ExternalSignatureProvider>();
      for (ExternalSignatureProvider signatureProvider : signatureProviders) {
         mapBuilder.put(signatureProvider.getBIP44AccountType(), signatureProvider);
      }

      this.signatureProviders = mapBuilder.build();
   }

   public ExternalSignatureProvider get(int bip44AccountType) {
      return signatureProviders.get(bip44AccountType);
   }

}
