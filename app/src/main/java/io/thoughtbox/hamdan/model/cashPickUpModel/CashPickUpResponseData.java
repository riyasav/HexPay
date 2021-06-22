package io.thoughtbox.hamdan.model.cashPickUpModel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CashPickUpResponseData extends BaseObservable implements Serializable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("agent")
    @Expose
    private String agent;
    @SerializedName("agentbranch")
    @Expose
    private String agentbranch;
    @SerializedName("modeoftransfer")
    @Expose
    private String modeoftransfer;
    @SerializedName("transfermode")
    @Expose
    private String transfermode;
    @SerializedName("address")
    @Expose
    private String address;
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
    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
        notifyPropertyChanged(BR.agent);
    }

    @Bindable
    public String getAgentbranch() {
        return agentbranch;
    }

    public void setAgentbranch(String agentbranch) {
        this.agentbranch = agentbranch;
        notifyPropertyChanged(BR.agentbranch);
    }

    @Bindable
    public String getModeoftransfer() {
        return modeoftransfer;
    }

    public void setModeoftransfer(String modeoftransfer) {
        this.modeoftransfer = modeoftransfer;
        notifyPropertyChanged(BR.modeoftransfer);
    }

    @Bindable
    public String getTransfermode() {
        return transfermode;
    }

    public void setTransfermode(String transfermode) {
        this.transfermode = transfermode;
        notifyPropertyChanged(BR.transfermode);
    }

    @Bindable
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(BR.address);
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
