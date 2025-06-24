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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycelium.wallet.api.retrofit.JacksonConverter;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public class StatRmcFactory {
    private static final String STAT_RMC_ENDPOINT = "https://stat.rmc.one/api";

    public static StatRmcService getService() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(STAT_RMC_ENDPOINT)
                .setConverter(new JacksonConverter(objectMapper))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Content-Type", "application/json");
                    }
                })
                .build();
        return restAdapter.create(StatRmcService.class);
    }
}
