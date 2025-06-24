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
package com.mrd.bitlib.model;

import org.junit.Test;

import static com.mrd.bitlib.model.Script.OP_RETURN;
import static org.junit.Assert.*;

import static com.mrd.bitlib.model.ScriptOutputOpReturn.isScriptOutputOpReturn;

public class ScriptOutputOpReturnTest {
   @Test
   public void isScriptOutputOpReturnTest() throws Exception {
      byte[][] chunks = new byte[2][];
      chunks[0]=new byte[]{OP_RETURN};
      assertFalse(isScriptOutputOpReturn(chunks));
      assertTrue(isScriptOutputOpReturn(new byte[][]{{OP_RETURN}, "Hallotest".getBytes()}));
      assertFalse(isScriptOutputOpReturn(new byte[][]{{OP_RETURN}, null}));
      assertFalse(isScriptOutputOpReturn(new byte[][]{{OP_RETURN}, "".getBytes()}));
      assertFalse(isScriptOutputOpReturn(new byte[][]{null, "Hallo".getBytes()}));
   }
}
