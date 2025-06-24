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
package com.mycelium.wallet.external;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.content.res.AppCompatResources;

import com.mycelium.wallet.MbwManager;
import com.mycelium.wapi.wallet.Address;
import com.mycelium.wapi.wallet.coins.CryptoCurrency;

abstract public class BuySellServiceDescriptor {
   @StringRes
   final public int title;

   @StringRes
   final public int description;

   @StringRes
   final public int settingDescription;

   final public int icon;


   public BuySellServiceDescriptor(@StringRes int title, @StringRes int description, @StringRes int settingDescription, @DrawableRes int icon) {
      this.title = title;
      this.description = description;
      this.settingDescription = settingDescription;
      this.icon = icon;
   }

   public Drawable getIcon(Context resources){
      return AppCompatResources.getDrawable(resources, icon);
   }

   public int getDescription(MbwManager mbwManager, Address activeReceivingAddress) {
      return description;
   }

   public boolean showEnableInSettings() { return true; }

   abstract public void launchService(Activity activity, MbwManager mbwManager, Address activeReceivingAddress, CryptoCurrency cryptoCurrency);
   abstract public boolean isEnabled(MbwManager mbwManager);
   abstract public void setEnabled(MbwManager mbwManager, boolean enabledState);
}
