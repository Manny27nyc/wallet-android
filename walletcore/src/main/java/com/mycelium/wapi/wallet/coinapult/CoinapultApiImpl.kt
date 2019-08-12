package com.mycelium.wapi.wallet.coinapult

import com.coinapult.api.httpclient.AccountInfo
import com.coinapult.api.httpclient.CoinapultClient
import com.coinapult.api.httpclient.SearchMany
import com.coinapult.api.httpclient.Transaction
import com.google.api.client.http.HttpResponseException
import com.mrd.bitlib.model.Address
import com.mycelium.WapiLogger
import com.mycelium.wapi.wallet.GenericAddress
import com.mycelium.wapi.wallet.GenericTransactionSummary
import com.mycelium.wapi.wallet.btc.BtcAddress
import com.mycelium.wapi.wallet.coins.Balance
import com.mycelium.wapi.wallet.coins.Value
import java.io.IOException
import java.math.BigDecimal
import java.net.SocketTimeoutException
import java.security.NoSuchAlgorithmException


class CoinapultApiImpl(val client: CoinapultClient, val logger: WapiLogger) : CoinapultApi {
    data class Entry(override val key: Long, override val value: MutableList<AccountInfo.Balance>?)
        : Map.Entry<Long, MutableList<AccountInfo.Balance>?>

    var lastInfo: Entry = Entry(0, null)

    override fun setMail(mail: String): Boolean {
        try {
            val result = client.setMail(mail)
            return result.email != null && result.email == mail
        } catch (e: IOException) {
            return false
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }

    override fun verifyMail(link: String, email: String): Boolean {
        try {
            val result = client.verifyMail(link, email)
            if (!result.verified) {
                logger.logError("Coinapult email error: " + result.error)
            }
            return result.verified
        } catch (e: IOException) {
            return false
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }

    override fun getAddress(currency: Currency, currentAddress: GenericAddress?): GenericAddress? {
        var address: GenericAddress? = null
        try {
            if (currentAddress == null) {
                address = BtcAddress(currency, Address.fromString(client.bitcoinAddress.address))
            } else {
                val criteria = HashMap<String, String>(1)
                criteria["to"] = address.toString()
                val search = client.search(criteria)
                val alreadyUsed = search.containsKey("transaction_id")
                address = if (alreadyUsed) {
                    // get a new one
                    BtcAddress(currency, Address.fromString(client.bitcoinAddress.address))
                } else {
                    currentAddress
                }
                client.config(address.toString(), currency.name)
            }
        } catch (e: IOException) {

        }
        return address
    }

    override fun getBalance(currency: Currency): Balance? {
        try {
            val balance = lastInfo()
            if (balance != null) {
                val filters = balance.filter {
                    it.currency == currency.name
                }
                if (filters.isNotEmpty()) {
                    return Balance(Value.valueOf(currency
                            , filters[0].amount.multiply(BigDecimal.TEN.pow(currency.unitExponent)).toLong())
                            , Value.zeroValue(currency), Value.zeroValue(currency), Value.zeroValue(currency))
                }
            }
        } catch (e: CoinapultClient.CoinapultBackendException) {
            logger.logError("CoinapultApiImpl error while getting balance", e)
        } catch (e: SocketTimeoutException) {
            logger.logError("CoinapultApiImpl error while getting balance", e)
        } catch (e: HttpResponseException) {
            logger.logError("CoinapultApiImpl error while getting balance", e)
        }
        return null
    }

    private fun lastInfo(): MutableList<AccountInfo.Balance>? {
        val balance = if (System.currentTimeMillis() - lastInfo.key > 10000 || lastInfo.value == null)
            client.accountInfo().balances
        else
            lastInfo.value
        if (balance != null) {
            lastInfo = Entry(System.currentTimeMillis(), balance)
        }
        return balance
    }

    override fun getTransactions(currency: Currency): List<GenericTransactionSummary>? {
        return ArrayList<GenericTransactionSummary>()
    }

    private fun add(currency: Currency, batch: SearchMany.Json?, tmpResult: MutableList<GenericTransactionSummary>) {
    }

    override fun broadcast(amount: BigDecimal, currency: Currency, address: BtcAddress) {
        try {
            val send: Transaction.Json
            if (currency != Currency.BTC) {
                send = client.send(amount, currency.name, address.toString(), BigDecimal.ZERO, null, null, null)
            } else {
//                val fullBtc = BigDecimal(preparedCoinapult.satoshis).divide(SATOSHIS_PER_BTC, MathContext.DECIMAL32)
                send = client.send(BigDecimal.ZERO, currency.name, address.toString(), amount, null, null, null)
            }
//            val error = send["error"]
//            if (error != null) {
//                return false
//            }
//            val transaction_id = send["transaction_id"]
//            val hasId = transaction_id != null
//            if (hasId) {
//                extraHistory.add(send)
//            }
//            return hasId
        } catch (e: IOException) {
//            return false
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }


    }
}