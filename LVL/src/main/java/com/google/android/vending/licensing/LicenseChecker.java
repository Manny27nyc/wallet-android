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
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.vending.licensing;

import com.google.android.vending.licensing.util.Base64;
import com.google.android.vending.licensing.util.Base64DecoderException;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings.Secure;
import android.util.Log;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Client library for Android Market license verifications.
 * <p>
 * The LicenseChecker is configured via a {@link Policy} which contains the
 * logic to determine whether a user should have access to the application. For
 * example, the Policy can define a threshold for allowable number of server or
 * client failures before the library reports the user as not having access.
 * <p>
 * Must also provide the Base64-encoded RSA public key associated with your
 * developer account. The public key is obtainable from the publisher site.
 */
public class LicenseChecker implements ServiceConnection {
    private static final String TAG = "LicenseChecker";

    // Timeout value (in milliseconds) for calls to service.
    private static final int TIMEOUT_MS = 10 * 1000;

    private static final boolean DEBUG_LICENSE_ERROR = false;

    private ILicensingService mService;

    //Tom: no need of public key, since the is no client side validation of the response.
    //private PublicKey mPublicKey;
    private long mRequestNonce;
    private final Context mContext;
    private final Policy mPolicy;
    /**
     * A handler for running tasks on a background thread. We don't want license
     * processing to block the UI thread.
     */
    private Handler mHandler;
    private final String mPackageName;
    private final String mVersionCode;
    private final Set<LicenseValidator> mChecksInProgress = new HashSet<>();
    private final Queue<LicenseValidator> mPendingChecks = new LinkedList<>();

    /**
     * @param context a Context
     * @param policy implementation of Policy
     * @param requestNonce provided nonce
     * @throws IllegalArgumentException if encodedPublicKey is invalid
     */
    public LicenseChecker(Context context, Policy policy, long requestNonce) {
        mContext = context;
        mPolicy = policy;
        //Tom: public key was removed from client side.
        //mPublicKey = generatePublicKey(encodedPublicKey);
        mRequestNonce = requestNonce;
        mPackageName = mContext.getPackageName();
        mVersionCode = getVersionCode(context, mPackageName);
        HandlerThread handlerThread = new HandlerThread("background thread");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
    }

    /**
     * Checks if the user should have access to the app.  Binds the service if necessary.
     * <p>
     * NOTE: This call uses a trivially obfuscated string (base64-encoded).  For best security,
     * we recommend obfuscating the string that is passed into bindService using another method
     * of your own devising.
     * <p>
     * source string: "com.android.vending.licensing.ILicensingService"
     * <p>
     * @param callback
     */
    public synchronized void checkAccess(LicenseCheckerCallback callback) {
        // If we have a valid recent LICENSED response, we can skip asking
        // Market.
        if (mPolicy.allowAccess()) {
            Log.i(TAG, "Using cached license response");
            callback.allow(Policy.LICENSED);
        } else {
            LicenseValidator validator = new LicenseValidator(mPolicy, new NullDeviceLimiter(),
                    callback, getNonce(), mPackageName, mVersionCode);

            if (mService == null) {
                Log.i(TAG, "Binding to licensing service.");
                try {
                    final Intent serviceIntent = new Intent(
                            new String(Base64.decode("Y29tLmFuZHJvaWQudmVuZGluZy5saWNlbnNpbmcuSUxpY2Vuc2luZ1NlcnZpY2U=")));
                    serviceIntent.setPackage("com.android.vending");
                    boolean bindResult = mContext
                            .bindService(
                                    serviceIntent,
                                    this, // ServiceConnection.
                                    Context.BIND_AUTO_CREATE);

                    if (bindResult) {
                        mPendingChecks.offer(validator);
                    } else {
                        Log.e(TAG, "Could not bind to service.");
                        handleServiceConnectionError(validator);
                    }
                } catch (SecurityException e) {
                    callback.applicationError(LicenseCheckerCallback.ERROR_MISSING_PERMISSION);
                } catch (Base64DecoderException e) {
                    e.printStackTrace();
                }
            } else {
                mPendingChecks.offer(validator);
                runChecks();
            }
        }
    }

    private void runChecks() {
        LicenseValidator validator;
        while ((validator = mPendingChecks.poll()) != null) {
            try {
                Log.i(TAG, "Calling checkLicense on service for " + validator.getPackageName());
                Log.i("Tom", "nonce: " + validator.getNonce());
                mService.checkLicense(
                        validator.getNonce(), validator.getPackageName(),
                        new ResultListener(validator));
                mChecksInProgress.add(validator);
            } catch (RemoteException e) {
                Log.w(TAG, "RemoteException in checkLicense call.", e);
                handleServiceConnectionError(validator);
            }
        }
    }

    private synchronized void finishCheck(LicenseValidator validator) {
        mChecksInProgress.remove(validator);
        if (mChecksInProgress.isEmpty()) {
            cleanupService();
        }
    }

    private class ResultListener extends ILicenseResultListener.Stub {
        private final LicenseValidator mValidator;
        private Runnable mOnTimeout;

        public ResultListener(LicenseValidator validator) {
            mValidator = validator;
            mOnTimeout = new Runnable() {
                public void run() {
                    Log.i(TAG, "Check timed out.");
                    handleServiceConnectionError(mValidator);
                    finishCheck(mValidator);
                }
            };
            startTimeout();
        }

        private static final int ERROR_CONTACTING_SERVER = 0x101;
        private static final int ERROR_INVALID_PACKAGE_NAME = 0x102;
        private static final int ERROR_NON_MATCHING_UID = 0x103;

        // Runs in IPC thread pool. Post it to the Handler, so we can guarantee
        // either this or the timeout runs.
        public void verifyLicense(final int responseCode, final String signedData,
                final String signature) {
            mHandler.post(new Runnable() {
                public void run() {
                    Log.i(TAG, "Received response.");
                    // Make sure it hasn't already timed out.
                    if (mChecksInProgress.contains(mValidator)) {
                        clearTimeout();
                        //Tom:removed the in app verify
                        //mValidator.verify(mPublicKey, responseCode, signedData, signature);

                        //Tom: all request are forward fo validation
                        finishCheck(mValidator);
                        mValidator.getCallback().licensingResponse(responseCode, signedData, signature);
                    }
                    if (DEBUG_LICENSE_ERROR) {
                        boolean logResponse;
                        String stringError = null;
                        switch (responseCode) {
                            case ERROR_CONTACTING_SERVER:
                                logResponse = true;
                                stringError = "ERROR_CONTACTING_SERVER";
                                break;
                            case ERROR_INVALID_PACKAGE_NAME:
                                logResponse = true;
                                stringError = "ERROR_INVALID_PACKAGE_NAME";
                                break;
                            case ERROR_NON_MATCHING_UID:
                                logResponse = true;
                                stringError = "ERROR_NON_MATCHING_UID";
                                break;
                            default:
                                logResponse = false;
                        }

                        if (logResponse) {
                            String android_id = Secure.getString(mContext.getContentResolver(),
                                    Secure.ANDROID_ID);
                            Date date = new Date();
                            Log.d(TAG, "Server Failure: " + stringError);
                            Log.d(TAG, "Android ID: " + android_id);
                            Log.d(TAG, "Time: " + date.toGMTString());
                        }
                    }

                }
            });
        }

        private void startTimeout() {
            Log.i(TAG, "Start monitoring timeout.");
            mHandler.postDelayed(mOnTimeout, TIMEOUT_MS);
        }

        private void clearTimeout() {
            Log.i(TAG, "Clearing timeout.");
            mHandler.removeCallbacks(mOnTimeout);
        }
    }

    public synchronized void onServiceConnected(ComponentName name, IBinder service) {
        mService = ILicensingService.Stub.asInterface(service);
        runChecks();
    }

    public synchronized void onServiceDisconnected(ComponentName name) {
        // Called when the connection with the service has been
        // unexpectedly disconnected. That is, Market crashed.
        // If there are any checks in progress, the timeouts will handle them.
        Log.w(TAG, "Service unexpectedly disconnected.");
        mService = null;
    }

    /**
     * Generates policy response for service connection errors, as a result of
     * disconnections or timeouts.
     */
    private synchronized void handleServiceConnectionError(LicenseValidator validator) {
        mPolicy.processServerResponse(Policy.RETRY, null);

        if (mPolicy.allowAccess()) {
            validator.getCallback().allow(Policy.RETRY);
        } else {
            validator.getCallback().dontAllow(Policy.RETRY);
        }
    }

    /** Unbinds service if necessary and removes reference to it. */
    private void cleanupService() {
        if (mService != null) {
            try {
                mContext.unbindService(this);
            } catch (IllegalArgumentException e) {
                // Somehow we've already been unbound. This is a non-fatal
                // error.
                Log.e(TAG, "Unable to unbind from licensing service (already unbound)");
            }
            mService = null;
        }
    }

    /**
     * Inform the library that the context is about to be destroyed, so that any
     * open connections can be cleaned up.
     * <p>
     * Failure to call this method can result in a crash under certain
     * circumstances, such as during screen rotation if an Activity requests the
     * license check or when the user exits the application.
     */
    public synchronized void onDestroy() {
        cleanupService();
        mHandler.getLooper().quit();
    }

    /** get a nonce (number used once). */
    private long getNonce() {
        return mRequestNonce;
    }

    /**
     * Get version code for the application package name.
     * 
     * @param packageName application package name
     * @return the version code or empty string if package not found
     */
    private static String getVersionCode(Context context, String packageName) {
        try {
            return String.valueOf(context.getPackageManager().getPackageInfo(packageName, 0).
                    versionCode);
        } catch (NameNotFoundException e) {
            Log.e(TAG, "Package not found. could not get version code.");
            return "";
        }
    }
}
