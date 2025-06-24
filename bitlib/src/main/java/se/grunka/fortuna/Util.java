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
package se.grunka.fortuna;

public class Util {
    public static byte[] twoLeastSignificantBytes(long value) {
        byte[] result = new byte[2];
        result[0] = (byte) (value & 0xff);
        result[1] = (byte) ((value & 0xff00) >> 8);
        return result;
    }

    public static int ceil(int value, int divisor) {
        return (value / divisor) + (value % divisor == 0 ? 0 : 1);
    }

   public static byte[] arrayCopyOf(byte[] seed, int seedLength) {
      byte[] copy = new byte[seedLength];
      System.arraycopy(seed, 0, copy, 0, Math.min(seed.length, seedLength));
      return copy;
   }
}
