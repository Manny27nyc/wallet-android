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
package com.mrd.bitlib.crypto.schnorr

/**
 * A data object representing a Schnorr Signature of `message`.
 *
 *
 * Note: `s` and `e` represent *positive* big-Endian integers. In particular,
 * any zero byte padding inserted by java.math.BigInteger should be removed before creating
 * one of these objects.
 *
 * @param s       the *s* parameter of the Schnorr signature.
 * @param e       the *e* parameter of the Schnorr signature.
 * @param message the message which has been signed.
 */
class SchnorrSignature(val s: ByteArray, val e: ByteArray, val message: ByteArray) {
     fun getSignatureBytes() = s + e
 }