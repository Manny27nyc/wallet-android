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

package com.btchip;

public class BTChipException extends Exception {

   /**
    *
    */
   private static final long serialVersionUID = 5512803003827126405L;

   public BTChipException(String reason) {
      super(reason);
   }

   public BTChipException(String reason, Throwable cause) {
      super(reason, cause);
   }

   public BTChipException(String reason, int sw) {
      super(reason);
      this.sw = sw;
   }

   public int getSW() {
      return sw;
   }

   public String toString() {
      if (sw == 0) {
         return "BTChip Exception : " + getMessage();
      } else {
         return "BTChip Exception : " + getMessage() + " " + Integer.toHexString(sw);
      }
   }

   private int sw;

}
