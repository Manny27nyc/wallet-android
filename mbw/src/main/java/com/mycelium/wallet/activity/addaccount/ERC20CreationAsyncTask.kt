package com.mycelium.wallet.activity.addaccount

import android.os.AsyncTask
import com.mycelium.wallet.MbwManager
import com.mycelium.wallet.event.AccountChanged
import com.mycelium.wallet.event.AccountCreated
import com.mycelium.wallet.persistence.MetadataStorage
import com.mycelium.wapi.wallet.erc20.ERC20Config
import com.mycelium.wapi.wallet.erc20.coins.ERC20Token
import com.mycelium.wapi.wallet.eth.EthAccount
import java.util.*


class ERC20CreationAsyncTask(val mbwManager: MbwManager,
                             val tokens: List<ERC20Token>,
                             val ethAccount: EthAccount,
                             val startListener: () -> Unit,
                             val endListener: (List<UUID>) -> Unit) : AsyncTask<Void, Int, List<UUID>>() {

    override fun onPreExecute() {
        super.onPreExecute()
        if (tokens.isEmpty()) {
            cancel(false)
        } else {
            startListener()
        }
    }

    override fun doInBackground(vararg params: Void?): List<UUID> =
            tokens.flatMap { token ->
                mbwManager.getWalletManager(false)
                        .createAccounts(ERC20Config(token, ethAccount))
                        .apply {
                            ethAccount.addEnabledToken(token.name)
                        }
            }

    override fun onPostExecute(accountIds: List<UUID>) {
        accountIds.forEach { accountId ->
            mbwManager.metadataStorage.setOtherAccountBackupState(accountId, MetadataStorage.BackupState.IGNORED)
            MbwManager.getEventBus().post(AccountCreated(accountId))
            MbwManager.getEventBus().post(AccountChanged(accountId))
        }
        endListener(accountIds)
    }
}