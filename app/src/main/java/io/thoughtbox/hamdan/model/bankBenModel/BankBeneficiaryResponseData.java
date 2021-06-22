package io.thoughtbox.hamdan.model.bankBenModel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BankBeneficiaryResponseData extends BaseObservable implements Serializable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("bank")
    @Expose
    private String bank;
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

    @Bindable
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
        notifyPropertyChanged(BR.bank);
    }

    @Bindable
    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
        notifyPropertyChanged(BR.branch);
    }

    @Bindable
    public String getAccountno() {
        return accountno;
    }

    public void setAccountno(String accountno) {
        this.accountno = accountno;
        notifyPropertyChanged(BR.accountno);
    }

    @Bindable
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
        notifyPropertyChanged(BR.country);
    }

    @Bindable
    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
        notifyPropertyChanged(BR.contact);
    }

    @Bindable
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
        notifyPropertyChanged(BR.currency);
    }

    @Bindable
    public String getCurrencycode() {
        return currencycode;
    }

    public void setCurrencycode(String currencycode) {
        this.currencycode = currencycode;
        notifyPropertyChanged(BR.currencycode);
    }

}
