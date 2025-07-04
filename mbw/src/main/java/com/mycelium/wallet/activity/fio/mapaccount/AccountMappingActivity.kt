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
package com.mycelium.wallet.activity.fio.mapaccount

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.mycelium.wallet.MbwManager
import com.mycelium.wallet.R
import com.mycelium.wallet.activity.fio.mapaccount.viewmodel.FIOMapPubAddressViewModel
import com.mycelium.wapi.wallet.WalletAccount
import com.mycelium.wapi.wallet.fio.FioAccount
import com.mycelium.wapi.wallet.fio.FioModule
import java.util.*
import androidx.activity.viewModels


class AccountMappingActivity : AppCompatActivity(R.layout.activity_account_mapping) {
    companion object {
        fun startForMapping(activity: Activity, account: WalletAccount<*>, requestCode:Int) {
            activity.startActivityForResult(Intent(activity, AccountMappingActivity::class.java)
                    .putExtra("mode", Mode.NEED_FIO_NAME_MAPPING)
                    .putExtra("extraAccountId", account.id), requestCode)
        }
    }

    private val viewModel: FIOMapPubAddressViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.run {
            setHomeAsUpIndicator(R.drawable.ic_back_arrow)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
        }
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?)
                ?.navController?.setGraph(R.navigation.fio_map_account, intent?.extras)
        val walletManager = MbwManager.getInstance(this.application).getWalletManager(false)
        if (intent?.extras?.containsKey("accountId") == true) {
            val accountId = intent.getSerializableExtra("accountId") as UUID
            val account = walletManager.getAccount(accountId)
            if (account is FioAccount) {
                viewModel.accountList.value = listOf(account)
            } else {
                val fioModule = walletManager.getModuleById(FioModule.ID) as FioModule
                viewModel.accountList.value = fioModule.getFIONames(account!!)
                        .mapNotNull { fioModule.getFioAccountByFioName(it.name) }
                        .toSet()
                        .map { walletManager.getAccount(it) as FioAccount }
            }
        }
        if (intent?.extras?.containsKey("mode") == true) {
            viewModel.mode.value = intent?.extras?.getSerializable("mode") as Mode
            viewModel.extraAccount.value = walletManager.getAccount(intent?.extras?.getSerializable("extraAccountId") as UUID)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when (item.itemId) {
                android.R.id.home -> {
                    onBackPressed()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
}
