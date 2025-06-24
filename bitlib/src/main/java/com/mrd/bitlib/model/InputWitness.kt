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
package com.mrd.bitlib.model

import com.mrd.bitlib.util.ByteWriter

import java.io.Serializable
import java.util.ArrayList
import kotlin.math.min

class InputWitness(val pushCount: Int) : Serializable {
    private val stack = ArrayList<ByteArray>(min(pushCount, MAX_INITIAL_ARRAY_LENGTH))

    fun setStack(i: Int, value: ByteArray) {
        while (i >= stack.size) {
            stack.add(byteArrayOf())
        }
        stack[i] = value
    }

    fun toByteWriter(writer: ByteWriter) {
        writer.putCompactInt(stack.size.toLong())
        stack.forEach { element ->
            writer.putCompactInt(element.size.toLong())
            writer.putBytes(element)
        }
    }

    companion object {
        @JvmField
        val EMPTY = InputWitness(0)
        const val MAX_INITIAL_ARRAY_LENGTH = 20
    }
}
