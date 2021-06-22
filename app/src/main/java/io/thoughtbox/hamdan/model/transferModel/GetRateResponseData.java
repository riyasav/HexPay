package io.thoughtbox.hamdan.model.transferModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetRateResponseData {

    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("costrate")
    @Expose
    private String costrate;
    @SerializedName("userrate")
    @Expose
    private String userrate;
    @SerializedName("minrate")
    @Expose
    private String minrate;
    @SerializedName("maxrate")
    @Expose
    private String maxrate;
    @SerializedName("returnate")
    @Expose
    private String returnate;
    @SerializedName("servicecharge")
    @Expose
    private String servicecharge;
    @SerializedName("backendcharge")
    @Expose
    private String backendcharge;
    @SerializedName("servicechargebenefit")
    @Expose
    private String servicechargebenefit;
    @SerializedName("exchangeratebenefit")
    @Expose
    private String exchangeratebenefit;
    @SerializedName("loyaltypointspertransaaction")
    @Expose
    private String loyaltypointspertransaaction;
    @SerializedName("discountcodetype")
    @Expose
    private String discountcodetype;
    @SerializedName("discountcodestatus")
    @Expose
    private String discountcodestatus;
    @SerializedName("discountcodemessage")
    @Expose
    private String discountcodemessage;
    @SerializedName("discountcodeapplied")
    @Expose
    private String discountcodeapplied;
    @SerializedName("discountredeemed")
    @Expose
    private String discountredeemed;
    @SerializedName("sessionid")
    @Expose
    private String sessionid;

    @SerializedName("vatamount")
    @Expose
    private String vatamount;

    @SerializedName("rebate")
    @Expose
    private String rebate;

    @SerializedName("agentsessionid")
    @Expose
    private String agentsessionid;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCostrate() {
        return costrate;
    }

    public void setCostrate(String costrate) {
        this.costrate = costrate;
    }

    public String getUserrate() {
        return userrate;
    }

    public void setUserrate(String userrate) {
        this.userrate = userrate;
    }

    public String getMinrate() {
        return minrate;
    }

    public void setMinrate(String minrate) {
        this.minrate = minrate;
    }

    public String getMaxrate() {
        return maxrate;
    }

    public void setMaxrate(String maxrate) {
        this.maxrate = maxrate;
    }

    public String getReturnate() {
        return returnate;
    }

    public void setReturnate(String returnate) {
        this.returnate = returnate;
    }

    public String getServicecharge() {
        return servicecharge;
    }

    public void setServicecharge(String servicecharge) {
        this.servicecharge = servicecharge;
    }

    public String getBackendcharge() {
        return backendcharge;
    }

    public void setBackendcharge(String backendcharge) {
        this.backendcharge = backendcharge;
    }

    public String getServicechargebenefit() {
        return servicechargebenefit;
    }

    public void setServicechargebenefit(String servicechargebenefit) {
        this.servicechargebenefit = servicechargebenefit;
    }

    public String getExchangeratebenefit() {
        return exchangeratebenefit;
    }

    public void setExchangeratebenefit(String exchangeratebenefit) {
        this.exchangeratebenefit = exchangeratebenefit;
    }

    public String getLoyaltypointspertransaaction() {
        return loyaltypointspertransaaction;
    }

    public void setLoyaltypointspertransaaction(String loyaltypointspertransaaction) {
        this.loyaltypointspertransaaction = loyaltypointspertransaaction;
    }

    public String getDiscountcodetype() {
        return discountcodetype;
    }

    public void setDiscountcodetype(String discountcodetype) {
        this.discountcodetype = discountcodetype;
    }

    public String getDiscountcodestatus() {
        return discountcodestatus;
    }

    public void setDiscountcodestatus(String discountcodestatus) {
        this.discountcodestatus = discountcodestatus;
    }

    public String getDiscountcodemessage() {
        return discountcodemessage;
    }

    public void setDiscountcodemessage(String discountcodemessage) {
        this.discountcodemessage = discountcodemessage;
    }

    public String getDiscountcodeapplied() {
        return discountcodeapplied;
    }

    public void setDiscountcodeapplied(String discountcodeapplied) {
        this.discountcodeapplied = discountcodeapplied;
    }

    public String getDiscountredeemed() {
        return discountredeemed;
    }

    public void setDiscountredeemed(String discountredeemed) {
        this.discountredeemed = discountredeemed;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getVatamount() {
        return vatamount;
    }

    public void setVatamount(String vatamount) {
        this.vatamount = vatamount;
    }

    public String getRebate() {
        return rebate;
    }

    public void setRebate(String rebate) {
        this.rebate = rebate;
    }

    public String getAgentsessionid() {
        return agentsessionid;
    }

    public void setAgentsessionid(String agentsessionid) {
        this.agentsessionid = agentsessionid;
    }
}
