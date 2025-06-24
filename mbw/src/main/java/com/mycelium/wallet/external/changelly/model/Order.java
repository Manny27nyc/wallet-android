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
package com.mycelium.wallet.external.changelly.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class Order {
    @JsonProperty("exchange_provider")
    public String provider = "Changelly";
    @JsonProperty("exchange_rate")
    public String rate;
    @JsonProperty("exchanging_amount")
    public String exchangingAmount;
    @JsonProperty("exchanging_currency")
    public String exchangingCurrency;
    @JsonProperty("receiving_address")
    public String receivingAddress;
    @JsonProperty("receiving_amount")
    public String receivingAmount;
    @JsonProperty("receiving_currency")
    public String receivingCurrency;
    @JsonProperty("timestamp")
    public String timestamp;
    @JsonProperty("tx_id")
    public String transactionId;
    @JsonProperty("order_id")
    public String order_id;
}
