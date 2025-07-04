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
*******************************************************************************    
*   Ledger Bitcoin Hardware Wallet Java API
*   (c) 2014-2015 Ledger - 1BTChip7VfTnrPra5jqci7ejnMguuHogTn
*   
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*   limitations under the License.
********************************************************************************
*/

package com.btchip.utils;

import com.btchip.BTChipException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BufferUtils {

   public static void writeUint32BE(ByteArrayOutputStream buffer, long value) {
      buffer.write((byte) ((value >> 24) & 0xff));
      buffer.write((byte) ((value >> 16) & 0xff));
      buffer.write((byte) ((value >> 8) & 0xff));
      buffer.write((byte) (value & 0xff));
   }

   public static void writeUint32LE(ByteArrayOutputStream buffer, long value) {
      buffer.write((byte) (value & 0xff));
      buffer.write((byte) ((value >> 8) & 0xff));
      buffer.write((byte) ((value >> 16) & 0xff));
      buffer.write((byte) ((value >> 24) & 0xff));
   }

   public static void writeUint64LE(ByteArrayOutputStream buffer, long value) {
      buffer.write((byte) (value & 0xff));
      buffer.write((byte) ((value >> 8) & 0xff));
      buffer.write((byte) ((value >> 16) & 0xff));
      buffer.write((byte) ((value >> 24) & 0xff));
      buffer.write((byte) ((value >> 32) & 0xff));
      buffer.write((byte) ((value >> 40) & 0xff));
      buffer.write((byte) ((value >> 48) & 0xff));
      buffer.write((byte) ((value >> 56) & 0xff));
   }

   public static void writeUint64BE(ByteArrayOutputStream buffer, long value) {
      buffer.write((byte) ((value >> 56) & 0xff));
      buffer.write((byte) ((value >> 48) & 0xff));
      buffer.write((byte) ((value >> 40) & 0xff));
      buffer.write((byte) ((value >> 32) & 0xff));
      buffer.write((byte) ((value >> 24) & 0xff));
      buffer.write((byte) ((value >> 16) & 0xff));
      buffer.write((byte) ((value >> 8) & 0xff));
      buffer.write((byte) (value & 0xff));
   }

   public static void writeBuffer(ByteArrayOutputStream buffer, byte[] data) throws BTChipException {
      try {
         buffer.write(data);
      } catch (IOException e) {
         throw new BTChipException("Internal error", e);
      }
   }

}
