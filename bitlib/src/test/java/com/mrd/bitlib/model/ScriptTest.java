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
/*
 * Copyright 2013 Megion Research and Development GmbH
 *
 * Licensed under the Microsoft Reference Source License (MS-RSL)
 *
 * This license governs use of the accompanying software. If you use the software, you accept this license.
 * If you do not accept the license, do not use the software.
 *
 * 1. Definitions
 * The terms "reproduce," "reproduction," and "distribution" have the same meaning here as under U.S. copyright law.
 * "You" means the licensee of the software.
 * "Your company" means the company you worked for when you downloaded the software.
 * "Reference use" means use of the software within your company as a reference, in read only form, for the sole purposes
 * of debugging your products, maintaining your products, or enhancing the interoperability of your products with the
 * software, and specifically excludes the right to distribute the software outside of your company.
 * "Licensed patents" means any Licensor patent claims which read directly on the software as distributed by the Licensor
 * under this license.
 *
 * 2. Grant of Rights
 * (A) Copyright Grant- Subject to the terms of this license, the Licensor grants you a non-transferable, non-exclusive,
 * worldwide, royalty-free copyright license to reproduce the software for reference use.
 * (B) Patent Grant- Subject to the terms of this license, the Licensor grants you a non-transferable, non-exclusive,
 * worldwide, royalty-free patent license under licensed patents for reference use.
 *
 * 3. Limitations
 * (A) No Trademark License- This license does not grant you any rights to use the Licensor’s name, logo, or trademarks.
 * (B) If you begin patent litigation against the Licensor over patents that you think may apply to the software
 * (including a cross-claim or counterclaim in a lawsuit), your license to the software ends automatically.
 * (C) The software is licensed "as-is." You bear the risk of using it. The Licensor gives no express warranties,
 * guarantees or conditions. You may have additional consumer rights under your local laws which this license cannot
 * change. To the extent permitted under your local laws, the Licensor excludes the implied warranties of merchantability,
 * fitness for a particular purpose and non-infringement.
 */

package com.mrd.bitlib.model;

import com.mrd.bitlib.util.ByteReader;
import com.mrd.bitlib.util.HexUtils;
import org.junit.Assert;
import org.junit.Test;

public class ScriptTest {

   @Test
   public void chunkTest() throws Script.ScriptParsingException {
      final String TEST_SCRIPT="5321033e20dea007b39688c6e97427a65aeedd0b47fde14e96631bb0330a403663150c2103b5058635d91ae26306140b90673b89616062c59a80301622f4ed6f8050c65a7f2103c04f34973eee8485e21a014268908d36510ed6f149c0ec3331aa521c6cc3929c2103d87a6d71ab19fda40947450ed2f2240dedc728f9bd373a28a43a93db1677159f54ae";
      Script.chunksFromScriptBytes(HexUtils.toBytes(TEST_SCRIPT));
   }

   @Test
   public void parseTransactionTest() throws BitcoinTransaction.TransactionParsingException {
      final String P2SH_MULTISIG_TRANSACTION ="010000000123d773a6dff771f9e32566b1d3fb08dcd9a0b2be78881461eef23e0861f2de3c01000000b2004730440220363939e550920b4d9947659a70f1c40603230c3e6967d20f22da130976ba01c1022056560d6f480b1bd150faf238c6ab36b42ad03d502970cce0a3092052ba5efe88014c6751210378d430274f8c5ec1321338151e9f27f4c676a008bdf8638d07c0b6be9ab35c71410778d430274f8c5ec1321338151e9f27f4c676a008bdf8638d07c0b6be9ab35c71a1518063243acd4dfe96b66e3f2ec8013c8e072cd09b3834a19f81f659cc345552aeffffffff01905f01000000000017a914367e3c2c31cb061606e5a812257fc153f2bef80e8700000000";
      final String P2SH_OUTPUT_ADDRESS = "36f9dsGq4sw1xy44ZAuMQ1AAiAyHZxBagF";
      final String FUNDING = "0100000001990d1b71e3b572926982fa2419144aaccbc14ba2e918496052d26a4da3777132000000006a4730440220387b5e2d0005ff13f9a02df718fb85a67133484adc8e2571379aed1fccc0692b02204cc4555d33244e2abffcf362dcd4b71b5f373578c3a39da90bf120c2d3d80f9601210254df56fd5663610ce8370a8af0aa9e3e8bc1befd176554f35575769aec249d16ffffffff0280380100000000001976a914f679b8239ffdf39987d7c5ca8e5fd44475a795e188aca08601000000000017a9147e76c2468a8d453bfbc130e0ca86b6821b24c7b38700000000";
      BitcoinTransaction transaction = BitcoinTransaction.fromByteReader(new ByteReader(HexUtils.toBytes(P2SH_MULTISIG_TRANSACTION)));
      Assert.assertEquals(P2SH_OUTPUT_ADDRESS, transaction.outputs[0].script.getAddress(NetworkParameters.productionNetwork).toString());
      BitcoinTransaction.fromByteReader(new ByteReader(HexUtils.toBytes(FUNDING)));
      Assert.assertArrayEquals(transaction.toBytes(), HexUtils.toBytes(P2SH_MULTISIG_TRANSACTION));
   }

   @Test
   public void parseSegWitTransactionTest() throws BitcoinTransaction.TransactionParsingException {
      final String SEGWIT_TRANSACTION = "010000000001017df13a6a9b86581d5cc33700f94484952f91ef01f4d5a12a9d7f846c275279650100000017160014d2d533042981eed910a9687e1afd97643293a47cffffffff0220e9ef01000000001976a914f03e263abeff4eb5cc88f94848ed1a46a2b0eb5d88acda043d060000000017a914678e8a2ce2c8019e55a162c760a999bcf2c659da870247304402205699e9b918ed89195eea86afe20be0b73c906f1946885714c826e4ed4d8e31f302203b0df66b05c42abcbb45a3ba3a95fd6b34554ce302b73929cbb631f4c6a791120121023d8c8849e2addaeef2c690d6c7d60010d777e7a989226b277fc27b0c8b90f80100000000";
      final String SEGWIT_TRANSACTION_TRADITIONAL_ENCODING = "01000000017df13a6a9b86581d5cc33700f94484952f91ef01f4d5a12a9d7f846c275279650100000017160014d2d533042981eed910a9687e1afd97643293a47cffffffff0220e9ef01000000001976a914f03e263abeff4eb5cc88f94848ed1a46a2b0eb5d88acda043d060000000017a914678e8a2ce2c8019e55a162c760a999bcf2c659da8700000000";
      final String SEGWIT_OUTPUT_ADDRESS = "n3REvJdQsv2yn1P1omycGBMhtYDnZjqX1m";
      final String SEGWIT_P2SH_OUTPUT_ADDRESS = "2N2gnLss3NdVJmgruqzDRnEPHWtfLe1D8dF";
      BitcoinTransaction transaction = BitcoinTransaction.fromByteReader(new ByteReader(HexUtils.toBytes(SEGWIT_TRANSACTION)));
      Assert.assertEquals(SEGWIT_OUTPUT_ADDRESS, transaction.outputs[0].script.getAddress(NetworkParameters.testNetwork).toString());
      Assert.assertEquals(SEGWIT_P2SH_OUTPUT_ADDRESS, transaction.outputs[1].script.getAddress(NetworkParameters.testNetwork).toString());
      Assert.assertArrayEquals(transaction.toBytes(), HexUtils.toBytes(SEGWIT_TRANSACTION));
      Assert.assertArrayEquals(transaction.toBytes(false), HexUtils.toBytes(SEGWIT_TRANSACTION_TRADITIONAL_ENCODING));
   }
}
