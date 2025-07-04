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
package com.mycelium.wallet.activity.settings.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mycelium.wallet.R;
import com.mycelium.wallet.activity.modern.Toaster;
import com.mycelium.wallet.activity.settings.adapter.DialogListAdapter;

public class DisplayPreferenceDialogHandler implements PreferenceManager.OnDisplayPreferenceDialogListener {
    private Context context;
    private AlertDialog alertDialog;

    public DisplayPreferenceDialogHandler(Context context) {
        this.context = context;
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        int theme = R.style.MyceliumSettings_Dialog_Small;

        if (preference instanceof ListPreference) {
            final ListPreference listPreference = (ListPreference) preference;
            final int origSize = listPreference.getEntryValues().length;

            View view = LayoutInflater.from(context).inflate(R.layout.dialog_pref_list, null);
            TextView title = view.findViewById(R.id.title);
            title.setText(listPreference.getDialogTitle());
            final RecyclerView listView = view.findViewById(android.R.id.list);
            listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

            final int selectedIndex = listPreference.findIndexOfValue(listPreference.getValue());
            final DialogListAdapter arrayAdapter = new DialogListAdapter(listPreference.getEntries(), selectedIndex);
            listView.setAdapter(arrayAdapter);
            listView.scrollToPosition(selectedIndex);
            view.findViewById(R.id.buttonCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });

            view.findViewById(R.id.buttonok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // it seems that the size of a list (such as exchanges) might change before clicking OK
                    // so we compare it to new value just before proceeding
                    final int newSize = listPreference.getEntryValues().length;
                    if (newSize == origSize && arrayAdapter.getSelected() >= 0 &&  arrayAdapter.getSelected() < newSize) {
                        String value = listPreference.getEntryValues()[arrayAdapter.getSelected()].toString();
                        if (listPreference.callChangeListener(value)) {
                            listPreference.setValue(value);
                        }
                    } else {
                        new Toaster(context).toast(R.string.try_again, true);
                    }
                    alertDialog.dismiss();
                }
            });
            alertDialog = new AlertDialog.Builder(context, theme)
                    .setView(view)
                    .create();
            alertDialog.show();
        } else {
            throw new IllegalArgumentException("Tried to display dialog for unknown " +
                    "preference type. Did you forget to override onDisplayPreferenceDialog()?");
        }
    }
}
