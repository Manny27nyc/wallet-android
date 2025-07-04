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

package com.mycelium.wallet.activity.util;

import android.content.Context;
import android.util.AttributeSet;

import com.mrd.bitlib.model.NetworkParameters;
import com.mrd.bitlib.util.HexUtils;
import com.mycelium.net.ServerEndpointType;
import com.mycelium.wallet.MbwManager;
import com.mycelium.wallet.Utils;
import com.mycelium.wapi.wallet.TransactionSummary;
import com.mycelium.wapi.wallet.WalletAccount;
import com.mycelium.wapi.wallet.bch.bip44.Bip44BCHAccount;
import com.mycelium.wapi.wallet.bch.single.SingleAddressBCHAccount;

public class TransactionDetailsLabel extends BlockExplorerLabel {
   private TransactionSummary transaction;
   private boolean coluMode;

   public TransactionDetailsLabel(Context context) {
      super(context);
   }

   public TransactionDetailsLabel(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   public TransactionDetailsLabel(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
   }

   public void setColuMode(boolean mode) {
      coluMode = mode;
   }

   @Override
   protected String getLinkText() {
      return transaction.getIdHex();
   }

   @Override
   protected String getFormattedLinkText() {
      return Utils.stringChopper(HexUtils.toHex(transaction.getId()), 4, " ");
   }

   @Override
   protected String getLinkURL(BlockExplorer blockExplorer) {
      return blockExplorer.getUrl(transaction, MbwManager.getInstance(getContext()).getTorMode() == ServerEndpointType.Types.ONLY_TOR);
   }

   public void setTransaction(final TransactionSummary tx) {
      this.transaction = tx;
      update_ui();
      NetworkParameters networkParameters = MbwManager.getInstance(getContext()).getNetwork();
      WalletAccount account = MbwManager.getInstance(getContext()).getSelectedAccount();
      if (coluMode) {
         String baseUrl;
         if (networkParameters.isProdnet()) {
            baseUrl = "http://coloredcoins.org/explorer/";
         } else {
            baseUrl = "http://coloredcoins.org/explorer/testnet/";
         }
         setHandler(new BlockExplorer("CCO", "coloredcoins.org"
                 , baseUrl + "address/", baseUrl + "tx/"
                 , baseUrl + "address/", baseUrl + "tx/"));
      } else if (account instanceof SingleAddressBCHAccount || account instanceof Bip44BCHAccount) {
         if (networkParameters.isProdnet()) {
            setHandler(new BlockExplorer("BTL", "blockTrail",
                    "https://www.blocktrail.com/BCC/address/",
                    "https://www.blocktrail.com/BCC/tx/",
                    null, null));
         } else {
            setHandler(new BlockExplorer("BTL", "blockTrail",
                    "https://www.blocktrail.com/tBCC/address/",
                    "https://www.blocktrail.com/tBCC/tx/",
                    null, null));
         }
      } else {
         setHandler(MbwManager.getInstance(getContext())._blockExplorerManager.getBEMByCurrency(transaction.getType().getName()).getBlockExplorer());
      }
   }

}
