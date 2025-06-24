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
 * Copyright 2013, 2014 Megion Research & Development GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mrd.bitlib.util;

import com.google.common.base.Joiner;

import java.io.IOException;
import java.io.Reader;

public class StringUtils {

   /**
    * Join an array of strings with the given separator.
    *
    * @param strings
    *           The strings to join
    * @param separator
    *           The separator to use
    * @return The concatenation of the collection of strings with the given
    *         separator.
    */
   public static String join(String[] strings, String separator) {
      return Joiner.on(separator).join(strings);
   }

   public static String readFully(Reader reader) throws IOException {
      char[] buffer = new char[2048];
      StringBuilder sb = new StringBuilder();
      while (true) {
         int read = reader.read(buffer);
         if (read != -1) {
            sb.append(buffer, 0, read);
         } else {
            return sb.toString();
         }
      }
   }

}
