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
 * Copyright 2013, 2014 Megion Research and Development GmbH
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

package com.mycelium.wallet;

import com.mrd.bitlib.model.NetworkParameters;
import com.mycelium.net.HttpEndpoint;
import com.mycelium.net.HttpsEndpoint;
import com.mycelium.net.ServerEndpoints;
import com.mycelium.wallet.activity.util.BlockExplorer;
import com.mycelium.wapi.wallet.btcvault.BTCVNetworkParameters;
import com.mycelium.wallet.external.BuySellServiceDescriptor;
import com.mycelium.wallet.external.LocalTraderServiceDescription;
import com.mycelium.wallet.external.BankCardServiceDescription;
import com.mycelium.wapi.wallet.btc.coins.BitcoinTest;
import com.mycelium.wapi.wallet.eth.coins.EthTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MbwRegTestEnvironment extends MbwEnvironment {
   private static final String myceliumThumbprint = "9c:8e:d7:ad:6c:28:db:d4:72:6a:71:93:d6:4d:cb:e7:c7:a0:2e:bc";

   @Override
   public NetworkParameters getNetwork() {
      return NetworkParameters.testNetwork;
   }

   @Override
   public BTCVNetworkParameters getBTCVNetwork() {
      return BTCVNetworkParameters.getRegtestNetwork();
   }

   /**
    * Local Trader API for testnet
    */
   private static final ServerEndpoints testnetLtEndpoints = new ServerEndpoints(new HttpEndpoint[]{
         new HttpsEndpoint("https://localhost:4433/lttestnet", myceliumThumbprint),
   });

   @Override
   public ServerEndpoints getLtEndpoints() {
      return  testnetLtEndpoints;
   }


   /**
    * Wapi
    */
   // run `adb reverse tcp:4433 tcp:4433` on your machine running vagrant
   private static final ServerEndpoints testnetWapiEndpoints = new ServerEndpoints(new HttpEndpoint[]{
         new HttpsEndpoint("https://localhost:4433/wapitestnet", myceliumThumbprint),
   });


   @Override
   public ServerEndpoints getWapiEndpoints() {
      return  testnetWapiEndpoints;
   }

   /**
    * Available BlockExplorers
    *
    * The first is the default block explorer if the requested one is not available
    */
   private static final Map<String, List<BlockExplorer>> testnetExplorerClearEndpoints = new HashMap<String, List<BlockExplorer>>() {
      {
         put(BitcoinTest.get().getName(), new ArrayList<BlockExplorer>() {{
            add(new BlockExplorer("SBT", "blockCypher", "https://live.blockcypher.com/btc-testnet/address/", "https://live.blockcypher.com/btc-testnet/tx/", null, null));
            add(new BlockExplorer("BTL", "blockTrail", "https://www.blocktrail.com/tBTC/address/", "https://www.blocktrail.com/tBTC/tx/", null, null));
            add(new BlockExplorer("BPY", "BitPay", "https://test-insight.bitpay.com/address/", "https://test-insight.bitpay.com/tx/", null, null));
            add(new BlockExplorer("BEX", "blockExplorer", "http://blockexplorer.com/testnet/address/", "https://blockexplorer.com/testnet/tx/", null, null));
         }});
         put(EthTest.INSTANCE.getName(), new ArrayList<BlockExplorer>() {{
            add(new BlockExplorer("ETS", "etherscan.io", "https://goerli.etherscan.io/address/", "https://goerli.etherscan.io/tx/", null, null));
         }});
      }
   };

   public Map<String, List<BlockExplorer>> getBlockExplorerMap() {
      return new HashMap<>(testnetExplorerClearEndpoints);
   }

   public List<BuySellServiceDescriptor> getBuySellServices(){
      return new ArrayList<BuySellServiceDescriptor>() {{
         add(new LocalTraderServiceDescription());
         add(new BankCardServiceDescription());
      }};
   }
}
