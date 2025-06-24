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
package com.mrd.bitlib.util;

import org.bouncycastle.util.encoders.Hex;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

// trivially testing our hex utils against the established bouncycastle tool Hex
public class HexUtilsTest {
    private final String [] strings = {"", "1212a873c0ff0023", "234235635645314243655746743565"};
    private final byte[][] bytess = {"Hello".getBytes(), "".getBytes(), "I've watched C-beams glitter in the dark near the Tannhäuser Gate.".getBytes()};

    @Test
    public void toHex() throws Exception {
        for(byte[] bytes : bytess) {
            String hex = new String(Hex.encode(bytes));
            assertEquals(hex, HexUtils.toHex(bytes));
        }
    }

    @Test
    public void toBytes() throws Exception {
        for(String string : strings) {
            byte [] bytes = Hex.decode(string);
            assertArrayEquals(bytes, HexUtils.toBytes(string));
        }
    }

    @Test
    @Ignore
    public void toBytesSpeedBitlib() {
        for(int i = 0; i < 10000000; i++) {
            for(String string : strings) {
                byte [] bytes = HexUtils.toBytes(string);
                String actual = HexUtils.toHex(bytes);
                assertEquals(actual, string);
            }
        }
    }

    @Test
    @Ignore
    // there is probably no good reason to have our own HexUtils. Bouncy also handles much nicer spaces in the hex string, like "00 43 43".
    public void toBytesSpeedHex() {
        for(int i = 0; i < 10000000; i++) {
            for(String string : strings) {
                byte [] bytes = Hex.decode(string);
                String actual = new String(Hex.encode(bytes));
                assertEquals(actual, string);
            }
        }
    }
}
