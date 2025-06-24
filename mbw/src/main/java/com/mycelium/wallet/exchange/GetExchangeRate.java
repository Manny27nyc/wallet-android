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
package com.mycelium.wallet.exchange;

import static com.mycelium.wallet.Utils.isERC20Token;

import com.mycelium.wallet.Utils;
import com.mycelium.wapi.model.ExchangeRate;
import com.mycelium.wapi.wallet.WalletManager;
import com.mycelium.wapi.wallet.btc.coins.BitcoinMain;
import com.mycelium.wapi.wallet.btc.coins.BitcoinTest;
import com.mycelium.wapi.wallet.btcvault.coins.BitcoinVaultMain;
import com.mycelium.wapi.wallet.btcvault.coins.BitcoinVaultTest;
import com.mycelium.wapi.wallet.coins.AssetInfo;
import com.mycelium.wapi.wallet.coins.Value;
import com.mycelium.wapi.wallet.currency.ExchangeRateProvider;
import com.mycelium.wapi.wallet.eth.coins.EthMain;
import com.mycelium.wapi.wallet.eth.coins.EthTest;
import com.mycelium.wapi.wallet.fio.coins.FIOMain;
import com.mycelium.wapi.wallet.fio.coins.FIOTest;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class GetExchangeRate {
    private String targetCurrency;
    private String sourceCurrency;
    private ExchangeRateProvider exchangeRateManager;
    private WalletManager walletManager;
    private BigDecimal sourcePrice;
    private BigDecimal targetPrice;
    private ExchangeRate sourceExchangeRate;
    private ExchangeRate targetExchangeRate;

    public GetExchangeRate(WalletManager walletManager, String targetCurrency, String sourceCurrency, ExchangeRateProvider exchangeRateManager) {
        this.targetCurrency = targetCurrency;
        this.sourceCurrency = sourceCurrency;
        this.exchangeRateManager = exchangeRateManager;
        this.walletManager = walletManager;
    }

    // multiply the source value by this rate, to get the target value
    public BigDecimal getRate() {
        if (getTargetPrice() == null || getSourcePrice() == null || BigDecimal.ZERO.equals(getSourcePrice())) {
            return null;
        }
        return getTargetPrice().divide(getSourcePrice(), 10, RoundingMode.HALF_UP);
    }

    public BigDecimal getSourcePrice() {
        return sourcePrice;
    }

    public BigDecimal getTargetPrice() {
        return targetPrice;
    }

    public ExchangeRate getSourceExchangeRate() {
        return sourceExchangeRate;
    }

    public ExchangeRate getTargetExchangeRate() {
        return targetExchangeRate;
    }

    public GetExchangeRate invoke() {
        sourcePrice = null;
        targetPrice = null;
        sourceExchangeRate = null;
        targetExchangeRate = null;

        if (isBtc(sourceCurrency) || isEth(sourceCurrency) || isFio(sourceCurrency) || isBtcv(sourceCurrency)
                || (isERC20Token(walletManager, sourceCurrency) && !isEth(targetCurrency))) {
            sourcePrice = BigDecimal.ONE;
        } else {
            sourceExchangeRate = exchangeRateManager.getExchangeRate(targetCurrency, sourceCurrency);
            if (sourceExchangeRate != null && sourceExchangeRate.price != null) {
                sourcePrice = BigDecimal.valueOf(sourceExchangeRate.price);
            }
        }

        if (isBtc(targetCurrency) || isEth(targetCurrency) || isFio(targetCurrency) || isBtcv(targetCurrency)
                || isERC20Token(walletManager, targetCurrency)) {
            targetPrice = BigDecimal.ONE;
        } else {
            targetExchangeRate = exchangeRateManager.getExchangeRate(sourceCurrency, targetCurrency);
            if (targetExchangeRate != null && targetExchangeRate.price != null) {
                targetPrice = BigDecimal.valueOf(targetExchangeRate.price);
            }
        }
        if(BigDecimal.ONE.equals(sourcePrice) && BigDecimal.ONE.equals(targetPrice)) {
            priceAccross("USD");
        } else if (sourcePrice == null && targetPrice == null) {
            priceAccross(Utils.getBtcCoinType().getSymbol());
        }
        return this;
    }

    private void priceAccross(String coin) {
        sourceExchangeRate = exchangeRateManager.getExchangeRate(coin, sourceCurrency);
        if (sourceExchangeRate != null && sourceExchangeRate.price != null) {
            sourcePrice = BigDecimal.valueOf(sourceExchangeRate.price);
        } else {
            sourceExchangeRate = exchangeRateManager.getExchangeRate(sourceCurrency, coin);
            if (sourceExchangeRate != null && sourceExchangeRate.price != null) {
                sourcePrice = BigDecimal.ONE.divide(BigDecimal.valueOf(sourceExchangeRate.price), 10, RoundingMode.HALF_UP);
            }
        }

        targetExchangeRate = exchangeRateManager.getExchangeRate(coin, targetCurrency);
        if (targetExchangeRate != null && targetExchangeRate.price != null) {
            targetPrice = BigDecimal.valueOf(targetExchangeRate.price);
        } else {
            targetExchangeRate = exchangeRateManager.getExchangeRate(targetCurrency, coin);
            if (targetExchangeRate != null && targetExchangeRate.price != null) {
                targetPrice = BigDecimal.ONE.divide(BigDecimal.valueOf(targetExchangeRate.price), 10, RoundingMode.HALF_UP);
            }
        }
    }

    public Value getValue(Value value, AssetInfo toCurrency) {
        BigDecimal rateValue = getRate();
        if (rateValue != null) {
            BigDecimal bigDecimal = rateValue.multiply(new BigDecimal(value.value))
                    .movePointLeft(value.type.getUnitExponent())
                    .round(MathContext.DECIMAL128);
            return Value.parse(toCurrency, bigDecimal);
        } else {
            return null;
        }
    }

    public boolean isRateOld() {
        Date current = new Date();
        return (sourceExchangeRate != null && TimeUnit.MILLISECONDS.toHours(current.getTime() - sourceExchangeRate.time) >= 24) ||
                (targetExchangeRate != null && TimeUnit.MILLISECONDS.toHours(current.getTime() - targetExchangeRate.time) >= 24);
    }

    private boolean isBtc(String currencySymbol) {
        return currencySymbol.equals(BitcoinMain.get().getSymbol()) || currencySymbol.equals(BitcoinTest.get().getSymbol());
    }

    private boolean isBtcv(String currencySymbol) {
        return currencySymbol.equals(BitcoinVaultMain.INSTANCE.getSymbol()) || currencySymbol.equals(BitcoinVaultTest.INSTANCE.getSymbol());
    }

    private boolean isEth(String currencySymbol) {
        return currencySymbol.equals(EthMain.INSTANCE.getSymbol()) || currencySymbol.equals(EthTest.INSTANCE.getSymbol());
    }

    private boolean isFio(String currencySymbol) {
        return currencySymbol.equals(FIOMain.INSTANCE.getSymbol()) || currencySymbol.equals(FIOTest.INSTANCE.getSymbol());
    }
}
