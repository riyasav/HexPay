package io.thoughtbox.hamdan.model.loginModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OtpResponsedata {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("refno")
    @Expose
    private String refno;
    @SerializedName("refvalue")
    @Expose
    private String refvalue;
    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("responsedata")
    @Expose
    private String responsedata;
    @SerializedName("pinmode")
    @Expose
    private String pinmode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRefno() {
        return refno;
    }

    public void setRefno(String refno) {
        this.refno = refno;
    }

    public String getRefvalue() {
        return refvalue;
    }

    public void setRefvalue(String refvalue) {
        this.refvalue = refvalue;
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

    public String getPinmode() {
        return pinmode;
    }

    public void setPinmode(String pinmode) {
        this.pinmode = pinmode;
    }

}