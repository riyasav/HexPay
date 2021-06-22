package io.thoughtbox.hamdan.model.reportModel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ReportResponseData extends BaseObservable implements Serializable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("trxdate")
    @Expose
    private String trxdate;
    @SerializedName("trxtime")
    @Expose
    private String trxtime;
    @SerializedName("txntype")
    @Expose
    private String txntype;
    @SerializedName("refno")
    @Expose
    private String refno;
    @SerializedName("rate")
    @Expose
    private String rate;
    @SerializedName("charges")
    @Expose
    private String charges;
    @SerializedName("fcamount")
    @Expose
    private String fcamount;
    @SerializedName("fccurrency")
    @Expose
    private String fccurrency;
    @SerializedName("lccurrency")
    @Expose
    private String lccurrency;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("beneficiary")
    @Expose
    private String beneficiary;
    @SerializedName("description")
    @Expose
    private String description;

    @Bindable
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public String getTrxdate() {
        return trxdate;
    }

    public void setTrxdate(String trxdate) {
        this.trxdate = trxdate;
        notifyPropertyChanged(BR.trxdate);
    }

    @Bindable
    public String getTrxtime() {
        return trxtime;
    }

    public void setTrxtime(String trxtime) {
        this.trxtime = trxtime;
        notifyPropertyChanged(BR.trxtime);
    }

    @Bindable
    public String getTxntype() {
        return txntype;
    }

    public void setTxntype(String txntype) {
        this.txntype = txntype;
        notifyPropertyChanged(BR.txntype);
    }

    @Bindable
    public String getRefno() {
        return refno;
    }

    public void setRefno(String refno) {
        this.refno = refno;
        notifyPropertyChanged(BR.refno);
    }

    @Bindable
    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
        notifyPropertyChanged(BR.rate);
    }

    @Bindable
    public String getCharges() {
        return charges;
    }

    public void setCharges(String charges) {
        this.charges = charges;
        notifyPropertyChanged(BR.charges);
    }

    @Bindable
    public String getFcamount() {
        return fcamount;
    }

    public void setFcamount(String fcamount) {
        this.fcamount = fcamount;
        notifyPropertyChanged(BR.fcamount);
    }

    @Bindable
    public String getFccurrency() {
        return fccurrency;
    }

    public void setFccurrency(String fccurrency) {
        this.fccurrency = fccurrency;
        notifyPropertyChanged(BR.fccurrency);
    }

    @Bindable
    public String getLccurrency() {
        return lccurrency;
    }

    public void setLccurrency(String lccurrency) {
        this.lccurrency = lccurrency;
        notifyPropertyChanged(BR.lccurrency);
    }

    @Bindable
    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
        notifyPropertyChanged(BR.amount);
    }

    @Bindable
    public String getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(String beneficiary) {
        this.beneficiary = beneficiary;
        notifyPropertyChanged(BR.beneficiary);
    }

    @Bindable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        notifyPropertyChanged(BR.description);
    }


}
