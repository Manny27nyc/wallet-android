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
package com.mrd.bitlib;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BuildEncodingTest {
   @Test
   public void testChars(){
      assertEquals(3,"ΟΛΩ".length());
      assertEquals("\u039f\u039b\u03a9","ΟΛΩ");
   }
}
