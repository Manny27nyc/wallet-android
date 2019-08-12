package com.mycelium.wapi.wallet.bch.coins

import com.mrd.bitlib.model.Address
import com.mycelium.wapi.wallet.GenericAddress
import com.mycelium.wapi.wallet.btc.BtcAddress
import com.mycelium.wapi.wallet.btc.coins.BitcoinMain
import com.mycelium.wapi.wallet.btc.coins.BitcoinTest
import com.mycelium.wapi.wallet.coins.CryptoCurrency
import com.mycelium.wapi.wallet.coins.families.Families

abstract class BchCoin: CryptoCurrency(){
    init {
        family = Families.BITCOIN
    }

    override fun getName(): String {
        return "Bitcoin Cash"
    }

    override fun parseAddress(addressString: String?): GenericAddress {
        val address = Address.fromString(addressString)
        return BtcAddress(if (address.network.isProdnet) BitcoinMain.get() else BitcoinTest.get(), address)
    }
}