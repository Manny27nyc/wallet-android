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

import com.google.common.base.Strings
import com.mrd.bitlib.crypto.IPublicKeyRing
import com.mrd.bitlib.crypto.PublicKey
import com.mrd.bitlib.model.*
import com.mrd.bitlib.util.CoinUtil
import com.mrd.bitlib.util.TaprootUtils
import com.mrd.bitlib.util.TaprootUtils.Companion.sigHash
import java.io.Serializable

open class UnsignedTransaction constructor(
        outputs: List<TransactionOutput>,
        funding: List<UnspentTransactionOutput>,
        keyRing: IPublicKeyRing,
        private val network: NetworkParameters,
        val lockTime: Int = 0,
        val defaultSequenceNumber: Int = NO_SEQUENCE
) : Serializable {
    val outputs = outputs.toTypedArray()
    val fundingOutputs = funding.toTypedArray()
    val signingRequests: Array<SigningRequest?> = arrayOfNulls(fundingOutputs.size)
    val inputs = fundingOutputs.map {
        TransactionInput(it.outPoint, ScriptInput.EMPTY, defaultSequenceNumber, it.value)
    }.toTypedArray()

    init {
        // Create transaction with valid outputs and empty inputs
        val transaction = BitcoinTransaction(1, inputs, this.outputs, lockTime)

        for (i in fundingOutputs.indices) {
            if (isSegWitOutput(i)) {
                inputs[i].script = ScriptInput.fromOutputScript(fundingOutputs[i].script)
            }
            val utxo = fundingOutputs[i]

            // Make sure that we only work on supported scripts
            if (utxo.script.javaClass !in SUPPORTED_SCRIPTS) {
                throw RuntimeException("Unsupported script")
            }

            // Find the address of the funding
            val address = utxo.script.getAddress(network)

            // Find the key to sign with
            val publicKey = keyRing.findPublicKeyByAddress(address)
                    ?:
                    // This should not happen as we only work on outputs that we have keys for
                    throw RuntimeException("Public key not found")
            var signAlgorithm = SignAlgorithm.Standard
            when (utxo.script) {
                is ScriptOutputP2SH -> {
                    getInputScript(publicKey, transaction, i, true)
                    signAlgorithm = SignAlgorithm.Standard
                }
                is ScriptOutputP2WPKH -> {
                    getInputScript(publicKey, transaction, i, false)
                    signAlgorithm = SignAlgorithm.Standard
                }
                is ScriptOutputP2TR -> {
                    getInputScriptP2TR(publicKey, transaction, i)
                    signAlgorithm = SignAlgorithm.Schnorr
                }
            }

            val scriptsList: MutableList<ScriptInput> = mutableListOf()
            if (!isSegWitOutput(i)) {
                inputs.forEach {
                    scriptsList.add(it.script)
                    it.script = ScriptInput.EMPTY
                }
                inputs[i].script = ScriptInput.fromOutputScript(fundingOutputs[i].script)
            }

            val sigHash = if (signAlgorithm == SignAlgorithm.Schnorr) {
                sigHash(transaction.commonSignatureMessage(i, fundingOutputs))
            } else {
                // Calculate the transaction hash that has to be signed
                transaction.getTxDigestHash(i)
            }
            // Set the input to the empty script again
            if (!isSegWitOutput(i)) {
                inputs.forEachIndexed { index, it ->
                    it.script = scriptsList[index]
                }
                inputs[i] = TransactionInput(fundingOutputs[i].outPoint, ScriptInput.EMPTY, NO_SEQUENCE, fundingOutputs[i].value)
            }

            signingRequests[i] = SigningRequest(publicKey, sigHash, signAlgorithm)
        }
    }

    private fun getInputScriptP2TR(publicKey: PublicKey, transaction: BitcoinTransaction, i: Int) {
        val inputScript = ScriptInputP2TR(ByteArray(0) +
                Script.OP_1.toByte() + Script.OP_PUSHBYTES_32.toByte() +
                TaprootUtils.tweakedPublicKey(publicKey))
        transaction.inputs[i].script = inputScript
        inputs[i].script = inputScript
    }

    private fun getInputScript(publicKey: PublicKey, transaction: BitcoinTransaction, i: Int, isNested: Boolean) {
        val inpScriptBytes = ByteArray(0) +
                Script.OP_0.toByte() + publicKey.pubKeyHashCompressed.size.toByte() +
                publicKey.pubKeyHashCompressed
        val inputScript = ScriptInput.fromScriptBytes(ByteArray(0) +
                (inpScriptBytes.size and 0xFF).toByte() + inpScriptBytes)
        (inputScript as ScriptInputP2WPKH).isNested = isNested
        transaction.inputs[i].script = inputScript
        inputs[i].script = inputScript
    }

    fun isSegwit() = fundingOutputs.asSequence()
            .map(UnspentTransactionOutput::script)
            .any(this::isSegwitOutputScript)

    private fun isSegwitOutputScript(script: ScriptOutput) =
        script is ScriptOutputP2WPKH || script is ScriptOutputP2SH || script is ScriptOutputP2TR


    fun isSegWitOutput(i: Int) =
            isSegwitOutputScript(fundingOutputs[i].script)

    /**
     * @return fee in satoshis
     */
    fun calculateFee() = fundingOutputs.map { it.value }.sum() - outputs.map { it.value}.sum()


    override fun toString(): String {
        val inStrings = fundingOutputs.map {
            String.format("%36s %13s", it.script.getAddress(network), getValue(it.value))
        }
        val outStrings = outputs.map {
            String.format("%36s %13s", it.script.getAddress(network), getValue(it.value))
        }
        val fee = CoinUtil.valueString(calculateFee(), false)
        val sb = StringBuilder(String.format("Fee: %s\n", fee))
        val empty = Strings.repeat(" ", 50)
        for (i in 0 until Math.max(fundingOutputs.size, outputs.size)) {
            sb.append(inStrings.getOrNull(i) ?: empty).append(" -> ").append(outStrings.getOrNull(i) ?: empty).append('\n')
        }
        return sb.toString()
    }

    fun getSignatureInfo() = signingRequests

    private fun getValue(value: Long?) =
            String.format("(%s)", CoinUtil.valueString(value!!, false))

    companion object {
        private const val serialVersionUID = 1L
        const val NO_SEQUENCE = -1
        private val SUPPORTED_SCRIPTS = listOf(
                ScriptOutputP2PKH::class.java,
                ScriptOutputP2SH::class.java,
                ScriptOutputP2WPKH::class.java,
                ScriptOutputP2TR::class.java
        )
    }
}
