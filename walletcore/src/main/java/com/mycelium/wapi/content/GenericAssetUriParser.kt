package com.mycelium.wapi.content

import com.mrd.bitlib.crypto.Bip38
import com.mrd.bitlib.model.NetworkParameters
import com.mycelium.wapi.content.btc.BitcoinUri
import com.mycelium.wapi.content.colu.mss.MSSUri
import com.mycelium.wapi.content.colu.mt.MTUri
import com.mycelium.wapi.content.colu.rmc.RMCUri
import com.mycelium.wapi.wallet.AddressUtils
import com.mycelium.wapi.wallet.GenericAddress
import com.mycelium.wapi.wallet.btc.coins.BitcoinMain
import com.mycelium.wapi.wallet.btc.coins.BitcoinTest
import com.mycelium.wapi.wallet.coins.CryptoCurrency
import com.mycelium.wapi.wallet.coins.Value
import com.mycelium.wapi.wallet.colu.coins.*
import java.math.BigDecimal
import java.net.URI
import java.net.URLDecoder


abstract class GenericAssetUriParser(open val network: NetworkParameters) : UriParser {
    protected fun parseParameters(uri: URI, coinType: CryptoCurrency): GenericAssetUri? {
        // Address
        var address: GenericAddress? = null
        val addressString = uri.host
        if (addressString?.isNotEmpty() == true) {
            address = AddressUtils.from(coinType, addressString)
        }

        val params = splitQuery(uri.rawQuery)

        val amount: Value? = params["amount"]?.let {
            Value.valueOf(coinType, BigDecimal(it).multiply(BigDecimal.TEN.pow(8)).toLong())
        }

        // Label
        // Bip21 defines "?label" and "?message" - lets try "label" first and if it does not
        // exist, lets use "message"
        val label: String? = params["label"] ?: params["message"]

        // Check if the supplied "address" is actually an encrypted private key
        if (addressString != null && Bip38.isBip38PrivateKey(addressString)) {
            if (coinType == BitcoinMain.get() || coinType == BitcoinTest.get()) {
                return PrivateKeyUri(addressString, label, "bitcoin")
            }
            return PrivateKeyUri(addressString, label, coinType.symbol.decapitalize())
        }

        // Payment Uri
        val paymentUri: String? = params["r"]

        return if (address == null && paymentUri == null) {
            null
        } else {
            createUriByCoinType(coinType, address, amount, label, paymentUri)
        }
    }

    fun splitQuery(query: String?): Map<String, String> {
        query ?: return emptyMap()
        val pairs = query.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return pairs.map {
            val kv = it.split("=".toRegex(), 2)
            URLDecoder.decode(kv[0], "UTF-8") to URLDecoder.decode(kv[1], "UTF-8")
        }.toMap()
    }

    companion object {
        @JvmStatic
        fun createUriByCoinType(coinType: CryptoCurrency,
                                address: GenericAddress?,
                                amount: Value?,
                                label: String?,
                                paymentUri: String?): GenericAssetUri? {
            return when (coinType) {
                is BitcoinMain, is BitcoinTest -> BitcoinUri(address, amount, label, paymentUri)
                is RMCCoin, is RMCCoinTest -> RMCUri(address, amount, label, paymentUri)
                is MTCoin, is MTCoinTest -> MTUri(address, amount, label, paymentUri)
                is MASSCoin, is MASSCoinTest -> MSSUri(address, amount, label, paymentUri)
                else -> null
            }
        }
    }
}
