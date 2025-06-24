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
package com.mycelium.net;

import com.squareup.okhttp.OkHttpClient;

import java.net.URI;

public class HttpEndpoint {
    private final String baseUrlString;

    public HttpEndpoint(String baseUrlString) {
        this.baseUrlString = baseUrlString;
    }

    @Override
    public String toString() {
        return getBaseUrl();
    }

    public String getBaseUrl() {
        return baseUrlString;
    }

    public URI getUri(String basePath, String function) {
        return URI.create(this.getBaseUrl() + basePath + '/' + function);
    }

    public URI getUri(String function) {
        return URI.create(this.getBaseUrl() + '/' + function);
    }

    public OkHttpClient getClient() {
        return new OkHttpClient();
    }

}
