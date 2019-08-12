package com.mycelium.wallet.activity.receive

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Transformations
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.mycelium.wallet.MbwManager
import com.mycelium.wallet.R
import com.mycelium.wallet.Utils
import com.mycelium.wallet.activity.GetAmountActivity
import com.mycelium.wallet.activity.util.toStringWithUnit
import com.mycelium.wapi.wallet.WalletAccount
import com.mycelium.wapi.wallet.coins.Value

abstract class ReceiveCoinsViewModel(val context: Application) : AndroidViewModel(context) {
    protected val mbwManager = MbwManager.getInstance(context)!!
    protected lateinit var model: ReceiveCoinsModel
    protected lateinit var account: WalletAccount<*>
    var hasPrivateKey: Boolean = false

    open fun init(account: WalletAccount<*>, hasPrivateKey: Boolean, showIncomingUtxo: Boolean = false) {
        if (::model.isInitialized) {
            throw IllegalStateException("This method should be called only once.")
        }
        this.account = account
        this.hasPrivateKey = hasPrivateKey
    }

    open fun loadInstance(savedInstanceState: Bundle) {
        model.loadInstance(savedInstanceState)
    }

    open fun saveInstance(outState: Bundle) {
        model.saveInstance(outState)
    }

    open fun getHint() = context.getString(R.string.amount_hint_denomination,
            mbwManager.denomination.getUnicodeString(account.coinType.symbol))

    abstract fun getFormattedValue(sum: Value): String

    abstract fun getTitle(): String

    abstract fun getCurrencyName(): String

    override fun onCleared() = model.onCleared()

    fun isInitialized() = ::model.isInitialized

    fun isReceivingAmountWrong() = model.receivingAmountWrong

    fun getCurrentlyReceivingFormatted() = Transformations.map(model.receivingAmount) {
        getFormattedValue(it ?: Value.zeroValue(mbwManager.selectedAccount.coinType))
    }

    fun getCurrentlyReceivingAmount() = model.receivingAmount

    fun getRequestedAmount() = model.amount

    fun getReceivingAddress() = model.receivingAddress

    fun getRequestedAmountFormatted() = Transformations.map(model.amount) {
        if (!Value.isNullOrZero(it)) {
            it?.toStringWithUnit(mbwManager.denomination)
        } else {
            ""
        }
    }

    fun getRequestedAmountAlternative() = model.alternativeAmountData

    fun getRequestedAmountAlternativeFormatted() = Transformations.map(model.alternativeAmountData) {
        if (!Value.isNullOrZero(it)) {
            "~ " + it?.toStringWithUnit(mbwManager.denomination)
        } else {
            ""
        }
    }

    fun isNfcAvailable() = model.nfc?.isNdefPushEnabled == true

    fun getNfc() = model.nfc

    fun getPaymentUri() = model.getPaymentUri()

    fun shareRequest() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        if (Value.isNullOrZero(model.amount.value)) {
            intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.bitcoin_address_title))
            intent.putExtra(Intent.EXTRA_TEXT, model.receivingAddress.value.toString())
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_bitcoin_address))
                    .addFlags(FLAG_ACTIVITY_NEW_TASK))
        } else {
            intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.payment_request))
            intent.putExtra(Intent.EXTRA_TEXT, getPaymentUri())
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_payment_request))
                    .addFlags(FLAG_ACTIVITY_NEW_TASK))
        }
    }

    fun copyToClipboard() {
        val text = if (Value.isNullOrZero(model.amount.value)) {
            model.receivingAddress.value.toString()
        } else {
            getPaymentUri()
        }
        Utils.setClipboardString(text, context)
        Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
    }

    fun setAmount(amount: Value) {
        if(amount.type == account.coinType) {
            model.setAmount(amount)
            val value = mbwManager.exchangeRateManager.get(amount, mbwManager.fiatCurrency)
                    ?: Value.zeroValue(account.coinType)
            model.setAlternativeAmount(value)
        } else {
            model.setAmount(mbwManager.exchangeRateManager.get(amount, account.coinType))
            model.setAlternativeAmount(amount)
        }
    }

    fun onEnterClick(activity: AppCompatActivity) {
        val amount = model.amount
        if (Value.isNullOrZero(amount.value)) {
            GetAmountActivity.callMeToReceive(activity, Value.zeroValue(mbwManager.selectedAccount.coinType),
                    GET_AMOUNT_RESULT_CODE, model.account.coinType)
        } else {
            // call the amount activity with the exact amount, so that the user sees the same amount he had entered
            // it in non-BTC
            GetAmountActivity.callMeToReceive(activity, amount.value,
                    GET_AMOUNT_RESULT_CODE, model.account.coinType)
        }
    }

    companion object {
        const val GET_AMOUNT_RESULT_CODE = 1
    }
}