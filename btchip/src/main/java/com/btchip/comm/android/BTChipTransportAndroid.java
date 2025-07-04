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
 *******************************************************************************
*   Ledger Bitcoin Hardware Wallet Java API
*   (c) 2014-2015 Ledger - 1BTChip7VfTnrPra5jqci7ejnMguuHogTn
 *
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
 *
*      http://www.apache.org/licenses/LICENSE-2.0
 *
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*   limitations under the License.
 ********************************************************************************
 */

package com.btchip.comm.android;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.*;
import android.nfc.Tag;
import android.util.Log;
import androidx.core.content.ContextCompat;
import com.btchip.comm.BTChipTransport;
import com.btchip.comm.BTChipTransportFactory;
import com.btchip.comm.BTChipTransportFactoryCallback;

import kotlin.Unit;
import nordpol.android.AndroidCard;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class BTChipTransportAndroid implements BTChipTransportFactory {

   private UsbManager usbManager;
   private BTChipTransport transport;
   private final LinkedBlockingQueue<Boolean> gotRights = new LinkedBlockingQueue<Boolean>(1);
   private Tag detectedTag;
   private byte[] aid;

   private static final String LOG_TAG = "BTChipTransportAndroid";

   private static final byte TEST_APDU[] = {(byte) 0xe0, (byte) 0xc4, (byte) 0x00, (byte) 0x00, (byte) 0x00};

   /**
    * Receives broadcast when a supported USB device is attached, detached or
    * when a permission to communicate to the device has been granted.
    */
   private final BroadcastReceiver mUsbReceiver = new UsbReceiver(permission -> {
      gotRights.clear();
      // sync with connect
      gotRights.add(permission);
      return Unit.INSTANCE;
   });

   public BTChipTransportAndroid(Context context) {
      usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
   }

   @Override
   public boolean isPluggedIn() {
      if (transport != null) {
         Log.d(LOG_TAG, "Check if transport is still valid");
         try {
            transport.exchange(TEST_APDU);
         } catch (Exception e) {
            Log.d(LOG_TAG, "Error reported by transport");
            try {
               transport.close();
            } catch (Exception e1) {
            }
            transport = null;
            detectedTag = null;
         }
      }
      return ((getDevice(usbManager) != null) || (detectedTag != null));
   }

   @Override
   public BTChipTransport getTransport() {
      return transport;
   }

   @Override
   public boolean connect(final Context context, final BTChipTransportFactoryCallback callback) {
      if (transport != null) {
         try {
            Log.d(LOG_TAG, "Closing previous transport");
            transport.close();
            Log.d(LOG_TAG, "Previous transport closed");
         } catch (Exception e) {
         }
      }
      if (detectedTag != null) {
         try {
            Log.d(LOG_TAG, "Connect to NFC tag");
            AndroidCard card = AndroidCard.get(detectedTag);
            card.connect();
            if (aid != null) {
               byte[] apdu = new byte[aid.length + 5];
               apdu[0] = (byte) 0x00;
               apdu[1] = (byte) 0xA4;
               apdu[2] = (byte) 0x04;
               apdu[3] = (byte) 0x00;
               apdu[4] = (byte) aid.length;
               System.arraycopy(aid, 0, apdu, 5, aid.length);
               byte[] response = card.transceive(apdu);
               if ((response[response.length - 2] != (byte) 0x90) || (response[response.length - 1] != (byte) 0x00)) {
                  throw new RuntimeException("Select failed");
               }
            }
            transport = new BTChipTransportAndroidNFC(card);
            callback.onConnected(true);
            return true;
         } catch (Exception e) {
            Log.d(LOG_TAG, "NFC tag select failed", e);
            detectedTag = null;
            callback.onConnected(false);
            return false;
         }
      }
      String usbPermission = UsbReceiver.actionUsbPermission(context);
      ContextCompat.registerReceiver(context, mUsbReceiver,
              new IntentFilter(usbPermission), ContextCompat.RECEIVER_EXPORTED);


      final UsbDevice device = getDevice(usbManager);

      gotRights.clear();

      usbManager.requestPermission(device, PendingIntent.getBroadcast(context, 0,
              new Intent(usbPermission).setPackage(context.getPackageName()),
              PendingIntent.FLAG_MUTABLE));
      // retry because of InterruptedException
      while (true) {
         try {
            // gotRights.take blocks until the UsbManager gives us the rights via callback to the BroadcastReceiver
            // this might need an user interaction
            if (gotRights.take()) {
               transport = open(usbManager, device);
               callback.onConnected(true);
               return true;
            } else {
               callback.onConnected(false);
               return true;
            }
         } catch (Exception ignored) {
         }
      }
   }

   public static UsbDevice getDevice(UsbManager manager) {
      HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
      for (UsbDevice device : deviceList.values()) {
         if ((device.getVendorId() == VID) &&
               ((device.getProductId() == PID_WINUSB) || (device.getProductId() == PID_HID) ||
                     (device.getProductId() == PID_HID_LEDGER) || (device.getProductId() == PID_HID_LEDGER_PROTON))) {
            return device;
         }
         if (device.getVendorId() == VID_LEDGER) {
            return device;
         }
      }
      return null;
   }

   public static BTChipTransport open(UsbManager manager, UsbDevice device) throws ConnectionFailException {
      // Must only be called once permission is granted (see http://developer.android.com/reference/android/hardware/usb/UsbManager.html)
      // Important if enumerating, rather than being awaken by the intent notification
      UsbInterface dongleInterface = device.getInterface(0);
      UsbEndpoint in = null;
      UsbEndpoint out = null;
      boolean ledger;
      for (int i = 0; i < dongleInterface.getEndpointCount(); i++) {
         UsbEndpoint tmpEndpoint = dongleInterface.getEndpoint(i);
         if (tmpEndpoint.getDirection() == UsbConstants.USB_DIR_IN) {
            in = tmpEndpoint;
         } else {
            out = tmpEndpoint;
         }
      }
      UsbDeviceConnection connection = manager.openDevice(device);
      if (connection == null) {
         throw new ConnectionFailException("Connection was not established");
      }
      connection.claimInterface(dongleInterface, true);
      ledger = ((device.getProductId() == PID_HID_LEDGER)
            || (device.getProductId() == PID_HID_LEDGER_PROTON)
            || (device.getVendorId() == VID_LEDGER));
      if (device.getProductId() == PID_WINUSB) {
         return new BTChipTransportAndroidWinUSB(connection, dongleInterface, in, out, TIMEOUT);
      } else {
         return new BTChipTransportAndroidHID(connection, dongleInterface, in, out, TIMEOUT, ledger);
      }
   }

   public void setDetectedTag(Tag tag) {
      Log.d(LOG_TAG, "Setting detected tag " + tag);
      this.detectedTag = tag;
   }

   public void clearDetectedTag() {
      detectedTag = null;
   }

   public void setAID(byte[] aid) {
      this.aid = aid;
   }

   public static final String LOG_STRING = "BTChip";

   private static final int VID = 0x2581;
   private static final int VID_LEDGER = 0x2c97;
   private static final int PID_WINUSB = 0x1b7c;
   private static final int PID_HID = 0x2b7c;
   private static final int PID_HID_LEDGER = 0x3b7c;
   private static final int PID_HID_LEDGER_PROTON = 0x4b7c;
   private static final int TIMEOUT = 20000;
}
