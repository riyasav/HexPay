package io.thoughtbox.hamdan.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BannerModel {
    @SerializedName("tokenstatus")
    @Expose
    private String tokenstatus;
    @SerializedName("renewedtoken")
    @Expose
    private String renewedtoken;
    @SerializedName("responsedata")
    @Expose
    private BannerResponse responsedata;
    @SerializedName("responsestatus")
    @Expose
    private String responsestatus;
    @SerializedName("responsecode")
    @Expose
    private String responsecode;
    @SerializedName("responsedescription")
    @Expose
    private String responsedescription;


    public BannerModel(String tokenstatus, String renewedtoken, BannerResponse responsedata, String responsestatus, String responsecode, String responsedescription) {
        super();
        this.tokenstatus = tokenstatus;
        this.renewedtoken = renewedtoken;
        this.responsedata = responsedata;
        this.responsestatus = responsestatus;
        this.responsecode = responsecode;
        this.responsedescription = responsedescription;
    }

    public String getTokenstatus() {
        return tokenstatus;
    }

    public void setTokenstatus(String tokenstatus) {
        this.tokenstatus = tokenstatus;
    }

    public String getRenewedtoken() {
        return renewedtoken;
    }

    public void setRenewedtoken(String renewedtoken) {
        this.renewedtoken = renewedtoken;
    }

    public BannerResponse getResponsedata() {
        return responsedata;
    }

    public void setResponsedata(BannerResponse responsedata) {
        this.responsedata = responsedata;
    }

    public String getResponsestatus() {
        return responsestatus;
    }

    public void setResponsestatus(String responsestatus) {
        this.responsestatus = responsestatus;
    }

    public String getResponsecode() {
        return responsecode;
    }

    public void setResponsecode(String responsecode) {
        this.responsecode = responsecode;
    }

    public String getResponsedescription() {
        return responsedescription;
    }

    public void setResponsedescription(String responsedescription) {
        this.responsedescription = responsedescription;
    }
}
