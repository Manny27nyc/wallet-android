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

package com.mycelium.wallet.activity.modern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.view.ActionMode.Callback;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mrd.bitlib.model.Address;
import com.mrd.bitlib.model.AddressType;
import com.mycelium.wallet.MbwManager;
import com.mycelium.wallet.R;
import com.mycelium.wallet.Utils;
import com.mycelium.wallet.activity.AddAccountActivity;
import com.mycelium.wallet.activity.AddAdvancedAccountActivity;
import com.mycelium.wallet.activity.MessageSigningActivity;
import com.mycelium.wallet.activity.export.VerifyBackupActivity;
import com.mycelium.wallet.activity.modern.adapter.AccountListAdapter;
import com.mycelium.wallet.activity.util.EnterAddressLabelUtil;
import com.mycelium.wallet.activity.util.ValueExtensionsKt;
import com.mycelium.wallet.activity.view.DividerItemDecoration;
import com.mycelium.wallet.event.AccountChanged;
import com.mycelium.wallet.event.AccountListChanged;
import com.mycelium.wallet.event.BalanceChanged;
import com.mycelium.wallet.event.ExchangeSourceChanged;
import com.mycelium.wallet.event.ExtraAccountsChanged;
import com.mycelium.wallet.event.ReceivingAddressChanged;
import com.mycelium.wallet.event.SelectedCurrencyChanged;
import com.mycelium.wallet.event.SyncProgressUpdated;
import com.mycelium.wallet.event.SyncStarted;
import com.mycelium.wallet.event.SyncStopped;
import com.mycelium.wallet.lt.LocalTraderEventSubscriber;
import com.mycelium.wallet.lt.LocalTraderManager;
import com.mycelium.wallet.lt.api.CreateTrader;
import com.mycelium.wallet.lt.api.DeleteTrader;
import com.mycelium.wallet.persistence.MetadataStorage;
import com.mycelium.wapi.wallet.AddressUtils;
import com.mycelium.wapi.wallet.AesKeyCipher;
import com.mycelium.wapi.wallet.ExportableAccount;
import com.mycelium.wapi.wallet.GenericAddress;
import com.mycelium.wapi.wallet.KeyCipher;
import com.mycelium.wapi.wallet.SyncMode;
import com.mycelium.wapi.wallet.WalletAccount;
import com.mycelium.wapi.wallet.WalletManager;
import com.mycelium.wapi.wallet.bch.bip44.Bip44BCHAccount;
import com.mycelium.wapi.wallet.bch.single.SingleAddressBCHAccount;
import com.mycelium.wapi.wallet.btc.BtcAddress;
import com.mycelium.wapi.wallet.btc.bip44.BitcoinHDModule;
import com.mycelium.wapi.wallet.btc.bip44.HDAccount;
import com.mycelium.wapi.wallet.btc.bip44.HDAccountExternalSignature;
import com.mycelium.wapi.wallet.btc.bip44.HDPubOnlyAccount;
import com.mycelium.wapi.wallet.btc.single.SingleAddressAccount;
import com.mycelium.wapi.wallet.coinapult.CoinapultAccount;
import com.mycelium.wapi.wallet.coinapult.CoinapultModule;
import com.mycelium.wapi.wallet.coins.Balance;
import com.mycelium.wapi.wallet.coins.Value;
import com.mycelium.wapi.wallet.colu.AddressColuConfig;
import com.mycelium.wapi.wallet.colu.ColuAccount;
import com.mycelium.wapi.wallet.colu.ColuAccountContext;
import com.mycelium.wapi.wallet.colu.coins.ColuMain;
import com.mycelium.wapi.wallet.manager.Config;
import com.mycelium.wapi.wallet.manager.State;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.mycelium.wapi.wallet.btc.bip44.BitcoinHDModuleKt.getActiveMasterseedAccounts;
import static com.mycelium.wapi.wallet.btc.bip44.BitcoinHDModuleKt.getActiveMasterseedHDAccounts;
import static com.mycelium.wapi.wallet.colu.ColuModuleKt.getColuAccounts;

public class AccountsFragment extends Fragment {
    public static final int ADD_RECORD_RESULT_CODE = 0;

    public static final String TAG = "AccountsFragment";

    private WalletManager walletManager;

    private MetadataStorage _storage;
    private MbwManager _mbwManager;
    private LocalTraderManager localTraderManager;
    private Toaster _toaster;
    private ProgressDialog _progress;
    private RecyclerView rvRecords;
    private View llLocked;
    private AccountListAdapter accountListAdapter;
    private View root;
    private Bus eventBus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root == null) {
            root = inflater.inflate(R.layout.records_activity, container, false);
        }
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (rvRecords == null) {
            rvRecords = view.findViewById(R.id.rvRecords);
            rvRecords.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            accountListAdapter = new AccountListAdapter(this, _mbwManager);
            rvRecords.setAdapter(accountListAdapter);
            rvRecords.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider_account_list)
                    , LinearLayoutManager.VERTICAL));
            rvRecords.setHasFixedSize(true);
        }
        if (llLocked == null) {
            llLocked = view.findViewById(R.id.llLocked);
        }
        accountListAdapter.setItemClickListener(recordAddressClickListener);
    }

    @Override
    public void onAttach(Context context) {
        _mbwManager = MbwManager.getInstance(context);
        walletManager = _mbwManager.getWalletManager(false);
        localTraderManager = _mbwManager.getLocalTraderManager();
        localTraderManager.subscribe(ltSubscriber);
        _storage = _mbwManager.getMetadataStorage();
        eventBus = MbwManager.getEventBus();
        _toaster = new Toaster(this);
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        eventBus.register(this);
        getView().findViewById(R.id.btUnlock).setOnClickListener(unlockClickedListener);
        update();
        _progress = new ProgressDialog(getActivity());
        super.onResume();
    }

    @Override
    public void onPause() {
        _progress.dismiss();
        eventBus.unregister(this);
        super.onPause();
    }

    @Override
    public void onDetach() {
        localTraderManager.unsubscribe(ltSubscriber);
        super.onDetach();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) {
            finishCurrentActionMode();
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        requireActivity().invalidateOptionsMenu();
        if (requestCode == ADD_RECORD_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            UUID accountid = (UUID) intent.getSerializableExtra(AddAccountActivity.RESULT_KEY);
            if (accountid != null) {
                //check whether the account is active - we might have scanned the priv key for an archived watchonly
                WalletAccount account = walletManager.getAccount(accountid);
                if (account.isActive()) {
                    _mbwManager.setSelectedAccount(accountid);
                }
                accountListAdapter.setFocusedAccountId(account.getId());
                updateIncludingMenus();
                if (!(account instanceof ColuAccount)
                        && !intent.getBooleanExtra(AddAccountActivity.IS_UPGRADE, false)) {

                    setLabelOnAccount(account, account.getLabel(), false);
                }
                eventBus.post(new ExtraAccountsChanged());
                eventBus.post(new AccountChanged(accountid));
            }
        } else if(requestCode == ADD_RECORD_RESULT_CODE && resultCode == AddAdvancedAccountActivity.RESULT_MSG) {
            new AlertDialog.Builder(getActivity())
                    .setMessage(intent.getStringExtra(AddAccountActivity.RESULT_MSG))
                    .setPositiveButton(R.string.button_ok, null)
                    .create()
                    .show();
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    private void deleteAccount(final WalletAccount accountToDelete) {
        checkNotNull(accountToDelete);
        final WalletAccount linkedAccount = getLinkedAccount(accountToDelete);

        final View checkBoxView = View.inflate(getActivity(), R.layout.delkey_checkbox, null);
        final CheckBox keepAddrCheckbox = checkBoxView.findViewById(R.id.checkbox);
        keepAddrCheckbox.setText(getString(R.string.keep_account_address));
        keepAddrCheckbox.setChecked(false);

        final AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getActivity());
        deleteDialog.setTitle(R.string.delete_account_title);
        deleteDialog.setMessage(Html.fromHtml(createDeleteDialogText(accountToDelete, linkedAccount)));

        // add checkbox only for SingleAddressAccounts and only if a private key is present
        final boolean hasPrivateData = (accountToDelete instanceof ExportableAccount
                && ((ExportableAccount) accountToDelete).getExportData(AesKeyCipher.defaultKeyCipher()).getPrivateData().isPresent());

        if (accountToDelete instanceof SingleAddressAccount && hasPrivateData) {
            deleteDialog.setView(checkBoxView);
        }

        if (accountToDelete instanceof ColuAccount && accountToDelete.canSpend()) {
            Log.d(TAG, "Preparing to delete a colu account.");
            deleteDialog.setView(checkBoxView);
        }

        deleteDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Log.d(TAG, "Entering onClick delete");
                if (accountToDelete.getId().equals(localTraderManager.getLocalTraderAccountId())) {
                    localTraderManager.unsetLocalTraderAccount();
                }
                if (hasPrivateData) {
                    Long satoshis = getPotentialBalance(accountToDelete);
                    AlertDialog.Builder confirmDeleteDialog = new AlertDialog.Builder(getActivity());
                    confirmDeleteDialog.setTitle(R.string.confirm_delete_pk_title);

                    // Set the message. There are four combinations, with and without label, with and without BTC amount.
                    String label = _mbwManager.getMetadataStorage().getLabelByAccount(accountToDelete.getId());
                    int labelCount = 1;
                    if (linkedAccount != null) {
                        label += ", " + _mbwManager.getMetadataStorage().getLabelByAccount(linkedAccount.getId());
                        labelCount++;
                    }
                    String message;

                    // For active accounts we check whether there is money on them before deleting. we don't know if there
                    // is money on archived accounts
                    String address;
                    if (accountToDelete instanceof SingleAddressAccount) {
                        Map<AddressType, Address> addressMap = ((SingleAddressAccount) accountToDelete).getPublicKey().
                                getAllSupportedAddresses(_mbwManager.getNetwork());
                        address = TextUtils.join("\n\n", addressMap.values());
                    } else {
                        GenericAddress receivingAddress = accountToDelete.getReceiveAddress();
                        if (receivingAddress != null) {
                            address = AddressUtils.toMultiLineString(receivingAddress.toString());
                        } else {
                            address = "";
                        }
                    }
                    if (accountToDelete.isActive() && satoshis != null && satoshis > 0) {
                        if (label.length() != 0) {
                            message = getResources().getQuantityString(R.plurals.confirm_delete_pk_with_balance_with_label,
                                    !(accountToDelete instanceof SingleAddressAccount) ? 1 : 0,
                                    getResources().getQuantityString(R.plurals.account_label, labelCount, label),
                                    address, ValueExtensionsKt.toStringWithUnit(getBalance(accountToDelete)));
                        } else {
                            message = getResources().getQuantityString(R.plurals.confirm_delete_pk_with_balance,
                                    !(accountToDelete instanceof SingleAddressAccount) ? 1 : 0,
                                    ValueExtensionsKt.toStringWithUnit(getBalance(accountToDelete)));
                        }
                    } else {
                        if (label.length() != 0) {
                            message = getResources().getQuantityString(R.plurals.confirm_delete_pk_without_balance_with_label,
                                    !(accountToDelete instanceof SingleAddressAccount) ? 1 : 0,
                                    getResources().getQuantityString(R.plurals.account_label, labelCount, label), address);
                        } else {
                            message = getResources().getQuantityString(R.plurals.confirm_delete_pk_without_balance,
                                    !(accountToDelete instanceof SingleAddressAccount) ? 1 : 0, address);
                        }
                    }
                    confirmDeleteDialog.setMessage(message);

                    confirmDeleteDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            Log.d(TAG, "In deleteFragment onClick");
                            if (keepAddrCheckbox.isChecked() && accountToDelete instanceof SingleAddressAccount) {
                                try {
                                    //Check if this SingleAddress account is related with ColuAccount
                                    WalletAccount linkedColuAccount = Utils.getLinkedAccount(accountToDelete, walletManager.getAccounts());
                                    if (linkedColuAccount instanceof ColuAccount) {
                                        walletManager.deleteAccount(linkedColuAccount.getId());
                                        walletManager.deleteAccount(accountToDelete.getId());
                                        ColuAccountContext context = ((ColuAccount) linkedColuAccount).getContext();
                                        ColuMain coluMain = (ColuMain) linkedColuAccount.getCoinType();
                                        Config config = new AddressColuConfig(context.getAddress().get(AddressType.P2PKH), coluMain);
                                        _storage.deleteAccountMetadata(linkedColuAccount.getId());
                                        walletManager.createAccounts(config);
                                    } else {
                                        ((SingleAddressAccount) accountToDelete).forgetPrivateKey(AesKeyCipher.defaultKeyCipher());
                                    }
                                    _toaster.toast(R.string.private_key_deleted, false);
                                } catch (KeyCipher.InvalidKeyCipher e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                if (accountToDelete instanceof ColuAccount) {
                                    try {
                                        walletManager.deleteAccount(accountToDelete.getId());
                                        WalletAccount linkedAccount = Utils.getLinkedAccount(accountToDelete, walletManager.getAccounts());
                                        if (linkedAccount != null) {
                                            walletManager.deleteAccount(linkedAccount.getId());
                                            _storage.deleteAccountMetadata(linkedAccount.getId());
                                        }
                                        if (keepAddrCheckbox.isChecked()) {
                                            ColuAccountContext context = ((ColuAccount) accountToDelete).getContext();
                                            ColuMain coluMain = (ColuMain) accountToDelete.getCoinType();
                                            Config config = new AddressColuConfig(context.getAddress().get(AddressType.P2PKH), coluMain);
                                            _storage.deleteAccountMetadata(accountToDelete.getId());
                                            walletManager.createAccounts(config);
                                        } else {
                                            _storage.deleteAccountMetadata(accountToDelete.getId());
                                            _toaster.toast("Deleting account.", false);
                                            _mbwManager.setSelectedAccount(_mbwManager.getWalletManager(false).getActiveAccounts().get(0).getId());
                                        }
                                    } catch (Exception e) {
                                        // make a message !
                                        Log.e(TAG, getString(R.string.colu_error_deleting), e);
                                        _toaster.toast(getString(R.string.colu_error_deleting), false);
                                    }
                                } else {
                                    //Check if this SingleAddress account is related with ColuAccount
                                    WalletAccount linkedColuAccount = Utils.getLinkedAccount(accountToDelete, walletManager.getAccounts());
                                    if (linkedColuAccount instanceof ColuAccount) {
                                        walletManager.deleteAccount(linkedColuAccount.getId());
                                        _storage.deleteAccountMetadata(linkedColuAccount.getId());
                                    }
                                    walletManager.deleteAccount(accountToDelete.getId());
                                    _storage.deleteAccountMetadata(accountToDelete.getId());
                                    _mbwManager.setSelectedAccount(_mbwManager.getWalletManager(false).getActiveAccounts().get(0).getId());
                                    _toaster.toast(R.string.account_deleted, false);
                                }
                            }
                            finishCurrentActionMode();
                            eventBus.post(new AccountChanged(accountToDelete.getId()));
                        }
                    });
                    confirmDeleteDialog.setNegativeButton(R.string.no, null);
                    confirmDeleteDialog.show();
                } else {
                    // account has no private data - dont make a fuzz about it and just delete it
                    walletManager.deleteAccount(accountToDelete.getId());
                    _storage.deleteAccountMetadata(accountToDelete.getId());
                    //Check if this SingleAddress account is related with ColuAccount
                    WalletAccount linkedColuAccount = Utils.getLinkedAccount(accountToDelete, walletManager.getAccounts());
                    if (linkedColuAccount != null) {
                        walletManager.deleteAccount(linkedColuAccount.getId());
                        _storage.deleteAccountMetadata(linkedColuAccount.getId());
                    }
                    finishCurrentActionMode();
                    eventBus.post(new AccountChanged(accountToDelete.getId()));
                    _toaster.toast(R.string.account_deleted, false);
                }
            }

            private Value getBalance(WalletAccount account) {
                if (account.isArchived()) {
                    return null;
                } else {
                    return account.getAccountBalance().confirmed;
                }
            }

            private Long getPotentialBalance(WalletAccount account) {
                if (account.isArchived()) {
                    return null;
                } else {
                    return account.getAccountBalance().getSpendable().value;
                }
            }
        });
        deleteDialog.setNegativeButton(R.string.no, null).show();
    }

    @NonNull
    private String createDeleteDialogText(WalletAccount accountToDelete, WalletAccount linkedAccount) {
        String accountName = _mbwManager.getMetadataStorage().getLabelByAccount(accountToDelete.getId());
        String dialogText;

        if (accountToDelete.isActive()) {
            dialogText = getActiveAccountDeleteText(accountToDelete, linkedAccount, accountName);
        } else {
            dialogText = getArchivedAccountDeleteText(linkedAccount, accountName);
        }
        return dialogText;
    }

    @NonNull
    private String getArchivedAccountDeleteText(WalletAccount linkedAccount, String accountName) {
        String dialogText;
        if (linkedAccount != null && linkedAccount.isVisible()) {
            String linkedAccountName =_mbwManager.getMetadataStorage().getLabelByAccount(linkedAccount.getId());
            dialogText = getString(R.string.delete_archived_account_message, accountName, linkedAccountName);
        } else {
            dialogText = getString(R.string.delete_archived_account_message_s, accountName);
        }
        return dialogText;
    }

    @NonNull
    private String getActiveAccountDeleteText(WalletAccount accountToDelete, WalletAccount linkedAccount, String accountName) {
        String dialogText;
        Balance balance = checkNotNull(accountToDelete.getAccountBalance());
        String valueString = getBalanceString(balance);

        if (linkedAccount != null && linkedAccount.isVisible()) {
            Balance linkedBalance = linkedAccount.getAccountBalance();
            String linkedValueString = getBalanceString(linkedBalance);
            String linkedAccountName =_mbwManager.getMetadataStorage().getLabelByAccount(linkedAccount.getId());
            dialogText = getString(R.string.delete_account_message, accountName, valueString,
                    linkedAccountName, linkedValueString) + "\n" +
                    getString(R.string.both_rmc_will_deleted, accountName, linkedAccountName);
        } else {
            dialogText = getString(R.string.delete_account_message_s, accountName, valueString);
        }
        return dialogText;
    }

    private String getBalanceString(Balance balance) {
        return ValueExtensionsKt.toStringWithUnit(balance.confirmed, _mbwManager.getDenomination());
    }

    /**
     * If account is colu we are asking for linked BTC. Else we are searching if any colu attached.
     */
    private WalletAccount getLinkedAccount(WalletAccount account) {
        return Utils.getLinkedAccount(account, walletManager.getAccounts());
    }

    private void finishCurrentActionMode() {
        if (currentActionMode != null) {
            currentActionMode.finish();
        }
    }

    private void update() {
        if (!isAdded()) {
            return;
        }

        if (_mbwManager.isKeyManagementLocked()) {
            // Key management is locked
            rvRecords.setVisibility(View.GONE);
            llLocked.setVisibility(View.VISIBLE);
        } else {
            // Make all the key management functionality available to experts
            rvRecords.setVisibility(View.VISIBLE);
            llLocked.setVisibility(View.GONE);
        }
        eventBus.post(new AccountListChanged());
    }

    private ActionMode currentActionMode;

    private AccountListAdapter.ItemClickListener recordAddressClickListener = new AccountListAdapter.ItemClickListener() {
        @Override
        public void onItemClick(@NonNull WalletAccount account) {
            // Check whether a new account was selected
            if (!_mbwManager.getSelectedAccount().equals(account) && account.isActive()) {
                _mbwManager.setSelectedAccount(account.getId());
            }
            toastSelectedAccountChanged(account);
            updateIncludingMenus();
        }
    };

    private void updateIncludingMenus() {
        WalletAccount account = requireFocusedAccount();
        boolean isBch = account instanceof SingleAddressBCHAccount
                || account instanceof Bip44BCHAccount;

        final List<Integer> menus = Lists.newArrayList();
        if(!(account instanceof ColuAccount)
                && !Utils.checkIsLinked(account, getColuAccounts(walletManager))) {
            menus.add(R.menu.record_options_menu);
        }

        if (account instanceof SingleAddressAccount || account.isDerivedFromInternalMasterseed()) {
            menus.add(R.menu.record_options_menu_backup);
        }

        if (account instanceof SingleAddressAccount) {
            menus.add(R.menu.record_options_menu_backup_verify);
        }

        if(account instanceof ColuAccount) {
            //TODO: distinguish between ColuAccount in single address mode and HD mode
            menus.add(R.menu.record_options_menu_backup);
            menus.add(R.menu.record_options_menu_backup_verify);
        }

        if (!account.isDerivedFromInternalMasterseed() && !isBch) {
            menus.add(R.menu.record_options_menu_delete);
        }

        if (account.isActive() && account.canSpend() && !(account instanceof HDPubOnlyAccount)
                && !isBch && !(account instanceof HDAccountExternalSignature)) {
            menus.add(R.menu.record_options_menu_sign);
        }

        if (account.isActive() && !isBch) {
            menus.add(R.menu.record_options_menu_active);
        }

        if (account.isActive() && !(account instanceof CoinapultAccount) && !isBch) {
            menus.add(R.menu.record_options_menu_outputs);
        }

        if (account instanceof CoinapultAccount) {
            menus.add(R.menu.record_options_menu_set_coinapult_mail);
        }

        if (!(account instanceof Bip44BCHAccount)
                && !(account instanceof SingleAddressBCHAccount)
                && account.isArchived()) {
            menus.add(R.menu.record_options_menu_archive);
        }

        if (account.isActive() && account instanceof ExportableAccount && !isBch) {
            menus.add(R.menu.record_options_menu_export);
        }

        if (account.isActive() && account instanceof HDAccount && !(account instanceof HDPubOnlyAccount)
                && getActiveMasterseedHDAccounts(walletManager).size() > 1 && !isBch) {
            final HDAccount HDAccount = (HDAccount) account;
            BitcoinHDModule bitcoinHDModule = (BitcoinHDModule) walletManager.getModuleById(BitcoinHDModule.ID);
            if (!HDAccount.hasHadActivity() && HDAccount.getAccountIndex() == bitcoinHDModule.getCurrentBip44Index()) {
                //only allow to remove unused HD acounts from the view
                menus.add(R.menu.record_options_menu_hide_unused);
            }
        }

        if (account.getId().equals(_mbwManager.getLocalTraderManager().getLocalTraderAccountId())) {
            menus.add(R.menu.record_options_menu_detach);
        }

        AppCompatActivity parent = (AppCompatActivity) requireActivity();

        Callback actionMode = new Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                for (Integer res : menus) {
                    actionMode.getMenuInflater().inflate(res, menu);
                }
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                // If we are synchronizing, show "Synchronizing, please wait..." to avoid blocking behavior
                if (_mbwManager.getWalletManager(false).getState() == State.SYNCHRONIZING) {
                    _toaster.toast(R.string.synchronizing_please_wait, false);
                    return true;
                }
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.miActivate:
                        activateSelected();
                        return true;
                    case R.id.miSetLabel:
                        setLabelOnAccount(accountListAdapter.getFocusedAccount(), "", true);
                        return true;
                    case R.id.miDeleteRecord:
                        deleteSelected();
                        return true;
                    case R.id.miArchive:
                        archiveSelected();
                        return true;
                    case R.id.miHideUnusedAccount:
                        hideSelected();
                        return true;
                    case R.id.miExport:
                        exportSelectedPrivateKey();
                        return true;
                    case R.id.miSignMessage:
                        signMessage();
                        return true;
                    case R.id.miDetach:
                        detachFromLocalTrader();
                        return true;
                    case R.id.miShowOutputs:
                        showOutputs();
                        return true;
                    case R.id.miMakeBackup:
                        makeBackup();
                        return true;
                    case R.id.miSingleKeyBackupVerify:
                        verifySingleKeyBackup();
                        return true;
                    case R.id.miRescan:
                        rescan();
                        return true;
                    case R.id.miSetMail:
                        setCoinapultMail();
                        return true;
                    case R.id.miVerifyMail:
                        verifyCoinapultMail();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                currentActionMode = null;
                // Loose focus
                if (accountListAdapter.getFocusedAccount() != null) {
                    accountListAdapter.setFocusedAccountId(null);
                }
            }
        };
        currentActionMode = parent.startSupportActionMode(actionMode);
        // Late set the focused record. We have to do this after
        // startSupportActionMode above, as it calls onDestroyActionMode when
        // starting for some reason, and this would clear the focus and force
        // an update.
        accountListAdapter.setFocusedAccountId(account.getId());
    }

    //todo: maybe move it to another class along with the other coinaspult mail stuff? would require passing the context for dialog boxes though.
    private void setCoinapultMail() {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setTitle(getString(R.string.coinapult_mail_question));
        View diaView = requireActivity().getLayoutInflater().inflate(R.layout.ext_coinapult_mail, null);
        final EditText mailField = diaView.findViewById(R.id.mail);
        mailField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        String email = _mbwManager.getMetadataStorage().getCoinapultMail();
        if (!email.isEmpty()) {
            mailField.setText(email);
        }
        b.setView(diaView);
        b.setPositiveButton(getString(R.string.button_done), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mailText = mailField.getText().toString();
                if (Utils.isValidEmailAddress(mailText)) {
                    if (!mailText.isEmpty()) {
                        _progress.setCancelable(false);
                        _progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        _progress.setMessage(getString(R.string.coinapult_setting_email));
                        _progress.show();
                        _mbwManager.getMetadataStorage().setCoinapultMail(mailText);
                        new SetCoinapultMailAsyncTask(mailText).execute();
                    }
                    dialog.dismiss();
                } else {
                    new Toaster(AccountsFragment.this).toast("Email address not valid", false);
                }
            }
        });
        b.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = b.create();
        dialog.show();
    }

    private void verifyCoinapultMail() {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setTitle(getString(R.string.coinapult_mail_verification));
        final String email = _mbwManager.getMetadataStorage().getCoinapultMail();
        View diaView = requireActivity().getLayoutInflater().inflate(R.layout.ext_coinapult_mail_verification, null);
        final EditText verificationTextField = diaView.findViewById(R.id.mailVerification);

        // check if there is a probable verification link in the clipboard and if so, pre-fill the textbox
        String clipboardString = Utils.getClipboardString(getActivity());
        if (!Strings.isNullOrEmpty(clipboardString) && clipboardString.contains("coinapult.com")) {
            verificationTextField.setText(clipboardString);
        }

        b.setView(diaView);
        b.setPositiveButton(getString(R.string.button_done), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String verification = verificationTextField.getText().toString();
                _progress.setCancelable(false);
                _progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                _progress.setMessage(getString(R.string.coinapult_verifying_email));
                _progress.show();
                new VerifyCoinapultMailAsyncTask(verification, email).execute();
                dialog.dismiss();
            }
        });
        b.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = b.create();
        dialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    private class SetCoinapultMailAsyncTask extends AsyncTask<Void, Integer, Boolean> {
        private String mail;

        public SetCoinapultMailAsyncTask(@NonNull String mail) {
            this.mail = mail;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return ((CoinapultModule) walletManager.getModuleById(CoinapultModule.ID)).setMail(mail);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            _progress.dismiss();
            if (success) {
                Utils.showSimpleMessageDialog(getActivity(), R.string.coinapult_set_mail_please_verify);
            } else {
                Utils.showSimpleMessageDialog(getActivity(), R.string.coinapult_set_mail_failed);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class VerifyCoinapultMailAsyncTask extends AsyncTask<Void, Integer, Boolean> {
        private String link;
        private String email;

        public VerifyCoinapultMailAsyncTask(String link, String email) {
            this.link = link;
            this.email = email;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return  ((CoinapultModule) walletManager.getModuleById(CoinapultModule.ID)).verifyMail(link, email);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            _progress.dismiss();
            if (success) {
                Utils.showSimpleMessageDialog(getActivity(), R.string.coinapult_verify_mail_success);
            } else {
                Utils.showSimpleMessageDialog(getActivity(), R.string.coinapult_verify_mail_error);
            }
        }
    }

    private void verifySingleKeyBackup() {
        if (!isAdded()) {
            return;
        }
        WalletAccount account = requireFocusedAccount();
        if (account instanceof SingleAddressAccount || account instanceof ColuAccount) {
            //start legacy backup verification
            VerifyBackupActivity.callMe(getActivity());
        }
    }

    private void makeBackup() {
        if (!isAdded()) {
            return;
        }
        WalletAccount account = requireFocusedAccount();
        if(account instanceof ColuAccount) {
            //ColuAccount class can be single or HD
            //TODO: test if account is single address or HD and do wordlist backup instead
            //start legacy backup if a single key or watch only was selected
            Utils.pinProtectedBackup(getActivity());
        } else {
            if (account.isDerivedFromInternalMasterseed()) {
                //start wordlist backup if a HD account or derived account was selected
                Utils.pinProtectedWordlistBackup(getActivity());
            } else if (account instanceof SingleAddressAccount) {
                //start legacy backup if a single key or watch only was selected
                Utils.pinProtectedBackup(getActivity());
            }
        }
    }

    private void showOutputs() {
        WalletAccount account = requireFocusedAccount();
        Intent intent = new Intent(getActivity(), UnspentOutputsActivity.class)
                .putExtra("account", account.getId());
        startActivity(intent);
    }

    private void signMessage() {
        if (!isAdded()) {
            return;
        }
        runPinProtected(new Runnable() {
            @Override
            public void run() {
                WalletAccount account = accountListAdapter.getFocusedAccount();
                MessageSigningActivity.callMe(getActivity(), account);
            }
        });
    }

    /**
     * Show a message to the user explaining what it means to select a different
     * address.
     */
    private void toastSelectedAccountChanged(WalletAccount account) {
        if (account.isArchived()) {
            _toaster.toast(getString(R.string.selected_archived_warning), true);
        } else if (account instanceof HDAccount) {
            _toaster.toast(getString(R.string.selected_hd_info), true);
        } else if (account instanceof SingleAddressAccount) {
            _toaster.toast(getString(R.string.selected_single_info), true);
        } else if(account instanceof ColuAccount) {
            _toaster.toast(getString(R.string.selected_colu_info
                    , _mbwManager.getMetadataStorage().getLabelByAccount(account.getId())), true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!isAdded()) {
            return true;
        }
        if (item.getItemId() == R.id.miAddRecord) {
            AddAccountActivity.callMe(this, ADD_RECORD_RESULT_CODE);
            return true;
        } else if (item.getItemId() == R.id.miLockKeys) {
            lock();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setLabelOnAccount(final WalletAccount account, final String defaultName, boolean askForPin) {
        if (account == null || !isAdded()) {
            return;
        }
        if (askForPin) {
            runPinProtected(new Runnable() {
                @Override
                public void run() {
                    EnterAddressLabelUtil.enterAccountLabel(requireActivity(), account.getId(), defaultName, _storage);
                }
            });
        } else {
            EnterAddressLabelUtil.enterAccountLabel(requireActivity(), account.getId(), defaultName, _storage);
        }
    }

    private void deleteSelected() {
        if (!isAdded()) {
            return;
        }
        final WalletAccount account = requireFocusedAccount();
        if (account.isActive() && accountProtected(account)) {
            _toaster.toast(R.string.keep_one_active, false);
            return;
        }
        runPinProtected(new Runnable() {
            @Override
            public void run() {
                deleteAccount(account);
            }
        });
    }

    private void rescan() {
        if (!isAdded()) {
            return;
        }
        requireFocusedAccount().dropCachedData();
        _mbwManager.getWalletManager(false).startSynchronization(SyncMode.FULL_SYNC_CURRENT_ACCOUNT_FORCED);
    }

    private void exportSelectedPrivateKey() {
        if (!isAdded()) {
            return;
        }
        runPinProtected(new Runnable() {
            @Override
            public void run() {
                Utils.exportSelectedAccount(getActivity());
            }
        });
    }

    private void detachFromLocalTrader() {
        if (!isAdded()) {
            return;
        }
        runPinProtected(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.lt_detaching_title)
                        .setMessage(getString(R.string.lt_detaching_question))
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                _mbwManager.getLocalTraderManager().unsetLocalTraderAccount();
                                _toaster.toast(R.string.lt_detached, false);
                                update();
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            }
        });
    }

    private void activateSelected() {
        if (!isAdded()) {
            return;
        }
        runPinProtected(new Runnable() {
            @Override
            public void run() {
                activate(requireFocusedAccount());
            }
        });
    }

    private void activate(WalletAccount account) {
        account.activateAccount();
        WalletAccount linkedAccount = Utils.getLinkedAccount(account, walletManager.getAccounts());
        if (linkedAccount != null) {
            linkedAccount.activateAccount();
        }
        //setselected also broadcasts AccountChanged event
        _mbwManager.setSelectedAccount(account.getId());
        updateIncludingMenus();
        _toaster.toast(R.string.activated, false);
        _mbwManager.getWalletManager(false).startSynchronization();
    }

    private void archiveSelected() {
        if (!isAdded()) {
            return;
        }
        final WalletAccount account = requireFocusedAccount();
        if (accountProtected(account)) {
            //this is the last active hd account, we dont allow archiving it
            _toaster.toast(R.string.keep_one_active, false);
            return;
        }
        if (account instanceof CoinapultAccount) {
            runPinProtected(new Runnable() {
                @Override
                public void run() {
                    archive(account);
                }
            });
            return;
        } else if (account instanceof HDAccount) {
            HDAccount hdAccount = (HDAccount) account;
            if (!hdAccount.hasHadActivity() && hdAccount.isDerivedFromInternalMasterseed()) {
                // this hdAccount is unused, we don't allow archiving it
                _toaster.toast(R.string.dont_allow_archiving_unused_notification, false);
                return;
            }
        }
        runPinProtected(new Runnable() {
            @Override
            public void run() {
                archive(account);
            }
        });
    }

    /**
     * Account is protected if after removal no masterseed accounts would stay active, so it would not be possible to select an account
     */
    private boolean accountProtected(WalletAccount toRemove) {
        // If the account is not derived from master seed, we can remove it
        if (!toRemove.isDerivedFromInternalMasterseed()) {
            return false;
        }
        List<WalletAccount<?>> accountsList = getActiveMasterseedAccounts(_mbwManager.getWalletManager(false));

        // If we have more than one master-seed derived account, we can remove it
        return accountsList.size() <= 1;
    }

    private void hideSelected() {
        if (!isAdded()) {
            return;
        }
        final WalletAccount account = requireFocusedAccount();
        if (accountProtected(account)) {
            //this is the last active account, we dont allow hiding it
            _toaster.toast(R.string.keep_one_active, false);
            return;
        }
        if (account instanceof HDAccount) {
            final HDAccount hdAccount = (HDAccount) account;
            if (hdAccount.hasHadActivity() && hdAccount.isDerivedFromInternalMasterseed()) {
                // this hdAccount is used, we don't allow hiding it
                _toaster.toast(R.string.dont_allow_hiding_used_notification, false);
                return;
            }

            runPinProtected(new Runnable() {
                @Override
                public void run() {
                    _mbwManager.getWalletManager(false).deleteAccount(hdAccount.getId());
                    // in case user had labeled the account, delete the stored name
                    _storage.deleteAccountMetadata(hdAccount.getId());
                    eventBus.post(new AccountChanged(hdAccount.getId()));
                    _mbwManager.setSelectedAccount(_mbwManager.getWalletManager(false).getActiveAccounts().get(0).getId());
                    //we dont want to show the context menu for the automatically selected account
                    accountListAdapter.setFocusedAccountId(null);
                    finishCurrentActionMode();
                }
            });
        }
    }

    private void archive(final WalletAccount account) {
        final WalletAccount linkedAccount = getLinkedAccount(account);
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.archiving_account_title)
                .setMessage(Html.fromHtml(createArchiveDialogText(account,linkedAccount)))
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        account.archiveAccount();
                        WalletAccount linkedAccount = Utils.getLinkedAccount(account, walletManager.getAccounts());
                        if (linkedAccount != null) {
                            linkedAccount.archiveAccount();
                        }
                        _mbwManager.setSelectedAccount(_mbwManager.getWalletManager(false).getActiveAccounts().get(0).getId());
                        eventBus.post(new AccountChanged(account.getId()));
                        if (linkedAccount != null) {
                            eventBus.post(new AccountChanged(linkedAccount.getId()));
                        }
                        updateIncludingMenus();
                        _toaster.toast(R.string.archived, false);
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @NonNull
    private String createArchiveDialogText(WalletAccount account, WalletAccount linkedAccount) {
        String accountName = _mbwManager.getMetadataStorage().getLabelByAccount(account.getId());
        return getAccountArchiveText(account, linkedAccount, accountName);
    }

    @NonNull
    private String getAccountArchiveText(WalletAccount account, WalletAccount linkedAccount, String accountName) {
        String dialogText;
        Balance balance = checkNotNull(account.getAccountBalance());
        String valueString = getBalanceString(balance);

        if (linkedAccount != null && linkedAccount.isVisible()) {
            Balance linkedBalance = linkedAccount.getAccountBalance();
            String linkedValueString = getBalanceString(linkedBalance);
            String linkedAccountName =_mbwManager.getMetadataStorage().getLabelByAccount(linkedAccount.getId());
            dialogText = getString(R.string.question_archive_account_s, accountName, valueString,
                    linkedAccountName, linkedValueString);
        } else {
            dialogText = getString(R.string.question_archive_account, accountName, valueString);
        }
        return dialogText;
    }

    private void lock() {
        _mbwManager.setKeyManagementLocked(true);
        update();
        if (isAdded()) {
            requireActivity().invalidateOptionsMenu();
        }
    }

    private void runPinProtected(final Runnable runnable) {
        _mbwManager.runPinProtectedFunction(requireActivity(), new Runnable() {
            @Override
            public void run() {
                if (!isAdded()) {
                    return;
                }
                runnable.run();
            }
        });
    }

    OnClickListener unlockClickedListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            _mbwManager.runPinProtectedFunction(getActivity(), new Runnable() {
                @Override
                public void run() {
                    _mbwManager.setKeyManagementLocked(false);
                    update();
                    if (isAdded()) {
                        requireActivity().invalidateOptionsMenu();
                    }
                }
            });
        }
    };

    @NonNull
    private WalletAccount requireFocusedAccount() {
        return checkNotNull(accountListAdapter.getFocusedAccount());
    }

    @Subscribe
    public void addressChanged(ReceivingAddressChanged event) {
        update();
    }

    @Subscribe
    public void balanceChanged(BalanceChanged event) {
        update();
    }

    @Subscribe
    public void syncStarted(SyncStarted event) {
        update();
    }

    @Subscribe
    public void syncStopped(SyncStopped event) {
        update();
    }

    @Subscribe
    public void accountChanged(AccountChanged event) {
        update();
    }

    @Subscribe
    public void syncProgressUpdated(SyncProgressUpdated event) {
        update();
    }

    @Subscribe
    public void exchangeSourceChange(ExchangeSourceChanged event) {
        accountListAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void selectedCurrencyChanged(SelectedCurrencyChanged event) {
        accountListAdapter.notifyDataSetChanged();
    }

    private LocalTraderEventSubscriber ltSubscriber = new LocalTraderEventSubscriber(new Handler()) {
        @Override
        public void onLtError(int errorCode) { }

        @Override
        public void onLtAccountDeleted(DeleteTrader request) {
            accountListAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLtTraderCreated(CreateTrader request) {
            accountListAdapter.notifyDataSetChanged();
        }
    };
}
