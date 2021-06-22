
package io.thoughtbox.hamdan.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class FileUploadResponse {

    @Expose
    @SerializedName("renewedtoken")
    private String mRenewedtoken;
    @Expose
    @SerializedName("responsecode")
    private String mResponsecode;
    @Expose
    @SerializedName("responsedata")
    private Boolean mResponsedata;
    @Expose
    @SerializedName("responsedescription")
    private String mResponsedescription;
    @Expose
    @SerializedName("responsestatus")
    private String mResponsestatus;
    @Expose
    @SerializedName("tokenstatus")
    private String mTokenstatus;

    public String getRenewedtoken() {
        return mRenewedtoken;
    }

    public void setRenewedtoken(String renewedtoken) {
        mRenewedtoken = renewedtoken;
    }

    public String getResponsecode() {
        return mResponsecode;
    }

    public void setResponsecode(String responsecode) {
        mResponsecode = responsecode;
    }

    public Boolean getResponsedata() {
        return mResponsedata;
    }

    public void setResponsedata(Boolean responsedata) {
        mResponsedata = responsedata;
    }

    public String getResponsedescription() {
        return mResponsedescription;
    }

    public void setResponsedescription(String responsedescription) {
        mResponsedescription = responsedescription;
    }

    public String getResponsestatus() {
        return mResponsestatus;
    }

    public void setResponsestatus(String responsestatus) {
        mResponsestatus = responsestatus;
    }

    public String getTokenstatus() {
        return mTokenstatus;
    }

    public void setTokenstatus(String tokenstatus) {
        mTokenstatus = tokenstatus;
    }

}
