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
package com.mycelium.wapi.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.net.URI;

public class FeatureWarning implements Serializable {
   @JsonProperty
   public Feature feature;

   @JsonProperty
   public WarningKind warningKind;

   @JsonProperty
   public String warningMessage;

   @JsonProperty
   public URI link;


   public FeatureWarning(@JsonProperty("feature") Feature feature,
                         @JsonProperty("warningKind") WarningKind warningKind,
                         @JsonProperty("warningMessage") String warningMessage,
                         @JsonProperty("link") URI link) {
      this.feature = feature;
      this.warningKind = warningKind;
      this.warningMessage = warningMessage;
      this.link = link;
   }
}
