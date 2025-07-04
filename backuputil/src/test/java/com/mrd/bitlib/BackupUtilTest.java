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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class BackupUtilTest {
   private static final String ENCRYPTED_KEY = "xEncGXICZE1_eVYfGWDioNu_8hA6RZzep4XqwPGRtcKb01MDg3s1XFntJYI9Dw";
   private static final String EXPECTED_RESPONSE = "Private key (Wallet Import Format): cRS3zDecX6c8UF9mtmh5vkB8CQ4nCNn1bjPQayXpt3fSLwSPi1LF\n" +
         "                   Bitcoin Address: n4J5FqC89EnV8hikctDs6njmG2cwxS8cM5\n" +
           "                   Bitcoin Address: 2NBzQZLt2MQkJpYGp66b2GQfR5BAQAHtXoU\n" +
           "                   Bitcoin Address: tb1ql8d5hlsgee3qaes32tnqljr4394wes5cnq8yrd";
   private static final String CORRECT_PASSWORD = "QDTDXOYFBXBKKMKR";

   @Test
   public void testParse(){
      BackupUtil util = new BackupUtil(ENCRYPTED_KEY, CORRECT_PASSWORD);
      String decoded = util.getKey();
      assertEquals(EXPECTED_RESPONSE, decoded);
      decoded= new BackupUtil(ENCRYPTED_KEY, "QDTDXOYFBXBKKMK").getKey();
      assertEquals(EXPECTED_RESPONSE, decoded);
   }
   @Test
   public void testErrorHandling(){
      BackupUtil util = new BackupUtil(ENCRYPTED_KEY, "QDTDXOYFBXBKKAA");
      String decoded = util.getKey();
      assertTrue(decoded.startsWith("Error: "));

      util = new BackupUtil(ENCRYPTED_KEY, "QDTDXOYFBXBKKAAA");
      decoded = util.getKey();
      assertTrue(decoded.startsWith("Error: "));

      util = new BackupUtil("xEncGXICZE1_eVYfGWDioAA_8hA6RZzep4XqwPGRtcKb01MDg3s1XFntJYI9Dw", CORRECT_PASSWORD);
      decoded = util.getKey();
      assertTrue(decoded.startsWith("Error: "));
   }
}
