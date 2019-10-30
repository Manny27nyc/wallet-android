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

package com.mycelium.wallet.activity.main;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mycelium.wallet.R;
import com.mycelium.wallet.activity.main.adapter.RecommendationAdapter;
import com.mycelium.wallet.activity.main.model.PartnerInfo;
import com.mycelium.wallet.activity.main.model.RecommendationBanner;
import com.mycelium.wallet.activity.main.model.RecommendationFooter;
import com.mycelium.wallet.activity.main.model.RecommendationHeader;
import com.mycelium.wallet.activity.main.model.RecommendationInfo;
import com.mycelium.wallet.activity.settings.SettingsPreference;
import com.mycelium.wallet.activity.view.DividerItemDecoration;
import com.mycelium.wallet.external.Ads;

import java.util.ArrayList;
import java.util.List;

import static com.mycelium.wallet.R.string.cancel;
import static com.mycelium.wallet.R.string.ok;
import static com.mycelium.wallet.R.string.partner_ledger;
import static com.mycelium.wallet.R.string.partner_ledger_info;
import static com.mycelium.wallet.R.string.partner_ledger_short;
import static com.mycelium.wallet.R.string.partner_ledger_url;
import static com.mycelium.wallet.R.string.partner_more_info_text;
import static com.mycelium.wallet.R.string.partner_purse;
import static com.mycelium.wallet.R.string.partner_purse_info;
import static com.mycelium.wallet.R.string.partner_purse_short;
import static com.mycelium.wallet.R.string.partner_purse_url;
import static com.mycelium.wallet.R.string.partner_safervpn;
import static com.mycelium.wallet.R.string.partner_safervpn_info;
import static com.mycelium.wallet.R.string.partner_safervpn_short;
import static com.mycelium.wallet.R.string.partner_safervpn_url;
import static com.mycelium.wallet.R.string.partner_trezor;
import static com.mycelium.wallet.R.string.partner_trezor_info;
import static com.mycelium.wallet.R.string.partner_trezor_short;
import static com.mycelium.wallet.R.string.partner_trezor_url;
import static com.mycelium.wallet.R.string.warning_partner;
import static com.mycelium.wallet.R.string.your_privacy_out_priority;

public class RecommendationsFragment extends Fragment {
    private AlertDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.main_recommendations_view, container, false);
        RecyclerView recommendationsList = root.findViewById(R.id.list);

        List<RecommendationInfo> list = new ArrayList<>();
        int fromItem = 1;
        list.add(new RecommendationHeader());

        list.add(getPartnerInfo(partner_ledger, partner_ledger_short, partner_ledger_info, partner_ledger_url, R.drawable.ledger_icon));
        list.add(getPartnerInfo(partner_trezor, partner_trezor_short, partner_trezor_info, partner_trezor_url, R.drawable.trezor2));
        list.add(getPartnerInfo(partner_purse, partner_purse_short, partner_purse_info, partner_purse_url, R.drawable.purse_small));
        list.add(getPartnerInfo(partner_safervpn, partner_safervpn_short, partner_safervpn_info, partner_safervpn_url, R.drawable.safervpn_icon_small));

        if (SettingsPreference.getFioEnabled()) {
            list.add(getPartnerInfo(R.string.partner_fiopresale, R.string.partner_fiopresale_short, R.string.partner_fiopresale_info, R.drawable.ic_fiopresale_icon_small,
                    () -> Ads.openFio(requireContext())));
        }

        list.add(new RecommendationFooter());
        RecommendationAdapter adapter = new RecommendationAdapter(list);

        adapter.setClickListener(new RecommendationAdapter.ClickListener() {
            @Override
            public void onClick(final PartnerInfo bean) {
                if (bean.getInfo() != null && bean.getInfo().length() > 0) {
                    alertDialog = new AlertDialog.Builder(getActivity())
                            .setMessage(bean.getInfo())
                            .setTitle(warning_partner)
                            .setIcon(bean.getSmallIcon())
                            .setPositiveButton(ok, (dialog, id) -> {
                                if (bean.getUri() != null) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW)
                                            .setData(Uri.parse(bean.getUri()));
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton(cancel, null)
                            .create();
                    alertDialog.show();
                } else {
                    if (bean.getUri() != null) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(bean.getUri())));
                    }
                }
            }

            @Override
            public void onClick(RecommendationFooter recommendationFooter) {
                alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle(your_privacy_out_priority)
                        .setMessage(partner_more_info_text)
                        .setPositiveButton(ok, null)
                        .setIcon(R.drawable.mycelium_logo_transp_small)
                        .create();
                alertDialog.show();
            }

            @Override
            public void onClick(RecommendationBanner recommendationBanner) {
                // implement when using big Banner Ad
            }
        });
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getResources().getDrawable(R.drawable.divider_account_list), LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setFromItem(fromItem);
        recommendationsList.addItemDecoration(dividerItemDecoration);
        recommendationsList.setAdapter(adapter);
        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        try {
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private PartnerInfo getPartnerInfo(int name, int description, int disclaimer, int uri, int icon) {
        return new PartnerInfo(getString(name), getString(description), getString(disclaimer), getString(uri), icon);
    }

    private PartnerInfo getPartnerInfo(int name, int description, int disclaimer, int icon, Runnable action) {
        return new PartnerInfo(getString(name), getString(description), getString(disclaimer), icon, action);
    }
}
