package io.thoughtbox.hamdan.model.transferModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransferResponseData {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("serialno")
    @Expose
    private String serialno;
    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("responsedata")
    @Expose
    private String responsedata;
    @SerializedName("trxdate")
    @Expose
    private String trxdate;
    @SerializedName("trxtime")
    @Expose
    private String trxtime;
    @SerializedName("beneficiary")
    @Expose
    private String beneficiary;
    @SerializedName("bennationality")
    @Expose
    private String bennationality;
    @SerializedName("bencountry")
    @Expose
    private String bencountry;
    @SerializedName("bencurrency")
    @Expose
    private String bencurrency;
    @SerializedName("fcamount")
    @Expose
    private String fcamount;
    @SerializedName("rate")
    @Expose
    private String rate;
    @SerializedName("charges")
    @Expose
    private String charges;
    @SerializedName("lcamount")
    @Expose
    private String lcamount;
    @SerializedName("pinmode")
    @Expose
    private String pinmode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResponsedata() {
        return responsedata;
    }

    public void setResponsedata(String responsedata) {
        this.responsedata = responsedata;
    }

    public String getTrxdate() {
        return trxdate;
    }

    public void setTrxdate(String trxdate) {
        this.trxdate = trxdate;
    }

    public String getTrxtime() {
        return trxtime;
    }

    public void setTrxtime(String trxtime) {
        this.trxtime = trxtime;
    }

    public String getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(String beneficiary) {
        this.beneficiary = beneficiary;
    }

    public String getBennationality() {
        return bennationality;
    }

    public void setBennationality(String bennationality) {
        this.bennationality = bennationality;
    }

    public String getBencountry() {
        return bencountry;
    }

    public void setBencountry(String bencountry) {
        this.bencountry = bencountry;
    }

    public String getBencurrency() {
        return bencurrency;
    }

    public void setBencurrency(String bencurrency) {
        this.bencurrency = bencurrency;
    }

    public String getFcamount() {
        return fcamount;
    }

    public void setFcamount(String fcamount) {
        this.fcamount = fcamount;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getCharges() {
        return charges;
    }

    public void setCharges(String charges) {
        this.charges = charges;
    }

    public String getLcamount() {
        return lcamount;
    }

    public void setLcamount(String lcamount) {
        this.lcamount = lcamount;
    }

    public String getPinmode() {
        return pinmode;
    }

    public void setPinmode(String pinmode) {
        this.pinmode = pinmode;
    }
}
