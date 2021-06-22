package io.thoughtbox.hamdan.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

public class CurrencyModel  extends BaseObservable {
    String fcAmount;
    String lcAmount;

    public CurrencyModel(String fcAmount, String lcAmount) {
        this.fcAmount = fcAmount;
        this.lcAmount = lcAmount;
    }

    @Bindable
    public String getFcAmount() {
        return fcAmount;
    }

    public void setFcAmount(String fcAmount) {
        this.fcAmount = fcAmount;
        notifyPropertyChanged(BR.fcAmount);
    }

    @Bindable
    public String getLcAmount() {
        return lcAmount;
    }

    public void setLcAmount(String lcAmount) {
        this.lcAmount = lcAmount;
        notifyPropertyChanged(BR.lcAmount);
    }
}