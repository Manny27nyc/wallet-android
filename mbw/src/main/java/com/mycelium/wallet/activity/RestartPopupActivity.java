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

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Html;
import android.view.Window;

import com.mycelium.wallet.R;

public class RestartPopupActivity extends AppCompatActivity {
    public static final String RESTART_WARNING_HEADER = "RESTART_WARNING_HEADER";
    public static final String RESTART_REQUIRED = "RESTART_REQUIRED";

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_activity);
        final Boolean restartRequired = getIntent().getBooleanExtra(RESTART_REQUIRED, true);

        AlertDialog.Builder Builder = new AlertDialog.Builder(this, R.style.MyceliumModern_Dialog)
                .setTitle(Html.fromHtml(getIntent().getExtras().getString(RESTART_WARNING_HEADER, "")))
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RestartPopupActivity.this.finish();
                        if (restartRequired) restart();
                    }
                });
        if (restartRequired) Builder.setMessage(R.string.configuration_change_restart_warning);
        AlertDialog alertDialog = Builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void restart() {
        Intent intent = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
