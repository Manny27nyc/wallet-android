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
package com.mycelium.net;

import com.mrd.bitlib.util.SslUtils;
import com.squareup.okhttp.OkHttpClient;

import javax.net.ssl.SSLSocketFactory;

public class HttpsEndpoint extends HttpEndpoint {
   public final String certificateThumbprint;

   public HttpsEndpoint(String baseUrlString) {
      this(baseUrlString, "");
   }

   public HttpsEndpoint(String baseUrlString, String certificateThumbprint) {
      super(baseUrlString);
      this.certificateThumbprint = certificateThumbprint;
   }

   public SSLSocketFactory getSslSocketFactory(){
      return SslUtils.getSsLSocketFactory(certificateThumbprint);
   }

   @Override
   public OkHttpClient getClient() {
      OkHttpClient client = super.getClient();
      client.setHostnameVerifier(SslUtils.HOST_NAME_VERIFIER_ACCEPT_ALL);
      client.setSslSocketFactory(this.getSslSocketFactory());
      return client;
   }
}
