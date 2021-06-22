package io.thoughtbox.hamdan.model.rateModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RateResponseData {
    @SerializedName("txntype")
    @Expose
    private String txntype;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("userrate")
    @Expose
    private String userrate;
    @SerializedName("charges")
    @Expose
    private String charges;

    @SerializedName("countryflag")
    @Expose
    private String countryflag;

    @SerializedName("currencycode")
    @Expose
    private String currencycode;



    public String getTxntype() {
        return txntype;
    }

    public void setTxntype(String txntype) {
        this.txntype = txntype;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getUserrate() {
        return userrate;
    }

    public void setUserrate(String userrate) {
        this.userrate = userrate;
    }

    public String getCharges() {
        return charges;
    }

    public void setCharges(String charges) {
        this.charges = charges;
    }

    public String getCountryflag() {
        return countryflag;
    }

    public void setCountryflag(String countryflag) {
        this.countryflag = countryflag;
    }

    public String getCurrencycode() {
        return currencycode;
    }

    public void setCurrencycode(String currencycode) {
        this.currencycode = currencycode;
    }
}
