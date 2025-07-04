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

package com.mycelium.wallet.activity.modern;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.mrd.bitlib.model.BitcoinAddress;
import com.mycelium.wallet.MbwManager;
import com.mycelium.wallet.R;
import com.mycelium.wallet.Utils;
import com.mycelium.wallet.activity.MessageSigningActivity;
import com.mycelium.wallet.activity.util.AddressLabel;
import com.mycelium.wapi.wallet.AddressUtils;
import com.mycelium.wapi.wallet.btc.BtcAddress;
import com.mycelium.wapi.wallet.btc.bip44.AddressesListProvider;

import java.util.List;
import java.util.UUID;

public class HDSigningActivity extends AppCompatActivity {
    private static final LinearLayout.LayoutParams WCWC = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);

    private SigningClickListener _signingClickListener;
    private MbwManager _mbwManager;
    private UUID _accountid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hd_signing_activity);

        _signingClickListener = new SigningClickListener();
        _mbwManager = MbwManager.getInstance(this.getApplication());
        _accountid = (UUID) getIntent().getSerializableExtra("account");

        updateUi();
    }

    private void updateUi() {
        LinearLayout addressView = findViewById(R.id.listPrivateKeyAddresses);
        AddressesListProvider addressesListProvider = (AddressesListProvider)
                _mbwManager.getWalletManager(false).getAccount(_accountid);

        //sort addresses by alphabet for easier selection
        List<BitcoinAddress> addresses = Utils.sortAddresses(addressesListProvider.addressesList());

        for (BitcoinAddress address : addresses) {
            addressView.addView(getItemView(address));
        }
    }

    private View getItemView(BitcoinAddress address) {
        // Create vertical linear layout for address
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setLayoutParams(WCWC);
        ll.setPadding(10, 10, 10, 10);

        // Add address chunks
        AddressLabel addressLabel = new AddressLabel(this);
        addressLabel.setAddress(AddressUtils.fromAddress(address));
        ll.addView(addressLabel);
        //Make address clickable
        addressLabel.setOnClickListener(_signingClickListener);
        return ll;
    }

    private class SigningClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            AddressLabel addressLabel = (AddressLabel) v;
            if (addressLabel.getAddress() == null) {
                return;
            }
            BtcAddress btcAddress = (BtcAddress) addressLabel.getAddress();
            MessageSigningActivity.callMe(HDSigningActivity.this, btcAddress, _accountid);
        }
    }
}
