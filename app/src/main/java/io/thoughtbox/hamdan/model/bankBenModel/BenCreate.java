package io.thoughtbox.hamdan.model.bankBenModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BenCreate {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("responsedata")
    @Expose
    private String responsedata;
    @SerializedName("pinmode")
    @Expose
    private String pinmode;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("agent")
    @Expose
    private String agent;
    @SerializedName("branch")
    @Expose
    private String branch;
    @SerializedName("accountno")
    @Expose
    private String accountno;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("contact")
    @Expose
    private String contact;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("currencycode")
    @Expose
    private String currencycode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getAccountno() {
        return accountno;
    }

    public void setAccountno(String accountno) {
        this.accountno = accountno;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencycode() {
        return currencycode;
    }

    public void setCurrencycode(String currencycode) {
        this.currencycode = currencycode;
    }

}
