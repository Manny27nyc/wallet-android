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
package com.mrd.bitlib

import com.mrd.bitlib.crypto.IPublicKeyRing
import com.mrd.bitlib.model.NetworkParameters
import com.mrd.bitlib.model.TransactionOutput
import com.mrd.bitlib.model.UnspentTransactionOutput

class PopBuilder(network: NetworkParameters) : StandardTransactionBuilder(network) {

    class UnsignedPop constructor(outputs: List<TransactionOutput>, funding: List<UnspentTransactionOutput>, keyRing: IPublicKeyRing, network: NetworkParameters) :
            UnsignedTransaction(outputs, funding, keyRing, network, MAX_LOCK_TIME, POP_SEQUENCE_NUMBER) {

        companion object {
            const val MAX_LOCK_TIME = 499999999
            private const val POP_SEQUENCE_NUMBER = 0
        }
    }

    fun createUnsignedPop(outputs: List<TransactionOutput>, funding: List<UnspentTransactionOutput>,
                          keyRing: IPublicKeyRing, network: NetworkParameters): UnsignedPop {

        return UnsignedPop(outputs, funding, keyRing, network)
    }
}
