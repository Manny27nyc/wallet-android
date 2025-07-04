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
import java.util.Vector;

public class BIP32Utils {

   public static byte[] splitPath(String path) throws BTChipException {
      if (path.length() == 0) {
         return new byte[]{0};
      }
      String elements[] = path.split("/");
      if (elements.length > 10) {
         throw new BTChipException("Path too long");
      }
      ByteArrayOutputStream result = new ByteArrayOutputStream();
      result.write((byte) elements.length);
      for (String element : elements) {
         long elementValue;
         int hardenedIndex = element.indexOf('\'');
         if (hardenedIndex > 0) {
            elementValue = Long.parseLong(element.substring(0, hardenedIndex));
            elementValue |= 0x80000000;
         } else {
            elementValue = Long.parseLong(element);
         }
         BufferUtils.writeUint32BE(result, elementValue);
      }
      return result.toByteArray();
   }
   
   public static Long[] pathToComponents(String path) throws BTChipException {
	   Vector<Long> result = new Vector<Long>();
	   if (path.length() == 0) {
		   return new Long[0];
	   }
	   String elements[] = path.split("/");
	   if (elements.length > 10) {
		   throw new BTChipException("Path too long");
	   }
	   for (String element : elements) {
		   long elementValue;
		   int hardenedIndex = element.indexOf('\'');
		   if (hardenedIndex > 0) {
			   elementValue = Long.parseLong(element.substring(0, hardenedIndex));
			   elementValue |= 0x80000000;
		   } else {
			   elementValue = Long.parseLong(element);
		   }
		   result.add(elementValue);
	   }
	   return result.toArray(new Long[0]);
   }
   
   public static byte[] serializePath(Long pathComponents[]) {
	   ByteArrayOutputStream result = new ByteArrayOutputStream();
	   result.write((byte) pathComponents.length);
	   for (Long element : pathComponents) {
		   BufferUtils.writeUint32BE(result, element);  
	   }
	   return result.toByteArray();
   }
   
   public static boolean isHardened(Long element) {
	   return ((element & 0x80000000) != 0);
   }
}
