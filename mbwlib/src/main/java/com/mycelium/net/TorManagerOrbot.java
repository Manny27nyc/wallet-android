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


import com.squareup.okhttp.OkHttpClient;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;

import info.guardianproject.netcipher.proxy.OrbotHelper;

public class TorManagerOrbot extends TorManager {

   public TorManagerOrbot() {
      startClient();
   }

   @Override
   public void startClient(){
      // check if orbot is running - somehow
      setInitState("Checking Orbot", 1);
   }


   @Override
   public void stopClient(){

   }

   @Override
   public OkHttpClient setupClient(OkHttpClient client) {
      SocketAddress proxyAddress = new InetSocketAddress("127.0.0.1", OrbotHelper.DEFAULT_PROXY_HTTP_PORT);
      client.setProxy(new Proxy(Proxy.Type.HTTP, proxyAddress));
      return client;
   }

   @Override
   public void resetInterface() {
      setInitState("Reset", 1);
   }
}
