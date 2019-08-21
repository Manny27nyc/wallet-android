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

package com.mycelium.wallet.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.mrd.bitlib.crypto.BipSss;
import com.mrd.bitlib.crypto.HdKeyNode;
import com.mrd.bitlib.crypto.InMemoryPrivateKey;
import com.mrd.bitlib.model.Address;
import com.mrd.bitlib.model.AddressType;
import com.mrd.bitlib.model.NetworkParameters;
import com.mycelium.wallet.BuildConfig;
import com.mycelium.wallet.MbwManager;
import com.mycelium.wallet.R;
import com.mycelium.wallet.Utils;
import com.mycelium.wallet.activity.modern.Toaster;
import com.mycelium.wallet.activity.util.ImportCoCoHDAccount;
import com.mycelium.wallet.activity.util.ValueExtensionsKt;
import com.mycelium.wallet.content.HandleConfigFactory;
import com.mycelium.wallet.content.ResultType;
import com.mycelium.wallet.extsig.keepkey.activity.KeepKeyAccountImportActivity;
import com.mycelium.wallet.extsig.ledger.activity.LedgerAccountImportActivity;
import com.mycelium.wallet.extsig.trezor.activity.TrezorAccountImportActivity;
import com.mycelium.wallet.persistence.MetadataStorage;
import com.mycelium.wapi.wallet.AddressUtils;
import com.mycelium.wapi.wallet.AesKeyCipher;
import com.mycelium.wapi.wallet.GenericAddress;
import com.mycelium.wapi.wallet.WalletAccount;
import com.mycelium.wapi.wallet.WalletManager;
import com.mycelium.wapi.wallet.btc.BtcAddress;
import com.mycelium.wapi.wallet.btc.bip44.HDAccount;
import com.mycelium.wapi.wallet.btc.bip44.UnrelatedHDAccountConfig;
import com.mycelium.wapi.wallet.btc.single.AddressSingleConfig;
import com.mycelium.wapi.wallet.btc.single.PrivateSingleConfig;
import com.mycelium.wapi.wallet.btc.single.SingleAddressAccount;
import com.mycelium.wapi.wallet.coins.CryptoCurrency;
import com.mycelium.wapi.wallet.coins.Value;
import com.mycelium.wapi.wallet.colu.AddressColuConfig;
import com.mycelium.wapi.wallet.colu.ColuAccount;
import com.mycelium.wapi.wallet.colu.ColuModule;
import com.mycelium.wapi.wallet.colu.ColuUtils;
import com.mycelium.wapi.wallet.colu.PrivateColuConfig;
import com.mycelium.wapi.wallet.colu.coins.ColuMain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mycelium.wallet.activity.util.IntentExtentionsKt.getAddress;
import static com.mycelium.wallet.activity.util.IntentExtentionsKt.getAssetUri;
import static com.mycelium.wallet.activity.util.IntentExtentionsKt.getHdKeyNode;
import static com.mycelium.wallet.activity.util.IntentExtentionsKt.getPrivateKey;
import static com.mycelium.wallet.activity.util.IntentExtentionsKt.getShare;

public class AddAdvancedAccountActivity extends FragmentActivity implements ImportCoCoHDAccount.FinishListener {
   public static final String BUY_TREZOR_LINK = "https://buytrezor.com?a=mycelium.com";
   public static final String BUY_KEEPKEY_LINK = "https://keepkey.go2cloud.org/SH1M";
   public static final String BUY_LEDGER_LINK = "https://www.ledgerwallet.com/r/494d?path=/products";
   public static final int RESULT_MSG = 25;

   public static void callMe(Activity activity, int requestCode) {
      Intent intent = new Intent(activity, AddAdvancedAccountActivity.class);
      activity.startActivityForResult(intent, requestCode);
   }

   private static final int SCAN_RESULT_CODE = 0;
   private static final int CREATE_RESULT_CODE = 1;
   private static final int TREZOR_RESULT_CODE = 2;
   private static final int CLIPBOARD_RESULT_CODE = 3;
   private static final int LEDGER_RESULT_CODE = 4;
   private static final int KEEPKEY_RESULT_CODE = 5;
   private MbwManager _mbwManager;

   private NetworkParameters _network;

   @BindView(R.id.btGenerateNewBchSingleKey)
   View btGenerateNewBchSingleKey;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
      super.onCreate(savedInstanceState);
      setContentView(R.layout.add_advanced_account_activity);
      ButterKnife.bind(this);
      final Activity activity = AddAdvancedAccountActivity.this;
      _mbwManager = MbwManager.getInstance(this);
      _network = _mbwManager.getNetwork();

      findViewById(R.id.btScan).setOnClickListener(new View.OnClickListener() {

         @Override
         public void onClick(View v) {
            ScanActivity.callMe(activity, SCAN_RESULT_CODE, HandleConfigFactory.returnKeyOrAddressOrHdNode());
         }

      });

      findViewById(R.id.btGenerateNewSingleKey).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Intent intent = new Intent(activity, CreateKeyActivity.class);
            startActivityForResult(intent, CREATE_RESULT_CODE);
         }
      });

      findViewById(R.id.btTrezor).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            TrezorAccountImportActivity.callMe(activity, TREZOR_RESULT_CODE);
         }
      });

      findViewById(R.id.btBuyTrezor).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Utils.openWebsite(activity, BUY_TREZOR_LINK);
         }
      });

      findViewById(R.id.btKeepKey).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            KeepKeyAccountImportActivity.callMe(activity, KEEPKEY_RESULT_CODE);
         }
      });

      findViewById(R.id.btBuyKeepKey).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Utils.openWebsite(activity, BUY_KEEPKEY_LINK);
         }
      });

      findViewById(R.id.btLedger).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            LedgerAccountImportActivity.callMe(activity, LEDGER_RESULT_CODE);
         }
      });

      findViewById(R.id.btBuyLedger).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Utils.openWebsite(activity, BUY_LEDGER_LINK);
         }
      });
      btGenerateNewBchSingleKey.setVisibility(View.GONE);
   }

   @Override
   public void onResume() {
      super.onResume();

      StringHandlerActivity.ParseAbility canHandle = StringHandlerActivity.canHandle(
              HandleConfigFactory.returnKeyOrAddressOrHdNode(),
              Utils.getClipboardString(AddAdvancedAccountActivity.this),
              MbwManager.getInstance(this).getNetwork());

      boolean canImportFromClipboard = (canHandle != StringHandlerActivity.ParseAbility.NO);

      Button clip = findViewById(R.id.btClipboard);
      clip.setEnabled(canImportFromClipboard);
      if (canImportFromClipboard) {
         clip.setText(R.string.clipboard);
      } else {
         clip.setText(R.string.clipboard_not_available);
      }
      clip.setOnClickListener(new View.OnClickListener() {

         @Override
         public void onClick(View v) {
            Intent intent = StringHandlerActivity.getIntent(AddAdvancedAccountActivity.this,
                    HandleConfigFactory.returnKeyOrAddressOrHdNode(),
                    Utils.getClipboardString(AddAdvancedAccountActivity.this));

            startActivityForResult(intent, CLIPBOARD_RESULT_CODE);
         }
      });
   }

   /**
    * SA watch only accounts import method.
    */
   private void returnAccount(GenericAddress address) {
      //UUID acc = _mbwManager.getWalletManager(false).createSingleAddressAccount(address);
      new ImportReadOnlySingleAddressAccountAsyncTask(address, AccountType.Unknown).execute();
   }

   /**
    * BIP44 account import method.
    * @param hdKeyNode node of depth 3.
    */
   private void returnAccount(HdKeyNode hdKeyNode) {
      UUID acc = _mbwManager.getWalletManager(false)
              .createAccounts(new UnrelatedHDAccountConfig(Collections.singletonList(hdKeyNode))).get(0);
      // set BackupState as ignored - we currently have no option to backup xPrivs after all
      _mbwManager.getMetadataStorage().setOtherAccountBackupState(acc, MetadataStorage.BackupState.IGNORED);
      finishOk(acc, false);
   }

   /**
    *  This method is only intended to support BIP32 CoCo accounts.
    * @param hdKeyNode node of depth 0
    */
   private void returnBip32Account(final HdKeyNode hdKeyNode) {
      if (hdKeyNode.getDepth() != 0) {
         throw new IllegalArgumentException("Only nodes of depth 0 are supported");
      }
      if (isNetworkActive()) {
         createAskForScanDialog(hdKeyNode);
      } else {
         createAskForNetworkDialog(hdKeyNode);
      }
   }

   private void createAskForNetworkDialog(final HdKeyNode hdKeyNode) {
      new AlertDialog.Builder(this)
              .setTitle(R.string.coco_service_unavailable)
              .setMessage(R.string.connection_unavailable)
              .setCancelable(true)
              .setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int id) {
                    returnBip32Account(hdKeyNode);
                 }
              })
              .setNegativeButton(R.string.cancel, null)
              .create()
              .show();
   }

   private void createAskForScanDialog(final HdKeyNode hdKeyNode) {
      new AlertDialog.Builder(this)
              .setTitle(R.string.attention)
              .setMessage(R.string.coco_scan_warning)
              .setCancelable(true)
              .setPositiveButton(R.string.button_continue, new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int id) {
                    ImportCoCoHDAccount importCoCoHDAccount = new ImportCoCoHDAccount(AddAdvancedAccountActivity.this, hdKeyNode);
                    importCoCoHDAccount.setFinishListener(AddAdvancedAccountActivity.this);
                    importCoCoHDAccount.execute();
                 }
              })
              .setNegativeButton(R.string.cancel, null)
              .create()
              .show();
   }

   private boolean isNetworkActive() {
      ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
      return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
   }

   @Override
   public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
      if (requestCode == SCAN_RESULT_CODE || requestCode == CLIPBOARD_RESULT_CODE) {
         if (resultCode == Activity.RESULT_OK) {
            boolean fromClipboard = (requestCode == CLIPBOARD_RESULT_CODE);

            ResultType type = (ResultType) intent.getSerializableExtra(StringHandlerActivity.RESULT_TYPE_KEY);
            switch (type) {
               case PRIVATE_KEY:
                  InMemoryPrivateKey key = getPrivateKey(intent);
                  if (fromClipboard) {
                     Utils.clearClipboardString(AddAdvancedAccountActivity.this);
                  }

                  // We imported this key from somewhere else - so we guess, that there exists an backup
                  returnAccount(key, MetadataStorage.BackupState.IGNORED, AccountType.Unknown);
                  break;
               case ADDRESS:
                  returnAccount(getAddress(intent));
                  break;
               case HD_NODE:
                  final HdKeyNode hdKeyNode = getHdKeyNode(intent);
                  if (fromClipboard && hdKeyNode.isPrivateHdKeyNode()) {
                     Utils.clearClipboardString(AddAdvancedAccountActivity.this);
                  }
                  processNode(hdKeyNode);
                  break;
               case ASSET_URI:
                  // uri result must be with address, can check request HandleConfigFactory.returnKeyOrAddressOrHdNode
                  returnAccount(getAssetUri(intent).getAddress());
                  break;
               case SHARE:
                  BipSss.Share share = getShare(intent);
                  BipSsImportActivity.callMe(this, share, StringHandlerActivity.IMPORT_SSS_CONTENT_CODE);
                  break;
               default:
                  throw new IllegalStateException("Unexpected result type from scan: " + type.toString());
            }
         } else {
            ScanActivity.toastScanError(resultCode, intent, this);
         }
      } else if (requestCode == CREATE_RESULT_CODE && resultCode == Activity.RESULT_OK) {
         String base58Key = intent.getStringExtra("base58key");
         Optional<InMemoryPrivateKey> key = InMemoryPrivateKey.fromBase58String(base58Key, _network);
         if (key.isPresent()) {
            // This is a new key - there is no existing backup
            returnAccount(key.get(), MetadataStorage.BackupState.UNKNOWN, AccountType.SA);
         } else {
            throw new RuntimeException("Creating private key from string unexpectedly failed.");
         }
      } else if (requestCode == TREZOR_RESULT_CODE && resultCode == Activity.RESULT_OK) {
         // already added to the WalletManager - just return the new account
         finishOk((UUID) intent.getSerializableExtra("account"), false);
      } else if (requestCode == KEEPKEY_RESULT_CODE && resultCode == Activity.RESULT_OK) {
         // already added to the WalletManager - just return the new account
         finishOk((UUID) intent.getSerializableExtra("account"), false);
      } else if (requestCode == LEDGER_RESULT_CODE && resultCode == Activity.RESULT_OK) {
         // already added to the WalletManager - just return the new account
         finishOk((UUID) intent.getSerializableExtra("account"), false);
      } else {
         super.onActivityResult(requestCode, resultCode, intent);
      }
   }

   private void processNode(final HdKeyNode hdKeyNode) {
      int depth = hdKeyNode.getDepth();
      switch (depth) {
         case 3:
            if (_mbwManager.getWalletManager(false).hasAccount(hdKeyNode.getUuid())){
               final WalletAccount existingAccount = _mbwManager.getWalletManager(false).getAccount(hdKeyNode.getUuid());
               if (hdKeyNode.isPrivateHdKeyNode() && !existingAccount.canSpend()) {
                  new AlertDialog.Builder(AddAdvancedAccountActivity.this)
                          .setTitle(R.string.priv_key_of_watch_only_account)
                          .setMessage(getString(R.string.want_to_add_priv_key_to_watch_account, _mbwManager.getMetadataStorage().getLabelByAccount(hdKeyNode.getUuid())))
                          .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialogInterface, int i) {
                                finishAlreadyExist(existingAccount.getReceiveAddress());
                             }
                          })
                          .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialogInterface, int i) {
                                returnAccount(hdKeyNode, true);
                             }
                          })
                          .create()
                          .show();
               }
            } else {
               returnAccount(hdKeyNode);
            }
            break;
         case 0:
            // This branch is created to support import CoCo from bip32 account
            if (hdKeyNode.isPrivateHdKeyNode()) {
               returnBip32Account(hdKeyNode);
            } else {
               new Toaster(this).toast(getString(R.string.import_xpub_should_xpriv), false);
            }
            break;
         default:
            String errorMessage = getString(R.string.import_xpub_wrong_depth, Integer.toString(depth));
            new Toaster(this).toast(errorMessage, false);
      }
   }

   // restore single account in asynctask so we can handle Colored Coins case
   private class ImportSingleAddressAccountAsyncTask extends AsyncTask<Void, Integer, AddressCheckResult> {
      private InMemoryPrivateKey key;
      private MetadataStorage.BackupState backupState;
      private ProgressDialog dialog;
      private boolean askUserForColorize = false;
      private List<WalletAccount<?>> existingAccounts = new ArrayList<>();
      private int selectedItem;

      ImportSingleAddressAccountAsyncTask(InMemoryPrivateKey key, MetadataStorage.BackupState backupState) {
         this.key = key;
         this.backupState = backupState;
      }

      @Override
      protected void onPreExecute() {
         super.onPreExecute();
         dialog = new ProgressDialog(AddAdvancedAccountActivity.this);
         dialog.setMessage("Importing");
         dialog.show();
      }

      @Override
      protected AddressCheckResult doInBackground(Void... params) {
         WalletManager walletManager = _mbwManager.getWalletManager(false);
         //Check whether this address is already used in any account
         for (Address addr : key.getPublicKey().getAllSupportedAddresses(_mbwManager.getNetwork()).values()) {
            GenericAddress checkedAddress = AddressUtils.fromAddress(addr);
            List<WalletAccount<?>> accounts = walletManager.getAccountsBy(checkedAddress);
            if (accounts.size() > 0) {
               existingAccounts = accounts;
               break;
            }
         }

         Address coluAddress = key.getPublicKey().toAddress(_mbwManager.getNetwork(), AddressType.P2PKH);
         ColuModule coluModule = (ColuModule)_mbwManager.getWalletManager(false).getModuleById(ColuModule.ID);
         askUserForColorize = coluModule.hasColuAssets(coluAddress);

         return askUserForColorize ? AddressCheckResult.HasColuAssets : AddressCheckResult.NoColuAssets;
      }

      @Override
      protected void onPostExecute(AddressCheckResult result) {
         dialog.dismiss();

         if (existingAccounts.size() == 0) {
            switch (result) {
               case HasColuAssets:
                  final ColuCoinAdapter adapter = new ColuCoinAdapter(AddAdvancedAccountActivity.this);
                  adapter.add(Utils.getBtcCoinType());
                  adapter.addAll(ColuUtils.allColuCoins(BuildConfig.FLAVOR));
                  new AlertDialog.Builder(AddAdvancedAccountActivity.this)
                          .setTitle(R.string.restore_address_as)
                          .setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialogInterface, int i) {
                                selectedItem = i;
                             }
                          })
                          .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialogInterface, int i) {
                                UUID account;
                                if (selectedItem == 0) {
                                   account = returnSAAccount(key, backupState);
                                } else {
                                   ColuMain coinType = (ColuMain) adapter.getItem(selectedItem);
                                   List<UUID> accounts = _mbwManager.getWalletManager(false)
                                           .createAccounts(new PrivateColuConfig(key, coinType, AesKeyCipher.defaultKeyCipher()));
                                   account = accounts.get(0);
                                }
                                finishOk(account, false);
                             }
                          })
                          .create()
                          .show();
                  break;
               case NoColuAssets:
                  finishOk(returnSAAccount(key, backupState), false);
            }
         } else {
            WalletAccount accountToUpgrade = null;
            switch (result) {
               case HasColuAssets:
                  WalletAccount coluAccount = null;
                  for(WalletAccount exAccount : existingAccounts) {
                     if (exAccount instanceof ColuAccount) {
                        coluAccount = exAccount;
                        break;
                     }
                  }

                  // If we have a colu account
                  if (coluAccount != null) {
                     if (!coluAccount.canSpend()) {
                        accountToUpgrade = coluAccount;
                     } else {
                        finishAlreadyExist(coluAccount.getReceiveAddress());
                     }
                  } else {
                     // We do not have a colu account, should create it
                     final ColuCoinAdapter adapter = new ColuCoinAdapter(AddAdvancedAccountActivity.this);
                     adapter.add(Utils.getBtcCoinType());
                     adapter.addAll(ColuUtils.allColuCoins(BuildConfig.FLAVOR));
                     new AlertDialog.Builder(AddAdvancedAccountActivity.this)
                             .setTitle(R.string.restore_address_as)
                             .setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                   selectedItem = i;
                                }
                             })
                             .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                   UUID account;
                                   if (selectedItem == 0) {
                                      doUpgrade(existingAccounts.get(0), key);
                                      return;
                                   } else {
                                      ColuMain coinType = (ColuMain) adapter.getItem(selectedItem);
                                      List<UUID> accounts = _mbwManager.getWalletManager(false)
                                              .createAccounts(new PrivateColuConfig(key, coinType, AesKeyCipher.defaultKeyCipher()));
                                      account = accounts.get(0);
                                   }
                                   finishOk(account, false);
                                }
                             })
                             .create()
                             .show();

                  }
                  break;
               case NoColuAssets:
                  // Since there are no COLU assets available, only BTC SA could exist
                  WalletAccount existingAccount = existingAccounts.get(0);

                  if (!existingAccount.canSpend()) {
                     accountToUpgrade = existingAccount;
                  } else {
                     finishAlreadyExist(existingAccount.getReceiveAddress());
                  }
                  break;
            }

            if (accountToUpgrade != null) {
               doUpgrade(accountToUpgrade, key);
            }
         }
      }
   }

   void doUpgrade(WalletAccount accountToUpgrade, InMemoryPrivateKey privateKey) {
      final InMemoryPrivateKey key = privateKey;
      final WalletAccount accToUpgrade = accountToUpgrade;
      // scanned the private key of a watch only single address account
      final String existingAccountName =
              _mbwManager.getMetadataStorage().getLabelByAccount(accountToUpgrade.getId());
      new AlertDialog.Builder(AddAdvancedAccountActivity.this)
              .setTitle(R.string.priv_key_of_watch_only_account)
              .setMessage(getString(R.string.want_to_add_priv_key_to_watch_account, existingAccountName))
              .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialogInterface, int i) {
                    finishAlreadyExist(accToUpgrade.getReceiveAddress());
                 }
              })
              .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialogInterface, int i) {
                    UUID accountId = accToUpgrade.getId();
                    WalletManager walletManager = _mbwManager.getWalletManager(false);
                    walletManager.deleteAccount(accToUpgrade.getId());
                    if (accToUpgrade instanceof SingleAddressAccount) {
                       accountId = walletManager.createAccounts(new PrivateSingleConfig(key,
                               AesKeyCipher.defaultKeyCipher(), existingAccountName)).get(0);
                    } else {
                       walletManager.createAccounts(new PrivateColuConfig(key, (ColuMain) accToUpgrade.getCoinType(), AesKeyCipher.defaultKeyCipher()));
                    }
                    finishOk(accountId, true);
                 }
              })
              .create()
              .show();
   }

   private class ColuCoinAdapter extends ArrayAdapter<CryptoCurrency> {
      ColuCoinAdapter(@NonNull Context context) {
         super(context, android.R.layout.simple_list_item_single_choice);
      }

      @NonNull
      @Override
      public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
         View view = super.getView(position, convertView, parent);
         ((TextView) view.findViewById(android.R.id.text1)).setText(getItem(position).getName());
         return view;
      }
   }

   private UUID returnSAAccount(InMemoryPrivateKey key, MetadataStorage.BackupState backupState) {
      UUID acc = _mbwManager.getWalletManager(false)
              .createAccounts(new PrivateSingleConfig(key, AesKeyCipher.defaultKeyCipher())).get(0);
      _mbwManager.getMetadataStorage().setOtherAccountBackupState(acc, backupState);
      return acc;
   }

   /**
    * SA spend account import method.
    */
   private void returnAccount(InMemoryPrivateKey key, MetadataStorage.BackupState backupState, AccountType type) {
      if (type == AccountType.SA) {
         finishOk(returnSAAccount(key, backupState), false);
      } else {
         new ImportSingleAddressAccountAsyncTask(key, backupState).execute();
      }
   }

   private void returnAccount(HdKeyNode hdKeyNode, boolean isUpgrade) {
      UUID acc = _mbwManager.getWalletManager(false)
              .createAccounts(new UnrelatedHDAccountConfig(Collections.singletonList(hdKeyNode))).get(0);
      // set BackupState as ignored - we currently have no option to backup xPrivs after all
      _mbwManager.getMetadataStorage().setOtherAccountBackupState(acc, MetadataStorage.BackupState.IGNORED);
      finishOk(acc, isUpgrade);
   }

   enum AddressCheckResult {
      AccountExists, HasColuAssets, NoColuAssets
   }

   private class ImportReadOnlySingleAddressAccountAsyncTask extends AsyncTask<Void, Integer, AddressCheckResult> {
      private GenericAddress address;
      private AccountType addressType;
      private ProgressDialog dialog;
      private boolean askUserForColorize = false;
      private int selectedItem;

      ImportReadOnlySingleAddressAccountAsyncTask(GenericAddress address, AccountType addressType) {
         this.address = address;
         this.addressType = addressType;
      }

      @Override
      protected void onPreExecute() {
         super.onPreExecute();
         dialog = new ProgressDialog(AddAdvancedAccountActivity.this);
         dialog.setMessage("Importing");
         dialog.show();
      }

      @Override
      protected AddressCheckResult doInBackground(Void... params) {
         //Check whether this address is already used in any account
         Optional<UUID> accountId = _mbwManager.getAccountId(address);
         if (accountId.isPresent()) {
            return AddressCheckResult.AccountExists;
         }

         BtcAddress btcAddress = (BtcAddress) address;
         ColuModule coluModule = (ColuModule)_mbwManager.getWalletManager(false).getModuleById(ColuModule.ID);
         askUserForColorize = coluModule.hasColuAssets(btcAddress.getAddress());

         return askUserForColorize ? AddressCheckResult.HasColuAssets : AddressCheckResult.NoColuAssets;
      }

      @Override
      protected void onPostExecute(AddressCheckResult result) {
         dialog.dismiss();

         switch(result) {
            case AccountExists:
               finishAlreadyExist(address);
               break;
            case NoColuAssets:
               UUID account = _mbwManager.getWalletManager(false)
                       .createAccounts(new AddressSingleConfig((BtcAddress) address)).get(0);
               finishOk(account, false);
               break;
            case HasColuAssets: {
               final ColuCoinAdapter adapter = new ColuCoinAdapter(AddAdvancedAccountActivity.this);
               adapter.add(Utils.getBtcCoinType());
               adapter.addAll(ColuUtils.allColuCoins(BuildConfig.FLAVOR));
               new AlertDialog.Builder(AddAdvancedAccountActivity.this)
                       .setTitle(R.string.restore_address_as)
                       .setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialogInterface, int i) {
                             selectedItem = i;
                          }
                       })
                       .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialogInterface, int i) {
                             UUID account;
                             if (selectedItem == 0) {
                                account = _mbwManager.getWalletManager(false)
                                        .createAccounts(new AddressSingleConfig((BtcAddress) address)).get(0);
                             } else {
                                ColuMain coinType = (ColuMain) adapter.getItem(selectedItem);
                                account = _mbwManager.getWalletManager(false)
                                        .createAccounts(new AddressColuConfig((BtcAddress) address, coinType)).get(0);
                             }
                             finishOk(account, false);
                          }
                       })
                       .create()
                       .show();
               }
            }
         }
   }

   @Override
   public void finishCoCoFound(final UUID firstAddedAccount, int accountsCreated, int existingAccountsFound,
                               Value mtFound, Value massFound, Value rmcFound) {
      List<String> amountStrings = new ArrayList<>();
      for (Value found : new Value[]{rmcFound, mtFound, massFound}) {
         if (found.isPositive()) {
            amountStrings.add(ValueExtensionsKt.toStringWithUnit(found));
         }
      }
      String fundsFound = TextUtils.join(", ", amountStrings);
      String message = null;
      String accountsCreatedString = getResources().getQuantityString(R.plurals.new_accounts_created, accountsCreated,
              accountsCreated);
      String existingFoundString = getResources().getQuantityString(R.plurals.existing_accounts_found,
              existingAccountsFound, existingAccountsFound);
      if (accountsCreated > 0 && existingAccountsFound == 0) {
         message = getString(R.string.d_coco_created, fundsFound, accountsCreatedString);
      } else if (accountsCreated > 0 && existingAccountsFound > 0) {
         message = getString(R.string.d_coco_created_existing_found, fundsFound, accountsCreatedString, existingFoundString);
      } else if (existingAccountsFound > 0) {
         message = getString(R.string.d_coco_existing_found, fundsFound, existingFoundString);
      }
      new AlertDialog.Builder(this)
              .setTitle(R.string.coco_found)
              .setMessage(message)
              .setPositiveButton(R.string.button_continue, new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialogInterface, int i) {
                    finishOk(firstAddedAccount, false);
                 }
              })
              .create()
              .show();
   }

   @Override
   public void finishCoCoNotFound(final HdKeyNode hdKeyNode) {
      new AlertDialog.Builder(this)
              .setTitle(R.string.coco_not_found)
              .setMessage(R.string.no_digital_asset)
              .setPositiveButton(R.string.close, null)
              .setNegativeButton(R.string.rescan, new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int id) {
                    ImportCoCoHDAccount importCoCoHDAccount = new ImportCoCoHDAccount(AddAdvancedAccountActivity.this, hdKeyNode);
                    importCoCoHDAccount.setFinishListener(AddAdvancedAccountActivity.this);
                    importCoCoHDAccount.execute();
                 }
              })
              .create()
              .show();
   }

   private void finishAlreadyExist(GenericAddress address) {
      String accountType = getAccountType(address);
      Intent result = new Intent()
              .putExtra(AddAccountActivity.RESULT_MSG, getString(R.string.account_already_exist, accountType));
      setResult(RESULT_MSG, result);
      finish();
   }

   private String getAccountType(GenericAddress address) {
      UUID accountId = _mbwManager.getAccountId(address).get();
      WalletAccount account = _mbwManager.getWalletManager(false).getAccount(accountId);
      if (account instanceof HDAccount) {
         return "BTC HD account";
      }
      if (account instanceof SingleAddressAccount) {
         return "BTC Single Address";
      }
      return account.getCoinType().getName();
   }

   private void finishOk(UUID account, boolean isUpgrade) {
      Intent result = new Intent()
              .putExtra(AddAccountActivity.RESULT_KEY, account)
              .putExtra(AddAccountActivity.IS_UPGRADE, isUpgrade);
      setResult(RESULT_OK, result);
      finish();
   }

   enum AccountType {
      SA, Colu, Unknown
   }
}
