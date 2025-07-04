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

import com.mrd.bitlib.bitcoinj.Base58
import com.mrd.bitlib.crypto.BipSss.IncompatibleSharesException
import com.mrd.bitlib.crypto.BipSss.NotEnoughSharesException
import com.mrd.bitlib.crypto.BipSss.Share.Companion.SSS_EXT2_PREFIX
import com.mrd.bitlib.crypto.BipSss.Share.Companion.SSS_EXT_PREFIX
import com.mrd.bitlib.crypto.BipSss.Share.Companion.SSS_PREFIX
import com.mrd.bitlib.util.BitUtils
import com.mrd.bitlib.util.ByteReader
import com.mrd.bitlib.util.ByteReader.InsufficientBytesException
import com.mrd.bitlib.util.ByteWriter
import com.mrd.bitlib.util.HashUtils
import java.io.Serializable
import java.lang.Exception

/**
 * Class implementing BIP-SS (place holder name until there actually is a BIP),
 * which allows combining shares to retrieve the secret
 *
 *
 * At its core it uses a 2^8 Galois Field  combining shares.
 * This allows you to split any secret into a number of shares and specify a
 * threshold, which defines the necessary number of shares needed to combine the
 * original secret. This allows you to for instance split a private key into 3
 * shares, where any 2 of those shares allow you to recreate the private key,
 * while preventing anyone having zero or one share have your private key. This
 * effectively protects you against loss or theft of your private key.
 *
 *
 */
enum class BipSssType(val prefix: String) {
    SSS(SSS_PREFIX), SSS_EXT(SSS_EXT_PREFIX), SSS_EXT2(SSS_EXT2_PREFIX);
}

object BipSss {
    const val TYPE_BASE_58_STRING: Int = 19

    /**
     *
     * @param shares the list of shares to combine
     * @return a base58 encoded string with the secret
     * @throws IncompatibleSharesException if there are shares not belonging to the same secret
     * @throws NotEnoughSharesException if more shares are needed to get the secret
     * @throws InvalidContentTypeException if the content type is not 19 (for base58 encoded secret)
     */
    @Throws(
        IncompatibleSharesException::class,
        NotEnoughSharesException::class,
        InvalidContentTypeException::class
    )
    @JvmStatic
    fun combine(shares: Collection<Share>): String {
        // Need at least one share

        if (shares.isEmpty()) {
            //todo figure out something better - disallow empty list
            throw NotEnoughSharesException(1)
        }

        // Figure out whether the shares are compatible
        val firstShare = shares.first()
        if (shares.any { !it.isCompatible(firstShare) }) {
            throw IncompatibleSharesException()
        }

        // Does it have the right format?
        if (firstShare.contentType != TYPE_BASE_58_STRING) {
            throw InvalidContentTypeException()
        }

        // Get the set of unique shares
        val unique = Share.Companion.removeDuplicateIndexes(shares)

        // Figure out whether we have enough shares (if possible)
        val threshold = unique.first().threshold
        if (threshold > unique.size) {
            throw NotEnoughSharesException(threshold - unique.size)
        }

        // Make a selection of the necessary shares
        val selection = unique.toList().subList(0, threshold)

        val content = if (firstShare.version == 2) {
            val gfv2 = Gf65536()
            val gfShares = selection.map { s -> Gf65536.Share(s.shareNumber, s.shareData) }
            gfv2.combineShares(gfShares)
        } else {
            // Combine
            val gf = Gf256()
            val gfShares = selection.map { s -> Gf256.Share(s.shareNumber.toByte(), s.shareData) }
            gf.combineShares(gfShares)
        }

        return Base58.encodeWithChecksum(content)
    }


    @JvmStatic
    fun split(secret: ByteArray, totalShares: Int, threshold: Int): List<Share> =
        if (totalShares < 256) {
            val gf = Gf256()
            val shares = gf.makeShares(secret, threshold, totalShares)
            val id = HashUtils.sha256(secret).bytes.copyOfRange(0, 2)

            shares.map {
                val shareNumber = it.index.toInt() and 0xFF
                Share(TYPE_BASE_58_STRING, id, shareNumber, threshold, totalShares, it.data, 1)
            }
        } else {
            val gfv2 = Gf65536()
            val shares = gfv2.makeShares(secret, threshold, totalShares)
            val id = HashUtils.sha256(secret).bytes.copyOfRange(0, 2)
            shares.map {
                Share(TYPE_BASE_58_STRING, id, it.index, threshold, totalShares, it.data, 2)
            }
        }

    class NotEnoughSharesException(@JvmField var needed: Int) : Exception()

    class InvalidContentTypeException : Exception()

    class IncompatibleSharesException : Exception()

    class Share(
        /**
         * The content type of this share.
         */
        val contentType: Int,
        @JvmField
        val id: ByteArray,
        /**
         * The share number for this share
         */
        @JvmField

        val shareNumber: Int,
        /**
         * The number of shares necessary to recreate the original content
         */
        val threshold: Int,
        /**
         * The data of this share
         */
        val totalShares: Int,
        val shareData: ByteArray,
        val version:Int = 1
    ) : Serializable {

        override fun toString(): String = toString(null)

        fun toString(exportType: BipSssType?): String {
            val w = ByteWriter(1024)
            w.put(contentType.toByte())
            w.putBytes(id)
            val type = if (exportType == null) {
                if (totalShares < 16) BipSssType.SSS
                else if (totalShares < 256) BipSssType.SSS_EXT
                else BipSssType.SSS_EXT2
            } else {
                exportType
            }
            if (type == BipSssType.SSS) {
                w.put(getByteForNumberAndThreshold(shareNumber, threshold))
            } else {
                w.putCompactInt(threshold.toLong())
                w.putCompactInt(shareNumber.toLong())
            }
            w.putBytes(shareData)
            val base58 = Base58.encodeWithChecksum(w.toBytes())
            return type.prefix + base58
        }

        private fun getByteForNumberAndThreshold(shareNumber: Int, threshold: Int): Byte =
            ((threshold - 1) * 16 + (shareNumber - 1)).toByte()

        /**
         * Determine whether two shares are compatible, and can be combined.
         */
        fun isCompatible(share: Share): Boolean {
            if (contentType != share.contentType) {
                return false
            }
            if (!BitUtils.areEqual(id, share.id)) {
                return false
            }
            if (threshold != share.threshold) {
                return false
            }
            return true
        }

        companion object {
            const val SSS_PREFIX: String = "SSS-"
            const val SSS_EXT_PREFIX: String = "SSSExt-"
            const val SSS_EXT2_PREFIX: String = "SSSExt2-"

            /**
             * Create a share from its string representation.
             *
             * @param encodedShare
             * the string representing the share
             * @return the decoded share or null if the string was not a valid share
             * encoding
             */
            @JvmStatic
            fun fromString(encodedShare: String): Share? {
                //check for SSS- prefix
                var encodedShare = encodedShare
                var type = BipSssType.SSS
                if (encodedShare.startsWith(SSS_EXT2_PREFIX)) {
                    encodedShare = encodedShare.substring(SSS_EXT2_PREFIX.length)
                    type = BipSssType.SSS_EXT2
                } else if (encodedShare.startsWith(SSS_EXT_PREFIX)) {
                    encodedShare = encodedShare.substring(SSS_EXT_PREFIX.length)
                    type = BipSssType.SSS_EXT
                } else if (encodedShare.startsWith(SSS_PREFIX)) {
                    encodedShare = encodedShare.substring(SSS_PREFIX.length)
                    type = BipSssType.SSS
                }
                // Base58 decode
                val decoded = Base58.decodeChecked(encodedShare)
                if (decoded == null || decoded.size < 4) {
                    return null
                }
                val reader = ByteReader(decoded)
                return try {
                    // content type
                    val contentByte = reader.get()
                    //id of the secret
                    val id = reader.getBytes(2)
                    val shareNumber: Int
                    val threshold: Int
                    if (type == BipSssType.SSS_EXT ||
                        type == BipSssType.SSS_EXT2
                    ) {
                        threshold = reader.compactInt.toInt()
                        shareNumber = reader.compactInt.toInt()
                    } else {
                        //contains number of this share and total shares needed
                        val numberAndThreshold = reader.get()
                        threshold = getThreshold(numberAndThreshold)
                        shareNumber = getShareNumber(numberAndThreshold)
                    }
                    //the  share bytes
                    val content = reader.getBytes(reader.available())

                    Share(
                        b2i(contentByte), id, shareNumber, threshold, -1, content,
                        if (type == BipSssType.SSS_EXT2) 2 else 1
                    )
                } catch (e: InsufficientBytesException) {
                    // This should not happen as we already have checked the content length
                    null
                }
            }

            private fun getThreshold(numberAndThreshold: Byte): Int =
                (b2i(numberAndThreshold) / 16) + 1


            private fun getShareNumber(numberAndThreshold: Byte): Int =
                (b2i(numberAndThreshold) % 16) + 1


            fun removeDuplicateIndexes(shares: Collection<Share>): Collection<Share> =
                shares.associate { it.shareNumber to it }.values

            private fun b2i(b: Byte): Int = (b.toInt()) and 0xFF
        }
    }
}
