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
package com.mrd.bitlib.crypto

import com.google.common.base.Preconditions
import com.mrd.bitlib.bitcoinj.Base58
import com.mrd.bitlib.crypto.ec.EcTools
import com.mrd.bitlib.crypto.ec.Parameters
import com.mrd.bitlib.crypto.ec.Point
import com.mrd.bitlib.crypto.schnorr.SchnorrSign
import com.mrd.bitlib.model.NetworkParameters
import com.mrd.bitlib.util.ByteWriter
import com.mrd.bitlib.util.HashUtils
import com.mrd.bitlib.util.Sha256Hash
import com.mrd.bitlib.util.TaprootUtils
import java.io.Serializable
import java.math.BigInteger

/**
 * A Bitcoin private key that is kept in memory.
 */
class InMemoryPrivateKey(
    private var privateKey: BigInteger,
    compressed: Boolean = false,
    override var publicKey: PublicKey = getPublicKey(privateKey, compressed)
) : PrivateKey(), KeyExporter, Serializable {

    /**
     * Construct a random private key using a secure random source with optional
     * compressed public keys.
     *
     * @param  randomSource
     * The random source from which the private key will be
     * deterministically generated.
     * @param compressed
     * Specifies whether the corresponding public key should be
     * compressed
     */
    /**
     * Construct a random private key using a secure random source. Using this
     * constructor yields uncompressed public keys.
     */
    @JvmOverloads
    constructor(randomSource: RandomSource, compressed: Boolean = false) :
            this(privateKey(randomSource), compressed)

    constructor(hash: Sha256Hash, compressed: Boolean) : this(hash.bytes, compressed)

    /**
     * Construct from private key bytes. Using this constructor yields
     * uncompressed public keys.
     *
     * @param bytes
     * The private key as an array of bytes
     * @param compressed
     * Specifies whether the corresponding public key should be
     * compressed
     */
    /**
     * Construct from private key bytes. Using this constructor yields
     * uncompressed public keys.
     *
     * @param bytes
     * The private key as an array of bytes
     */
    @JvmOverloads
    constructor(bytes: ByteArray, compressed: Boolean = false) : this(privateKey(bytes), compressed)

    /**
     * Construct from private and public key bytes
     *
     * @param priBytes
     * The private key as an array of bytes
     */
    constructor(priBytes: ByteArray, pubBytes: ByteArray) :
            this(privateKey(priBytes), false, PublicKey(pubBytes))


    private constructor(pair: Pair<BigInteger, Boolean>) : this(pair.first, pair.second)

    /**
     * Construct from a base58 encoded key (SIPA format)
     */
    constructor(base58Encoded: String?, network: NetworkParameters) :
            this(privateKey(base58Encoded, network))

    private fun calculateE(n: BigInteger, messageHash: ByteArray): BigInteger =
        if (n.bitLength() > messageHash.size * 8) {
            BigInteger(1, messageHash)
        } else {
            val messageBitLength = messageHash.size * 8
            var trunc = BigInteger(1, messageHash)

            if (messageBitLength - n.bitLength() > 0) {
                trunc = trunc.shiftRight(messageBitLength - n.bitLength())
            }
            trunc
        }


    private abstract class DsaSignatureNonceGen {
        abstract fun getNonce(): BigInteger
    }

    private class DsaSignatureNonceGenDeterministic(
        private val messageHash: Sha256Hash,
        private val privateKey: KeyExporter
    ) : DsaSignatureNonceGen() {

        // rfc6979 compliant generation of k-value for DSA
        override fun getNonce(): BigInteger {
            // Step b
            var v = ByteArray(32) { 0x01.toByte() }

            // Step c
            var k = ByteArray(32) { 0x00.toByte() }

            // Step d
            val bwD = ByteWriter(32 + 1 + 32 + 32)
            bwD.putBytes(v)
            bwD.put(0x00.toByte())
            bwD.putBytes(privateKey.getPrivateKeyBytes())
            bwD.putBytes(messageHash.bytes)
            k = Hmac.hmacSha256(k, bwD.toBytes())

            // Step e
            v = Hmac.hmacSha256(k, v)

            // Step f
            val bwF = ByteWriter(32 + 1 + 32 + 32)
            bwF.putBytes(v)
            bwF.put(0x01.toByte())
            bwF.putBytes(privateKey.getPrivateKeyBytes())
            bwF.putBytes(messageHash.bytes)
            k = Hmac.hmacSha256(k, bwF.toBytes())

            // Step g
            v = Hmac.hmacSha256(k, v)

            // Step H2b
            v = Hmac.hmacSha256(k, v)

            var t = bits2int(v)

            // Step H3, repeat until T is within the interval [1, Parameters.n - 1]
            while ((t.signum() <= 0) || (t >= Parameters.n)) {
                val bwH = ByteWriter(32 + 1)
                bwH.putBytes(v)
                bwH.put(0x00.toByte())
                k = Hmac.hmacSha256(k, bwH.toBytes())
                v = Hmac.hmacSha256(k, v)

                t = BigInteger(v)
            }
            return t
        }

        // Step H1/H2a, ignored as tlen === qlen (256 bit)
        fun bits2int(bits: ByteArray): BigInteger = BigInteger(1, bits)
    }

    override fun generateSignature(messageHash: Sha256Hash): Signature =
        generateSignatureInternal(
            messageHash,
            DsaSignatureNonceGenDeterministic(messageHash, this)
        )


    private fun generateSignatureInternal(
        messageHash: Sha256Hash,
        kGen: DsaSignatureNonceGen
    ): Signature {
        val n = Parameters.n
        val e = calculateE(n, messageHash.bytes) //leaving strong typing here
        var r: BigInteger
        var s: BigInteger

        // 5.3.2
        do  // generate s
        {
            val k = kGen.getNonce()

            // generate r
            val p = EcTools.multiply(Parameters.G, k)

            // 5.3.3
            val x = p.x.toBigInteger()

            r = x.mod(n)

            s = k.modInverse(n).multiply(e.add(privateKey.multiply(r))).mod(n)
        } while (s == BigInteger.ZERO)

        // Enforce low S value
        if (s > Parameters.MAX_SIG_S) {
            // If the signature is larger than MAX_SIG_S, inverse it
            s = Parameters.n.subtract(s)
        }

        return Signature(r, s)
    }

    override fun getPrivateKeyBytes(): ByteArray {
        val result = ByteArray(32)
        val bytes = privateKey.toByteArray()
        if (bytes.size <= result.size) {
            System.arraycopy(bytes, 0, result, result.size - bytes.size, bytes.size)
        } else {
            // This happens if the most significant bit is set and we have an
            // extra leading zero to avoid a negative BigInteger
            Preconditions.checkState(bytes.size == 33 && bytes[0].toInt() == 0)
            System.arraycopy(bytes, 1, result, 0, bytes.size - 1)
        }
        return result
    }

    override fun getBase58EncodedPrivateKey(network: NetworkParameters): String =
        if (publicKey.isCompressed) {
            getBase58EncodedPrivateKeyCompressed(network)
        } else {
            getBase58EncodedPrivateKeyUncompressed(network)
        }

    fun getPrivateKeyBytes(network: NetworkParameters): ByteArray =
        if (publicKey.isCompressed) {
            getPrivateKeyBytesCompressed(network)
        } else {
            getPrivateKeyBytesUncompressed(network)
        }

    private fun getBase58EncodedPrivateKeyUncompressed(network: NetworkParameters): String {
        val toEncode = getPrivateKeyBytesUncompressed(network)
        // Set checksum
        val checkSum = HashUtils.doubleSha256(toEncode, 0, 1 + 32).firstFourBytes()
        // Encode
        return Base58.encode(toEncode + checkSum)
    }

    fun getPrivateKeyBytesUncompressed(network: NetworkParameters): ByteArray =
        // network byte + key bytes(32)
        ByteArray(0) + (if (network.isProdnet) 0x80.toByte() else 0xEF.toByte()) + getPrivateKeyBytes()

    private fun getBase58EncodedPrivateKeyCompressed(network: NetworkParameters): String {
        val toEncode = getPrivateKeyBytesCompressed(network)
        // Set checksum
        val checkSum = HashUtils.doubleSha256(toEncode, 0, 1 + 32 + 1).firstFourBytes()
        // Encode
        return Base58.encode(toEncode + checkSum)
    }

    fun getPrivateKeyBytesCompressed(network: NetworkParameters): ByteArray =
        //network byte + key bytes(32) + compressed indicator
        ByteArray(0) + (if (network.isProdnet) 0x80.toByte() else 0xEF.toByte()) + getPrivateKeyBytes() + (0x01).toByte()

    override fun makeSchnorrBitcoinSignature(message: ByteArray, merkle: ByteArray): ByteArray =
        makeSchnorrBitcoinSignature(message, merkle, null)

    override fun makeSchnorrBitcoinSignature(
        message: ByteArray,
        merkle: ByteArray,
        auxRand: ByteArray?
    ): ByteArray {
        val tweak = TaprootUtils.tweak(publicKey, merkle)
        return SchnorrSign(TaprootUtils.tweakedPrivateKey(this.getPrivateKeyBytes(), tweak))
            .sign(message, auxRand)
    }

    companion object {
        private fun privateKey(randomSource: RandomSource): BigInteger {
            val nBitLength = Parameters.n.bitLength()
            var d: BigInteger
            do {
                // Make a BigInteger from bytes to ensure that Andriod and 'classic'
                // java make the same BigIntegers from the same random source with the
                // same seed. Using BigInteger(nBitLength, random)
                // produces different results on Android compared to 'classic' java.
                val bytes = ByteArray(nBitLength / 8)
                randomSource.nextBytes(bytes)
                bytes[0] = (bytes[0].toInt() and 0x7F).toByte() // ensure positive number
                d = BigInteger(bytes)
            } while (d == BigInteger.ZERO || (d >= Parameters.n))
            return d
        }

        private fun privateKey(bytes: ByteArray): BigInteger {
            require(bytes.size == 32) { "The length must be 32 bytes" }
            // Ensure that we treat it as a positive number
            val keyBytes = ByteArray(33)
            System.arraycopy(bytes, 0, keyBytes, 1, 32)
            return BigInteger(keyBytes)
        }

        private fun privateKey(
            base58Encoded: String?,
            network: NetworkParameters
        ): Pair<BigInteger, Boolean> {
            var decoded = Base58.decodeChecked(base58Encoded)

            // Validate format
            require(!(decoded == null || decoded.size < 33 || decoded.size > 34)) { "Invalid base58 encoded key" }
            require(!(network == NetworkParameters.productionNetwork && decoded[0] != 0x80.toByte())) { "The base58 encoded key is not for the production network" }
            require(!(network == NetworkParameters.testNetwork && decoded[0] != 0xEF.toByte())) { "The base58 encoded key is not for the test network" }

            // Determine whether compression should be used for the public key
            var compressed: Boolean
            if (decoded.size == 34) {
                require(decoded[33].toInt() == 0x01) { "Invalid base58 encoded key" }
                // Get rid of the compression indication byte at the end
                val temp = ByteArray(33)
                System.arraycopy(decoded, 0, temp, 0, temp.size)
                decoded = temp
                compressed = true
            } else {
                compressed = false
            }
            // Make positive and clear network prefix
            decoded[0] = 0
            return BigInteger(decoded) to compressed
        }


        private fun getPublicKey(privateKey: BigInteger, compressed: Boolean = false): PublicKey {
            var Q = EcTools.multiply(Parameters.G, privateKey)
            if (compressed) {
                // Convert Q to a compressed point on the curve
                Q = Point(Q.curve, Q.x, Q.y, true)
            }
            return PublicKey(Q.getEncoded())
        }

        @JvmStatic
        fun fromBase58String(
            base58: String?,
            network: NetworkParameters
        ): InMemoryPrivateKey? =
            try {
                InMemoryPrivateKey(base58, network)
            } catch (e: IllegalArgumentException) {
                null
            }

        @JvmStatic
        fun fromBase58MiniFormat(
            base58: String?,
            network: NetworkParameters?
        ): InMemoryPrivateKey? {
            // Is it a mini private key on the format proposed by Casascius?
            if (base58 == null || base58.length < 2 || !base58.startsWith("S")) {
                return null
            }
            // Check that the string has a valid checksum
            val withQuestionMark = "$base58?"
            val checkHash = HashUtils.sha256(withQuestionMark.toByteArray()).firstFourBytes()
            if (checkHash[0].toInt() != 0x00) {
                return null
            }
            // Now get the Sha256 hash and use it as the private key
            val privateKeyBytes = HashUtils.sha256(base58.toByteArray())
            return try {
                InMemoryPrivateKey(privateKeyBytes, false)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}
