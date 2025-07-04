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
 * Copyright 2015 Megion Research and Development GmbH
 * Copyright 2015 Ledger
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

package com.mycelium.wallet.extsig.ledger.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.btchip.BTChipDongle.BTChipOutput;
import com.btchip.BTChipDongle.BTChipOutputKeycard;
import com.btchip.BTChipDongle.UserConfirmation;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.mrd.bitlib.UnsignedTransaction;
import com.mrd.bitlib.model.BitcoinAddress;
import com.mrd.bitlib.model.TransactionOutput;
import com.mycelium.wallet.LedgerPin2FADialog;
import com.mycelium.wallet.LedgerPinDialog;
import com.mycelium.wallet.MbwManager;
import com.mycelium.wallet.PinDialog;
import com.mycelium.wallet.R;
import com.mycelium.wallet.Utils;
import com.mycelium.wallet.activity.send.SignTransactionActivity;
import com.mycelium.wallet.activity.util.Pin;
import com.mycelium.wallet.activity.util.ValueExtensionsKt;
import com.mycelium.wallet.extsig.ledger.LedgerManager;
import com.mycelium.wapi.wallet.AccountScanManager;
import com.mycelium.wapi.wallet.AccountScanManager.Status;
import com.mycelium.wapi.wallet.btc.BtcTransaction;
import com.mycelium.wapi.wallet.btc.bip44.HDAccount;
import com.squareup.otto.Subscribe;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import nordpol.android.TagDispatcher;

public class LedgerSignTransactionActivity extends SignTransactionActivity {
   private final LedgerManager ledgerManager = MbwManager.getInstance(this).getLedgerManager();
   private boolean showTx;
   private TagDispatcher dispatcher;

   private static final int PAUSE_DELAY = 500;
   private AsyncTask<Integer, Void, Void> asyncCheckUnpluggedTask;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      dispatcher = TagDispatcher.get(this, ledgerManager);
   }

   @Override
   protected void setView() {
      setContentView(R.layout.sign_ledger_transaction_activity);
   }

   @Override
   protected void onResume() {
      super.onResume();
      // setup the handlers for the Ledger manager to this activity
      MbwManager.getEventBus().register(this);
      updateUi();
      dispatcher.enableExclusiveNfc();
   }

   @Override
   protected void onPause() {
      super.onPause();
      // unregister me as event handler for Ledger
      MbwManager.getEventBus().unregister(this);
      dispatcher.disableExclusiveNfc();
   }

   @Override
   protected void onStop() {
      if (asyncCheckUnpluggedTask != null){
         asyncCheckUnpluggedTask.cancel(true);
      }
      super.onStop();
   }

   @Override
   protected void onNewIntent(Intent intent) {
      dispatcher.interceptIntent(intent);
   }

   private void updateUi() {
      if ((ledgerManager.getCurrentState() != AccountScanManager.Status.unableToScan) &&
            (ledgerManager.getCurrentState() != AccountScanManager.Status.initializing)) {
         findViewById(R.id.ivConnectLedger).setVisibility(View.GONE);
      } else {
         findViewById(R.id.ivConnectLedger).setVisibility(View.VISIBLE);
         ((TextView) findViewById(R.id.tvPluginLedger)).setText(getString(R.string.ledger_please_plug_in));
         findViewById(R.id.tvPluginLedger).setVisibility(View.VISIBLE);
      }

      if (showTx) {
         findViewById(R.id.ivConnectLedger).setVisibility(View.GONE);
         findViewById(R.id.llShowTx).setVisibility(View.VISIBLE);

         ArrayList<String> toAddresses = new ArrayList<>(1);

         long totalSending = 0;
         UnsignedTransaction unsigned = ((BtcTransaction) _transaction).getUnsignedTx();

         for (TransactionOutput o : unsigned.getOutputs()) {
            BitcoinAddress toAddress;
            toAddress = o.script.getAddress(_mbwManager.getNetwork());
            Optional<Integer[]> addressId = ((HDAccount) _account).getAddressId(toAddress);

            if (!(addressId.isPresent() && addressId.get()[0] == 1)) {
               // this output goes to a foreign address (addressId[0]==1 means its internal change)
               totalSending += o.value;
               toAddresses.add(toAddress.toDoubleLineString());
            }
         }

         String toAddress = Joiner.on(",\n").join(toAddresses);
         String amount = ValueExtensionsKt.toString(Utils.getBtcCoinType().value(totalSending));
         String total = ValueExtensionsKt.toString(Utils.getBtcCoinType().value(totalSending + unsigned.calculateFee()));
         String fee = ValueExtensionsKt.toString(Utils.getBtcCoinType().value(unsigned.calculateFee()));

         ((TextView) findViewById(R.id.tvAmount)).setText(amount);
         ((TextView) findViewById(R.id.tvToAddress)).setText(toAddress);
         ((TextView) findViewById(R.id.tvFee)).setText(fee);
         ((TextView) findViewById(R.id.tvTotal)).setText(total);
      } else {
         findViewById(R.id.llShowTx).setVisibility(View.GONE);
      }
   }

   private void showPinPad(int title, final PinDialog.OnPinEntered callback) {
      LedgerPinDialog pin = new LedgerPinDialog(LedgerSignTransactionActivity.this, true);
      pin.setTitle(title);
      pin.setOnPinValid(callback);
      pin.show();
   }

   final Handler disconnectHandler = new Handler(new Handler.Callback() {
      @Override
      public boolean handleMessage(Message message) {
         ((TextView) findViewById(R.id.tvPluginLedger)).setText(getString(R.string.ledger_powercycle));
         findViewById(R.id.tvPluginLedger).setVisibility(View.VISIBLE);
         updateUi();
         return true;
      }
   });

   final Handler connectHandler = new Handler(new Handler.Callback() {
      @Override
      public boolean handleMessage(Message message) {
         ((TextView) findViewById(R.id.tvPluginLedger)).setText(getString(R.string.ledger_please_wait));
         updateUi();
         return true;
      }
   });

   private void onUserConfirmationRequest2FA(BTChipOutput outputParam) {
      BTChipOutputKeycard output = (BTChipOutputKeycard) outputParam;
      ArrayList<String> toAddresses = new ArrayList<>(1);
      UnsignedTransaction unsigned = ((BtcTransaction) _transaction).getUnsignedTx();
      for (TransactionOutput o : unsigned.getOutputs()) {
         BitcoinAddress toAddress;
         toAddress = o.script.getAddress(_mbwManager.getNetwork());
         Optional<Integer[]> addressId = ((HDAccount) _account).getAddressId(toAddress);

         if (!(addressId.isPresent() && addressId.get()[0] == 1)) {
            // this output goes to a foreign address (addressId[0]==1 means its internal change)
            toAddresses.add(toAddress.toString());
         }
      }

      // show the 2FA dialog
      LedgerPin2FADialog pin = new LedgerPin2FADialog(this, toAddresses.get(0), output.getKeycardIndexes());
      pin.setTitle(R.string.ledger_enter_2fa_pin);
      pin.setOnPinValid(new LedgerPin2FADialog.OnPinEntered() {
         @Override
         public void pinEntered(LedgerPin2FADialog dialog, Pin pin) {
            ledgerManager.enterTransaction2FaPin(convertPin2Fa(pin.getPin()));
            dialog.dismiss();
         }
      });
      pin.show();
   }

   @NonNull
   private String convertPin2Fa(String pin) {
      byte[] binaryPin = new byte[pin.length()];
      for (int i = 0; i < pin.length(); i++) {
         binaryPin[i] = (byte) Integer.parseInt(pin.substring(i, i + 1), 16);
      }
      pin = new String(binaryPin, StandardCharsets.ISO_8859_1);
      return pin;
   }

   private void waitForUnplugAndReplug() {
      if (asyncCheckUnpluggedTask != null) {
         asyncCheckUnpluggedTask.cancel(true);
      }

      asyncCheckUnpluggedTask = new AsyncTask<Integer, Void, Void>() {
         @Override
         protected Void doInBackground(Integer... params) {
            disconnectHandler.sendEmptyMessage(0);
            while (ledgerManager.isPluggedIn() && !isCancelled()) {
               try {
                  Thread.sleep(PAUSE_DELAY);
               } catch (InterruptedException ignore) {
               }
            }
            while (!ledgerManager.isPluggedIn() && !isCancelled()) {
               try {
                  Thread.sleep(PAUSE_DELAY);
               } catch (InterruptedException ignore) {
               }
            }
            connectHandler.sendEmptyMessage(0);
            return null;
         }

         @Override
         protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            showPinPad(R.string.ledger_enter_transaction_pin, new PinDialog.OnPinEntered() {
               @Override
               public void pinEntered(PinDialog dialog, Pin pin) {
                  ledgerManager.enterTransaction2FaPin(pin.getPin());
                  dialog.dismiss();
               }
            });
         }
      };

      // there is already the signing task running - run this on a different thread to get it executed now
      asyncCheckUnpluggedTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 1);
   }

   private void onUserConfirmationRequestKeyboard() {
      showTx = true;
      updateUi();
      waitForUnplugAndReplug();
   }

   @Subscribe
   public void onScanError(AccountScanManager.OnScanError event) {
      Utils.showSimpleMessageDialog(LedgerSignTransactionActivity.this, event.errorMessage, new Runnable() {
         @Override
         public void run() {
            LedgerSignTransactionActivity.this.setResult(RESULT_CANCELED);
            // close this activity and let the user try again
            LedgerSignTransactionActivity.this.finish();
         }
      });

      // kill the signing task
      LedgerSignTransactionActivity.this.cancelSigningTask();
   }

   @Subscribe
   public void onStatusChanged(AccountScanManager.OnStatusChanged event) {
      if (event.state.equals(Status.readyToScan)) {
         // todo: not needed
         connectHandler.sendEmptyMessage(0);
      }
      updateUi();
   }

   @Subscribe
   public void onPinRequest(LedgerManager.OnPinRequest event) {
      showPinPad(R.string.ledger_enter_pin, new PinDialog.OnPinEntered() {
         @Override
         public void pinEntered(PinDialog dialog, Pin pin) {
            ledgerManager.enterPin(pin.getPin());
            dialog.dismiss();
         }
      });
   }

   @Subscribe
   public void onShowTransactionVerification(LedgerManager.OnShowTransactionVerification event) {
      showTx = true;
      updateUi();
   }

   @Subscribe
   public void on2FaRequest(LedgerManager.On2FaRequest event) {
      if (event.output.getUserConfirmation().equals(UserConfirmation.KEYBOARD) ||
            event.output.getUserConfirmation().equals(UserConfirmation.KEYCARD_NFC)) {

         // Prefer the second factor confirmation to the keycard if initiated from another interface in a multi interface product
         onUserConfirmationRequestKeyboard();
      } else if (event.output.getUserConfirmation().equals(UserConfirmation.KEYCARD) ||
            event.output.getUserConfirmation().equals(UserConfirmation.KEYCARD_SCREEN) ||
            event.output.getUserConfirmation().equals(UserConfirmation.KEYCARD_DEPRECATED)) {

         onUserConfirmationRequest2FA(event.output);
      }
   }
}
