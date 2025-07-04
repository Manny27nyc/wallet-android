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

package com.ledger.tbase.comm;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.Future;

import android.content.Context;
import android.util.Log;

import com.btchip.BTChipConstants;
import com.btchip.BTChipException;
import com.btchip.comm.BTChipTransport;
import com.btchip.comm.android.BTChipTransportAndroid;
import com.btchip.utils.Dump;
import com.btchip.utils.FutureUtils;
import com.ledger.wallet.service.ILedgerWalletService;
import com.ledger.wallet.service.ServiceResult;

public class LedgerTransportTEEProxy implements BTChipTransport {
	
	public static final String TAG="LedgerTransportTEEProxy";
		
	private Context context;
	private ILedgerWalletService service;
	private byte[] session;
	private byte[] nvm;
	private boolean debug;
	private boolean teeImplementation;
	
	private static final byte FEATURES_TEE = (byte)0x01;
	private static final byte FEATURES_JAVACARD_PROXY = (byte)0x02;	
	
	private static final byte PROTOCOL_CARD = (byte)0x01;
		
	private static final byte[] APDU_INIT[] = {
		Dump.hexToBin("D020000038000000000000000118F43F95A217EFEDE0A8D98DAC357E3B2501E79C3958B9D7E15238D43A6807C397680EB805BC0E95E2B65D9E49B1B045"),
		Dump.hexToBin("D02200002B000000020000000120B25006C589F0DCF1BBB75BAA1542A5E6CF300995F0046DE59CC641C0798D9D489006")
	};
	
	private static final int SW_OK = 0x9000;
	private static final int SW_CONDITIONS_NOT_SATISFIED = 0x6985;
		
	public LedgerTransportTEEProxy(Context context, ILedgerWalletService service) {
		this.context = context;
		this.service = service;
	}
	
	public LedgerTransportTEEProxy(Context context) {
		this(context, null);
	}
		
	public byte[] getNVM() {
		return nvm;
	}
	public void setNVM(byte[] nvm) {
		this.nvm = nvm;
	}
	
	public void setService(ILedgerWalletService service) {
		this.service = service;
	}
	
	public ILedgerWalletService getService() {
		return service;
	}
	
	public boolean hasTeeImplementation() {
		return teeImplementation;
	}
	
	public boolean init() {
		ServiceResult result = null;
		
		if (service == null) {
			Log.d(TAG, "Cannot initialize until service is available");
			return false;
		}
		
		try {
			result = service.getServiceFeatures();
		}
		catch(Exception e) {
			Log.d(TAG, "Failed to query service features (internal)", e);
			return false;
		}
		teeImplementation = ((result.getResult()[0] & FEATURES_TEE) != (byte)0);
		
		try {
			result = service.openDefault();
		}
		catch(Exception e) {
			Log.d(TAG, "Failed to open application (internal)", e);
			return false;
		}
		if (result.getExceptionMessage() != null) {
			Log.d(TAG, "Failed to open application (service) " + result.getExceptionMessage());
			return false;
		}
		session = result.getResult();
		try {
			result = service.initStorage(session, nvm);
		}
		catch(Exception e) {
			Log.d(TAG, "Failed to initialize NVM (internal)", e);
			try {
				close();
			}
			catch(Exception e1) {				
			}
			return false;
		}
		if (result.getExceptionMessage() != null) {
			Log.d(TAG, "Failed to initialize NVM (service) " + result.getExceptionMessage());
			try {
				close();
			}
			catch(Exception e1) {				
			}
			return false;
		}
		if (teeImplementation) {
			try {
				for (byte[] apdu : APDU_INIT) {
					int sw = 0;
					byte[] response = exchange(apdu).get();
					if ((response != null) && (response.length > 2)) {
						sw = ((response[response.length - 2] & 0xff) << 8) | (response[response.length - 1] & 0xff);
						if ((sw != SW_OK) && (sw != SW_CONDITIONS_NOT_SATISFIED)) {
							throw new BTChipException("Invalid response status " + Integer.toHexString(sw));
						}
					}
				}
			}
			catch(Exception e) {
				try {
					close();
				}
				catch(Exception e1) {				
				}
				Log.d(TAG, "Init failed", e);
				return false;
			}
		}
						
		return true;
	}
	
	private boolean needExternalUI(byte[] commandParam) {
		if (!teeImplementation) {
			return false;
		}
		// Some commands need to call the Trusted UI :
		// SETUP, VERIFY PIN, HASH INPUT FINALIZE, HASH INPUT FINALIZE FULL
		byte ins = commandParam[1];
		switch(ins) {
			case BTChipConstants.BTCHIP_INS_SETUP:
			case BTChipConstants.BTCHIP_INS_VERIFY_PIN:
			case BTChipConstants.BTCHIP_INS_HASH_INPUT_FINALIZE:
			case BTChipConstants.BTCHIP_INS_HASH_INPUT_FINALIZE_FULL:
				return true;
			default:
				return false;
		}
	}

	@Override
	public Future<byte[]> exchange(byte[] command) throws BTChipException {
		ServiceResult result = null;
				
		if (debug) {
			Log.d(BTChipTransportAndroid.LOG_STRING, "=> " + Dump.dump(command));
		}
		if (service == null) {
			throw new BTChipException("Service is not available");
		}		
		if (session == null) {
			throw new BTChipException("Session is not open");
		}
		try {
			if (needExternalUI(command)) {
				result = service.exchangeExtendedUI(session, command);						
			}
			else {
				result = service.exchange(session, command);
			}
		}
		catch(Exception e) {
			throw new BTChipException("Exception calling service", e);
		}
		if (result.getExceptionMessage() != null) {
			Log.d(TAG, "Exchange failed " + result.getExceptionMessage());
			return null;
		}
		Log.d(BTChipTransportAndroid.LOG_STRING, "<= " + Dump.dump(result.getResult()));		
		return FutureUtils.getDummyFuture(result.getResult());
	}

	@Override
	public void close() throws BTChipException {
		
		if (service == null) {
			throw new BTChipException("Service is not available");
		}
		if (session == null) {
			return;
		}		
		try {
			service.close(session);
		}
		catch(Exception e) {
			throw new BTChipException("Exception calling service", e);
		}
		session = null;
	}

	@Override
	public void setDebug(boolean debugFlag) {
		this.debug = debugFlag;
	}
	
	public Future<byte[]> requestNVM() throws BTChipException {
		if (service == null) {
			throw new BTChipException("Service is not available");
		}		
		if (session == null) {
			throw new BTChipException("Session is not open");
		}	
		ServiceResult result = null;
		try {
			result = service.getStorage(session);
		}
		catch(Exception e) {
			throw new BTChipException("Exception calling service", e);
		}
		return FutureUtils.getDummyFuture(result.getResult());
	}
	
	public byte[] loadNVM(String nvmFile) {
		try {
			FileInputStream in = context.openFileInput(nvmFile);
			byte[] nvm = new byte[in.available()];
			in.read(nvm);
			return nvm;
		}
		catch(Exception e) {
			Log.d(TAG, "Unable to load NVM: " + e.getMessage());
			return null;
		}
	}
	
	public void writeNVM(String nvmFile, byte[] nvm) throws BTChipException {
		try {
			FileOutputStream out = context.openFileOutput(nvmFile, Context.MODE_PRIVATE);
			out.write(nvm);
   		 	out.flush();
   		 	out.close();			
		}
		catch(Exception e) {
			throw new BTChipException("Unable to write NVM", e);
		}
	}

}
