package io.thoughtbox.hamdan.fragments.rates;

import io.thoughtbox.hamdan.model.rateModels.RateResponseData;

public interface CurrencyRatesListener {
    void onCurrencyListClickListener(RateResponseData data);
}

