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
package com.mycelium.lt.location;


public abstract class Geocoder {
   public abstract GeocodeResponse query(String address, int maxResults) throws RemoteGeocodeException;

   public abstract GeocodeResponse getFromLocation(double latitude, double longitude) throws RemoteGeocodeException;

   protected boolean isValidStatus(String status) {
      return "OK".equals(status) || "ZERO_RESULTS".equals(status);
   }
}
