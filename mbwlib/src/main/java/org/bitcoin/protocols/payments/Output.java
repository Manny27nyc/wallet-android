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
// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: ./paymentrequest.proto
package org.bitcoin.protocols.payments;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;
import okio.ByteString;

import static com.squareup.wire.Message.Datatype.BYTES;
import static com.squareup.wire.Message.Datatype.UINT64;
import static com.squareup.wire.Message.Label.REQUIRED;

/**
 * Generalized form of "send payment to this/these bitcoin addresses"
 */
public final class Output extends Message {
  private static final long serialVersionUID = 0L;

  public static final Long DEFAULT_AMOUNT = 0L;
  public static final ByteString DEFAULT_SCRIPT = ByteString.EMPTY;

  @ProtoField(tag = 1, type = UINT64)
  public final Long amount;

  /**
   * amount is integer-number-of-satoshis
   */
  @ProtoField(tag = 2, type = BYTES, label = REQUIRED)
  public final ByteString script;

  public Output(Long amount, ByteString script) {
    this.amount = amount;
    this.script = script;
  }

  private Output(Builder builder) {
    this(builder.amount, builder.script);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof Output)) return false;
    Output o = (Output) other;
    return equals(amount, o.amount)
        && equals(script, o.script);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = amount != null ? amount.hashCode() : 0;
      result = result * 37 + (script != null ? script.hashCode() : 0);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<Output> {

    public Long amount;
    public ByteString script;

    public Builder() {
    }

    public Builder(Output message) {
      super(message);
      if (message == null) return;
      this.amount = message.amount;
      this.script = message.script;
    }

    public Builder amount(Long amount) {
      this.amount = amount;
      return this;
    }

    /**
     * amount is integer-number-of-satoshis
     */
    public Builder script(ByteString script) {
      this.script = script;
      return this;
    }

    @Override
    public Output build() {
      checkRequiredFields();
      return new Output(this);
    }
  }
}
