package io.thoughtbox.hamdan.model.rateModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DashboardRateData {
    @SerializedName("banktransferrate")
    @Expose
    private String banktransferrate;
    @SerializedName("fastcashtransferrate")
    @Expose
    private String fastcashtransferrate;
    @SerializedName("currency")
    @Expose
    private String currency;

    public String getBanktransferrate() {
        return banktransferrate;
    }

    public void setBanktransferrate(String banktransferrate) {
        this.banktransferrate = banktransferrate;
    }

    public String getFastcashtransferrate() {
        return fastcashtransferrate;
    }

    public void setFastcashtransferrate(String fastcashtransferrate) {
        this.fastcashtransferrate = fastcashtransferrate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
