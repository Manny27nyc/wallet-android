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

package com.mycelium.lt;

import com.mycelium.net.HttpEndpoint;
import com.mycelium.net.ServerEndpoints;
import org.junit.Ignore;
import org.junit.Test;

import com.mycelium.lt.api.LtApi;
import com.mycelium.lt.api.LtApiClient;
import com.mycelium.lt.api.LtApiException;
import com.mycelium.lt.api.LtResponse;
import com.mycelium.lt.api.model.AdType;
import com.mycelium.lt.api.model.GpsLocation;
import com.mycelium.lt.api.model.LtSession;
import com.mycelium.lt.api.params.SearchParameters;

public class ManualTest {
   @Test
   @Ignore
   public void testManualConnect() throws LtApiException {
      HttpEndpoint testnetLocalTraderEndpoint = new HttpEndpoint("http://192.168.178.53:8098/trade/");

      LtApiClient client = new LtApiClient(new ServerEndpoints(new HttpEndpoint[]{testnetLocalTraderEndpoint}));
      LtResponse<LtSession> session = client.createSession(LtApi.VERSION, "en", "BTC");
      client.sellOrderSearch(session.getResult().id, new SearchParameters(new GpsLocation(0, 0, "dummy"), 2,
            AdType.SELL_BTC));
   }
}
