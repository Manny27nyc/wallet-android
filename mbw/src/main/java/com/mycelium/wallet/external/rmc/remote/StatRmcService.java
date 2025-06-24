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
package com.mycelium.wallet.external.rmc.remote;

import java.util.List;
import java.util.Map;

import retrofit.http.GET;
import retrofit.http.Path;

public interface StatRmcService {
    @GET("/stats/hashrate")
    long getCommonHashrate();

    @GET("/hashrate/{address}")
    long getHashrate(@Path("address") String address);

    @GET("/balance/{address}")
    long getBalance(@Path("address") String address);

    @GET("/payments/{address}")
    Map<String, List<String>> getPaidTransactions(@Path("address") String address);
}
