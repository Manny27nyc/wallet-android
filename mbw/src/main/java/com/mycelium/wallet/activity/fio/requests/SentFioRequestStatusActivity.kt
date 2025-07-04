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
package com.mycelium.wallet.activity.fio.requests

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.mycelium.net.ServerEndpointType
import com.mycelium.wallet.MbwManager
import com.mycelium.wallet.R
import com.mycelium.wallet.Utils
import com.mycelium.wallet.activity.util.toStringWithUnit
import com.mycelium.wallet.databinding.FioSentRequestStatusActivityBinding
import com.mycelium.wapi.wallet.Util
import com.mycelium.wapi.wallet.Util.convertToDate
import com.mycelium.wapi.wallet.coins.COINS
import com.mycelium.wapi.wallet.coins.Value
import com.mycelium.wapi.wallet.fio.FioRequestStatus
import fiofoundation.io.fiosdk.models.fionetworkprovider.FIORequestContent
import fiofoundation.io.fiosdk.models.fionetworkprovider.SentFIORequestContent
import java.text.DateFormat
import java.util.*

class SentFioRequestStatusActivity : AppCompatActivity() {
    private var fioRequestContent: SentFIORequestContent? = null
    private lateinit var mbwManager: MbwManager
    lateinit var binding: FioSentRequestStatusActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(FioSentRequestStatusActivityBinding.inflate(layoutInflater).apply {
            binding = this
        }.root)
        supportActionBar?.run {
            title = if (intent.getStringExtra(CONTENT) != null) {
                setHomeAsUpIndicator(R.drawable.ic_back_arrow)
                setDisplayHomeAsUpEnabled(true)
                "My FIO Request"
            } else {
                "FIO Request Sent"
            }
        }
        mbwManager = MbwManager.getInstance(this)
        if (intent.getStringExtra(CONTENT) != null) {
            fioRequestContent = Gson().fromJson(intent.getStringExtra(CONTENT), SentFIORequestContent::class.java)
        }

        setTitles()
        setStatus()
        setAmount()

        binding.tvFrom.text = fioRequestContent?.payeeFioAddress ?: intent.getStringExtra(ApproveFioRequestActivity.FROM)
        binding.tvTo.text = fioRequestContent?.payerFioAddress ?: intent.getStringExtra(ApproveFioRequestActivity.TO)
        val memo = if (fioRequestContent != null) {
            fioRequestContent!!.deserializedContent!!.memo ?: ""
        } else {
            intent.getStringExtra(ApproveFioRequestActivity.MEMO)
        }
        binding.tvMemo.text = memo
        binding.llMemo.visibility = if (memo.isNullOrEmpty()) View.GONE else View.VISIBLE

        binding.tvDate.text = getDateString(if (fioRequestContent != null) {
            convertToDate(fioRequestContent!!.timeStamp)
        } else {
            Date()
        })
        binding.btNextButton.setOnClickListener { finish() }
        binding.tvRequestDetailsLink.visibility = if (fioRequestContent != null) View.GONE else View.VISIBLE
        binding.tvRequestDetailsLink.setOnClickListener {
            val txHash = this.intent.getStringExtra(ApproveFioRequestActivity.TXID)
            val blockExplorer = mbwManager._blockExplorerManager.getBEMByCurrency(Utils.getFIOCoinType().name)!!.blockExplorer
            val url = blockExplorer.getUrl(txHash, mbwManager.torMode == ServerEndpointType.Types.ONLY_TOR)

            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
            Toast.makeText(this, R.string.redirecting_to_block_explorer, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setTitles() {
        if (intent.getStringExtra(CONTENT) != null) {
            binding.tvFromTitle.text = "From:"
            binding.tvToTitle.text = "To:"
        } else {
            binding.tvFromTitle.text = "Request from:"
            binding.tvToTitle.text = "Request sent to:"
        }
    }

    private fun setAmount() {
        if (fioRequestContent != null) {
            val requestedCurrency = (COINS.values + mbwManager.getWalletManager(false).getAssetTypes()).firstOrNull {
                it.symbol.equals(fioRequestContent!!.deserializedContent!!.tokenCode, true)
                        && if(mbwManager.network.isTestnet) it.name.contains("test", true) else true
            }
            if (requestedCurrency != null) {
                val amount = Value.valueOf(requestedCurrency, Util.strToBigInteger(requestedCurrency,
                        fioRequestContent!!.deserializedContent!!.amount))
                binding.tvAmount.text = amount.toStringWithUnit()
                mbwManager.exchangeRateManager.get(amount, mbwManager.getFiatCurrency(requestedCurrency))
                        ?.toStringWithUnit()?.let {
                            binding.tvConvertedAmount.text = " ~ $it"
                        }
            } else {
                binding.tvAmount.text = "${fioRequestContent!!.deserializedContent!!.amount} ${fioRequestContent!!.deserializedContent!!.tokenCode}"
            }
        } else {
            val amount = (intent.getSerializableExtra(ApproveFioRequestActivity.AMOUNT) as Value)
            binding.tvAmount.text = amount.toStringWithUnit()
            val fiatCurrency = mbwManager.getFiatCurrency(amount.type)
            val value = mbwManager.exchangeRateManager.get(amount, fiatCurrency) ?: Value.zeroValue(fiatCurrency)
            val convertedAmount = value.toStringWithUnit()
            binding.tvConvertedAmount.text = " ~ $convertedAmount"
        }
    }

    private fun setStatus() {
        val status = if (fioRequestContent != null) {
            FioRequestStatus.getStatus(fioRequestContent!!.status)
        } else {
            FioRequestStatus.REQUESTED
        }
        val color = when (status) {
            FioRequestStatus.SENT_TO_BLOCKCHAIN -> R.color.fio_green
            FioRequestStatus.REJECTED -> R.color.fio_red
            else -> R.color.fio_yellow
        }
        binding.tvStatus.setTextColor(ContextCompat.getColor(this, color))
        binding.tvStatus.text = when (status) {
            FioRequestStatus.REJECTED -> "Rejected"
            FioRequestStatus.REQUESTED -> "Not Paid"
            FioRequestStatus.SENT_TO_BLOCKCHAIN -> "Paid"
            FioRequestStatus.NONE -> "Not Paid"
        }
    }

    private fun getDateString(date: Date): String {
        val locale = resources.configuration.locale

        val dayFormat = DateFormat.getDateInstance(DateFormat.LONG, locale)
        val dateString = dayFormat.format(date)

        val hourFormat = DateFormat.getTimeInstance(DateFormat.LONG, locale)
        val timeString = hourFormat.format(date)

        return "$dateString $timeString"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when (item.itemId) {
                android.R.id.home -> {
                    onBackPressed()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    companion object {
        const val CONTENT = "content"
        fun start(activity: Activity, item: FIORequestContent) {
            with(Intent(activity, SentFioRequestStatusActivity::class.java)) {
                putExtra(CONTENT, item.toJson())
                activity.startActivity(this)
            }
        }

        fun start(activity: Activity, amount: Value, from: String, to: String, memo: String, txid: String) {
            with(Intent(activity, SentFioRequestStatusActivity::class.java)) {
                putExtra(ApproveFioRequestActivity.AMOUNT, amount)
                putExtra(ApproveFioRequestActivity.FROM, from)
                putExtra(ApproveFioRequestActivity.TO, to)
                putExtra(ApproveFioRequestActivity.MEMO, memo)
                putExtra(ApproveFioRequestActivity.TXID, txid)
                activity.startActivity(this)
            }
        }
    }
}