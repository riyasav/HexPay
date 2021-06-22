package io.thoughtbox.hamdan.model;


public class CurrencyRateModel {
    String currencyFlag;
    String currencyCode;
    String currencyName;
    String currencyRate;
    String countryName;

    public CurrencyRateModel() {
    }

    public CurrencyRateModel(String currencyFlag, String currencyCode, String currencyName, String currencyRate, String countryName) {
        this.currencyFlag = currencyFlag;
        this.currencyCode = currencyCode;
        this.currencyName = currencyName;
        this.currencyRate = currencyRate;
        this.countryName = countryName;
    }

    public String getCurrencyFlag() {
        return currencyFlag;
    }

    public void setCurrencyFlag(String currencyFlag) {
        this.currencyFlag = currencyFlag;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getCurrencyRate() {
        return currencyRate;
    }

    public void setCurrencyRate(String currencyRate) {
        this.currencyRate = currencyRate;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}