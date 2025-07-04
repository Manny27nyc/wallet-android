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

package com.mycelium.wallet.activity.export;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.common.base.Optional;
import com.mrd.bitlib.crypto.Bip39;
import com.mrd.bitlib.crypto.InMemoryPrivateKey;
import com.mrd.bitlib.model.BitcoinAddress;
import com.mrd.bitlib.model.AddressType;
import com.mycelium.wallet.BuildConfig;
import com.mycelium.wallet.MbwManager;
import com.mycelium.wallet.R;
import com.mycelium.wallet.Utils;
import com.mycelium.wallet.activity.ScanActivity;
import com.mycelium.wallet.activity.StringHandlerActivity;
import com.mycelium.wallet.content.ResultType;
import com.mycelium.wallet.persistence.MetadataStorage;
import com.mycelium.wallet.content.HandleConfigFactory;
import com.mycelium.wapi.wallet.AesKeyCipher;
import com.mycelium.wapi.wallet.KeyCipher;
import com.mycelium.wapi.wallet.WalletAccount;
import com.mycelium.wapi.wallet.WalletManager;
import com.mycelium.wapi.wallet.bch.single.SingleAddressBCHAccount;
import com.mycelium.wapi.wallet.btc.single.SingleAddressAccount;
import com.mycelium.wapi.wallet.colu.ColuUtils;
import com.mycelium.wapi.wallet.colu.coins.ColuMain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.mycelium.wallet.activity.util.IntentExtentionsKt.getMasterSeed;
import static com.mycelium.wallet.activity.util.IntentExtentionsKt.getPrivateKey;
import static com.mycelium.wapi.wallet.colu.ColuModuleKt.getColuAccounts;


public class VerifyBackupActivity extends AppCompatActivity {
   private static final int SCAN_RESULT_CODE = 0;

   public static void callMe(Activity currentActivity) {
      Intent intent = new Intent(currentActivity, VerifyBackupActivity.class);
      currentActivity.startActivity(intent);
   }

   private MbwManager _mbwManager;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
      super.onCreate(savedInstanceState);
      setContentView(R.layout.verify_backup_activity);

      _mbwManager = MbwManager.getInstance(this.getApplication());

      findViewById(R.id.btScan).setOnClickListener(new android.view.View.OnClickListener() {

         @Override
         public void onClick(View v) {
            ScanActivity.callMe(VerifyBackupActivity.this, SCAN_RESULT_CODE, HandleConfigFactory.verifySeedOrKey());
         }

      });

      findViewById(R.id.btClipboard).setEnabled(hasPrivateKeyOnClipboard());
      findViewById(R.id.btClipboard).setOnClickListener(new android.view.View.OnClickListener() {

         @Override
         public void onClick(View v) {
            String privateKey = Utils.getClipboardString(VerifyBackupActivity.this);
            verifyClipboardPrivateKey(privateKey);
         }

      });
   }

   private boolean hasPrivateKeyOnClipboard() {
      String clipboardString = Utils.getClipboardString(this);
      InMemoryPrivateKey pk = InMemoryPrivateKey.fromBase58String(clipboardString, _mbwManager.getNetwork());
      return pk != null;
   }

   @Override
   protected void onResume() {
      updateUi();
      super.onResume();
   }

   @Override
   protected void onDestroy() {
      _mbwManager.clearCachedEncryptionParameters();
      super.onDestroy();
   }

   private void updateUi() {
      TextView tvNumKeys = findViewById(R.id.tvNumKeys);
      String infotext = "";
      if (_mbwManager.getMasterSeedManager().hasBip32MasterSeed()
            && _mbwManager.getMetadataStorage().getMasterSeedBackupState().equals(MetadataStorage.BackupState.UNKNOWN)) {
         infotext = getString(R.string.verify_backup_master_seed) + "\n";
      }

      int num = countKeysToVerify();
      if (num == 1) {
         infotext = infotext + getString(R.string.verify_backup_one_key);
      } else if (num > 0) {
         infotext = infotext + getString(R.string.verify_backup_num_keys, Integer.toString(num));
      }

      if (infotext.length() == 0) {
         tvNumKeys.setVisibility(View.GONE);
      } else {
         tvNumKeys.setVisibility(View.VISIBLE);
         tvNumKeys.setText(infotext);
      }
   }

   private int countKeysToVerify() {
      int num = 0;
      WalletManager walletManager = _mbwManager.getWalletManager(false);
      for (WalletAccount account : walletManager.getAccounts()) {
         MetadataStorage.BackupState backupState = _mbwManager.getMetadataStorage().getOtherAccountBackupState(account.getId());

         if (backupState!= MetadataStorage.BackupState.IGNORED) {
            boolean needsBackup = account instanceof SingleAddressAccount
                && !(account instanceof SingleAddressBCHAccount)
                && account.canSpend()
                && backupState != MetadataStorage.BackupState.VERIFIED;
            if (needsBackup) {
               num++;
            }
         }
      }
      for (WalletAccount account : getColuAccounts(walletManager)) {
         MetadataStorage.BackupState backupState = _mbwManager.getMetadataStorage().getOtherAccountBackupState(account.getId());

         if (backupState!= MetadataStorage.BackupState.IGNORED) {
            boolean needsBackup = account.canSpend() && backupState != MetadataStorage.BackupState.VERIFIED;
            if (needsBackup) {
               num++;
            }
         }
      }
      return num;
   }

   private void verifyClipboardPrivateKey(String keyString) {
      InMemoryPrivateKey pk = InMemoryPrivateKey.fromBase58String(keyString, _mbwManager.getNetwork());
      if (pk != null) {
         verify(pk);
         return;
      }

      showDialogMessage(R.string.unrecognized_private_key_format, false);
   }

   private void verify(InMemoryPrivateKey pk) {
      UUID account = null;
      boolean success = false;
      Collection<BitcoinAddress> allAddresses = null;
      WalletManager walletManager = _mbwManager.getWalletManager(false);
      for (BitcoinAddress currentAddress : pk.getPublicKey().getAllSupportedAddresses(_mbwManager.getNetwork()).values()) {
         // Figure out the account ID
         account = SingleAddressAccount.calculateId(currentAddress);
         // Check whether regular wallet contains that account
         success = walletManager.hasAccount(account);
         if (success) {
            allAddresses = pk.getPublicKey().getAllSupportedAddresses(_mbwManager.getNetwork()).values();
            break;
         }
      }

      for (ColuMain coluAsset : ColuUtils.allColuCoins(BuildConfig.FLAVOR)) {
         UUID coluUUID = ColuUtils.getGuidForAsset(coluAsset, pk.getPublicKey().toAddress(_mbwManager.getNetwork(), AddressType.P2PKH).getAllAddressBytes());
         success |= _mbwManager.getWalletManager(false).hasAccount(coluUUID);
      }

      if (success && allAddresses != null) {
         _mbwManager.getMetadataStorage().setOtherAccountBackupState(account, MetadataStorage.BackupState.VERIFIED);
         for (ColuMain coluAsset : ColuUtils.allColuCoins(BuildConfig.FLAVOR)) {
            UUID coluUUID = ColuUtils.getGuidForAsset(coluAsset, pk.getPublicKey().toAddress(_mbwManager.getNetwork(), AddressType.P2PKH).getAllAddressBytes());
            _mbwManager.getMetadataStorage().setOtherAccountBackupState(coluUUID, MetadataStorage.BackupState.VERIFIED);
         }
         updateUi();
         List<String> addressList = new ArrayList<>();
         for (BitcoinAddress address : allAddresses){
              addressList.add(address.toMultiLineString());
          }
         String label = _mbwManager.getMetadataStorage().getLabelByAccount(account);

         String addresses = TextUtils.join("\n\n", addressList);
         String message = getResources().getString(R.string.verify_backup_ok, label, addresses);
         showDialogMessage(message, false);
      } else {
         showDialogMessage(R.string.verify_backup_no_such_record, false);
      }
   }

    void verify(Bip39.MasterSeed masterSeed) {
        try {
            Bip39.MasterSeed ourSeed = _mbwManager.getMasterSeedManager().getMasterSeed(AesKeyCipher.defaultKeyCipher());
            if (masterSeed.equals(ourSeed)) {
                _mbwManager.getMetadataStorage().setMasterSeedBackupState(MetadataStorage.BackupState.VERIFIED);
                showDialogMessage(R.string.verify_backup_ok_message, false);
                updateUi();
            } else {
                showDialogMessage(R.string.wrong_seed, false);
            }
        } catch (KeyCipher.InvalidKeyCipher invalidKeyCipher) {
            throw new RuntimeException(invalidKeyCipher);
        }
    }

   private void showDialogMessage(int messageResource, final boolean quit) {
      showDialogMessage(getResources().getString(messageResource), quit);
   }

   private void showDialogMessage(String message, final boolean quit) {
      Utils.showSimpleMessageDialog(this, message);
   }

   @Override
   public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
      if (requestCode == SCAN_RESULT_CODE) {
         if (resultCode == RESULT_OK) {
             ResultType type = (ResultType) intent.getSerializableExtra(StringHandlerActivity.RESULT_TYPE_KEY);
             if (type == ResultType.PRIVATE_KEY) {
                 verify(getPrivateKey(intent));
             } else if (type == ResultType.MASTER_SEED) {
                 verify(getMasterSeed(intent));
             } else {
                 showDialogMessage("Not supported backup! Please contact support.", false);
             }
         } else {
            final String keyError = StringHandlerActivity.RESULT_ERROR;
            final String error = intent != null && intent.hasExtra(keyError) ? intent.getStringExtra(keyError) : null;
            if (error != null) {
               showDialogMessage(error, false);
            }
         }
      }
   }
}