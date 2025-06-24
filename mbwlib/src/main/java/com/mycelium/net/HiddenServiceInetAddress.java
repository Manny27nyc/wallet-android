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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.Inet4Address;
import java.net.InetAddress;

public class HiddenServiceInetAddress  {

  public static InetAddress getInstance(String host){
     // hacky: call the private constructor of Inet4Address to get an instance
     // with a hostname and a hardcoded IP without trying to resolve it via DNS
     Constructor<?> constructor = Inet4Address.class.getDeclaredConstructors()[0];
     byte[] ip = {0,0,0,0};
     try {
        constructor.setAccessible(true);
        return (InetAddress) constructor.newInstance(ip,host);
     } catch (InstantiationException e) {
        throw new RuntimeException(e);
     } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
     } catch (InvocationTargetException e) {
        throw new RuntimeException(e);
     }
  }
}
