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

import com.mrd.bitlib.model.CompactInt

/**
 * https://bitcoinops.org/en/tools/calc-size/
 */
class FeeEstimator(private val legacyInputs: Int, private val p2shSegwitInputs: Int,
                   private val bechInputs: Int, private val bech32mInputs: Int,
                   private val legacyOutputs: Int, private val p2shOutputs: Int,
                   private val bechOutputs: Int, private val bech32mOutputs: Int,
                   private val minerFeePerKb: Long) {

    /**
     * Estimate the size of a transaction by taking the number of inputs and outputs into account. This allows us to
     * give a good estimate of the final transaction size, and determine whether out fee size is large enough.*/
    fun estimateTransactionSize(): Int {
        val totalOutputsSize = SEGWIT_NATIVE_OUTPUT_SIZE * bechOutputs + SEGWIT_COMPAT_OUTPUT_SIZE * p2shOutputs +
                OUTPUT_SIZE * legacyOutputs +
                TAPROOT_OUTPUT_SIZE * bech32mOutputs

        var estimateExceptInputs = 0
        if (bechInputs + p2shSegwitInputs + bech32mInputs > 0) {
            estimateExceptInputs += 2
        }
        estimateExceptInputs += 4 // Version info
        estimateExceptInputs += CompactInt.toBytes((legacyInputs + p2shSegwitInputs + bechInputs + bech32mInputs).toLong()).size // num input encoding. Usually 1. >253 inputs -> 3
        estimateExceptInputs += CompactInt.toBytes((legacyOutputs + p2shOutputs + bechOutputs + bech32mOutputs).toLong()).size // num output encoding. Usually 1. >253 outputs -> 3
        estimateExceptInputs += totalOutputsSize
        estimateExceptInputs += 4 // nLockTime

        val estimateWithSignatures = estimateExceptInputs +
                MAX_INPUT_SIZE * (legacyInputs + p2shSegwitInputs) +
                MAX_BECH32_INPUT_SIZE_TOTAL * bechInputs +
                MAX_BECH32M_INPUT_SIZE_TOTAL * bech32mInputs
        val estimateWithoutWitness = estimateExceptInputs +
                MAX_INPUT_SIZE * legacyInputs +
                MAX_SEGWIT_COMPAT_INPUT_SIZE * p2shSegwitInputs +
                MAX_SEGWIT_NATIVE_INPUT_SIZE_BASE * bechInputs +
                MAX_BECH32M_INPUT_SIZE_BASE * bech32mInputs

        return (estimateWithoutWitness * 3 + estimateWithSignatures) / 4
    }

    /**
     * Returns the estimate needed fee in satoshis for a default P2PKH transaction with a certain number
     * of inputs and outputs and the specified per-kB-fee
     **/
    fun estimateFee(): Long {
        // fee is based on the size of the transaction, we have to pay for
        // every 1000 bytes
        val txSizeKb = (estimateTransactionSize() / 1000.0).toFloat() //in kilobytes
        return (txSizeKb * minerFeePerKb).toLong()
    }

    companion object {
        // hash size 32 + output index size 4 + script length 1 + max. script size for compressed keys 107 + sequence number 4
        // also see https://github.com/bitcoin/bitcoin/blob/master/src/primitives/transaction.h#L190
        private const val MAX_INPUT_SIZE = 32 + 4 + 1 + 107 + 4

        // This input value is used to count core value without witness
        private const val MAX_SEGWIT_COMPAT_INPUT_SIZE = 32 + 4 + 1 + 46 + 4

        // This input value is used to count core value without witness
        // txid + prevVOUT + ScriptSig is empty (size = 1 byte) + sequence
        private const val MAX_SEGWIT_NATIVE_INPUT_SIZE_BASE = 32 + 4 + 1 + 4

        // stack + sign size + signature + hash type byte
        private const val MAX_SEGWIT_NATIVE_INPUT_SIZE_WITNESS = 1 + 1 + 71 + 1 + 33

        // BECH32 utxos does not contain scriptSig and scriptPubKey is 3 bytes smaller
        private const val MAX_BECH32_INPUT_SIZE_TOTAL =
            MAX_SEGWIT_NATIVE_INPUT_SIZE_BASE + MAX_SEGWIT_NATIVE_INPUT_SIZE_WITNESS


        // txid + prevVOUT + ScriptSig is empty (size = 1 byte) + sequence
        private const val MAX_BECH32M_INPUT_SIZE_BASE = 32 + 4 + 1 + 4

        // stack + sign size + signature(64) + hashtype byte
        private const val MAX_BECH32M_INPUT_SIZE_WITNESS = 1 + 1 + 64 + 1

        private const val MAX_BECH32M_INPUT_SIZE_TOTAL =
            MAX_BECH32M_INPUT_SIZE_BASE + MAX_BECH32M_INPUT_SIZE_WITNESS

        // output value 8B + script length 1B + script 25B (always)
        private const val OUTPUT_SIZE = 8 + 1 + 25

        private const val SEGWIT_COMPAT_OUTPUT_SIZE = 8 + 1 + 23
        private const val SEGWIT_NATIVE_OUTPUT_SIZE = 8 + 1 + 22
        //amount + script pubkey size + script pubkey
        private const val TAPROOT_OUTPUT_SIZE = 8 + 1 + 34
    }
}
