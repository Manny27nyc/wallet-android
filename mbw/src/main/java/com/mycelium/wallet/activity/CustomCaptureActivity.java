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
package com.mycelium.wallet.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.mycelium.wallet.BuildConfig;
import com.mycelium.wallet.R;
import com.mycelium.wallet.Utils;
import com.mycelium.wallet.activity.modern.Toaster;

@SuppressLint("RestrictedApi")
public class CustomCaptureActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener {

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private ImageButton switchFlashlightButton;
    private ImageButton switchCameraButton;
    private boolean isFrontCamera = false;
    private boolean isFlashOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_capture);

        switchFlashlightButton = findViewById(R.id.switch_flashlight);
        switchCameraButton = findViewById(R.id.switch_camera);
        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView.setTorchListener(this);

        if (!hasFlash()) {
            switchFlashlightButton.setVisibility(View.GONE);
        }

        if (!hasFrontAndBackCamera()) {
            switchCameraButton.setVisibility(View.GONE);
        }

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (BuildConfig.DEBUG && keyCode == KeyEvent.KEYCODE_C) {
            // press c while in the scanner to copy the clipboard as scan result
            String clipboardContent = Utils.getClipboardString(this);
            new Toaster(this).toast("Taking clipboard content as return value of scan: " + clipboardContent, false);
            setResult(RESULT_OK, new Intent()
                    .putExtra("SCAN_RESULT_FORMAT", "QR_CODE")
                    .putExtra("SCAN_RESULT", clipboardContent));
            finish();
            return true;
        }
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    private boolean hasFrontAndBackCamera() {
        PackageManager pm = getPackageManager();
        boolean frontCam, rearCam;

        frontCam = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
        rearCam = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);

        return frontCam && rearCam;
    }

    public void onSwitchFlashlightPressed(View view) {
        if (isFlashOn) {
            setTorchOff();
        } else {
            barcodeScannerView.setTorchOn();
            isFlashOn = true;
        }
    }

    private void setTorchOff() {
        barcodeScannerView.setTorchOff();
        isFlashOn = false;
    }

    @Override
    public void onTorchOn() {
        // it is also possible to turn flash from volume keys, preventing that.
        if (!isFrontCamera) {
            switchFlashlightButton.setImageDrawable(getResources().getDrawable(R.drawable.camera_flash_on));
        }
    }

    @Override
    public void onTorchOff() {
        switchFlashlightButton.setImageDrawable(getResources().getDrawable(R.drawable.camera_flash_off));
    }

    public void onSwitchCameraPressed(View view) {
        setTorchOff();

        if (isFrontCamera) {
            switchFlashlightButton.setVisibility(View.VISIBLE);
            barcodeScannerView.initializeFromIntent(new Intent().putExtra(Intents.Scan.CAMERA_ID, Camera.CameraInfo.CAMERA_FACING_BACK));
            isFrontCamera = false;
        } else {
            switchFlashlightButton.setVisibility(View.GONE);
            barcodeScannerView.initializeFromIntent(new Intent().putExtra(Intents.Scan.CAMERA_ID, Camera.CameraInfo.CAMERA_FACING_FRONT));
            isFrontCamera = true;
        }
        resetCaptureManager();
    }

    private void resetCaptureManager() {
        capture.onPause();
        capture.onResume();
    }

    public void onUpdateFocusPressed(View view) {
        resetCaptureManager();
    }

    public void onCloseCameraPressed(View view) {
        finish();
    }
}
