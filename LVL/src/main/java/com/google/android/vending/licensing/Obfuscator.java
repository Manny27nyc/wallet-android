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
/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.vending.licensing;

/**
 * Interface used as part of a {@link Policy} to allow application authors to obfuscate
 * licensing data that will be stored into a SharedPreferences file.
 * <p>
 * Any transformation scheme must be reversable. Implementing classes may optionally implement an
 * integrity check to further prevent modification to preference data. Implementing classes
 * should use device-specific information as a key in the obfuscation algorithm to prevent
 * obfuscated preferences from being shared among devices.
 */
public interface Obfuscator {

    /**
     * Obfuscate a string that is being stored into shared preferences.
     *
     * @param original The data that is to be obfuscated.
     * @param key The key for the data that is to be obfuscated.
     * @return A transformed version of the original data.
     */
    String obfuscate(String original, String key);

    /**
     * Undo the transformation applied to data by the obfuscate() method.
     *
     * @param original The data that is to be obfuscated.
     * @param key The key for the data that is to be obfuscated.
     * @return A transformed version of the original data.
     * @throws ValidationException Optionally thrown if a data integrity check fails.
     */
    String unobfuscate(String obfuscated, String key) throws ValidationException;
}
