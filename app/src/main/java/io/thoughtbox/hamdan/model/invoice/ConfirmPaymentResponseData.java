package io.thoughtbox.hamdan.model.invoice;

import androidx.databinding.BaseObservable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ConfirmPaymentResponseData extends BaseObservable implements Serializable {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("transactionpin")
    @Expose
    private String transactionpin;
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
    @SerializedName("sendername")
    @Expose
    private String sendername;
    @SerializedName("senderidtype")
    @Expose
    private String senderidtype;
    @SerializedName("senderidno")
    @Expose
    private String senderidno;
    @SerializedName("purposeid")
    @Expose
    private String purposeid;
    @SerializedName("purpose")
    @Expose
    private String purpose;
    @SerializedName("sourceid")
    @Expose
    private String sourceid;
    @SerializedName("source")
    @Expose
    private String source;
    @SerializedName("payementmodeid")
    @Expose
    private String payementmodeid;
    @SerializedName("payementmode")
    @Expose
    private String payementmode;
    @SerializedName("origincurrency")
    @Expose
    private String origincurrency;
    @SerializedName("origincountry")
    @Expose
    private String origincountry;
    @SerializedName("origincurrencycode")
    @Expose
    private String origincurrencycode;
    @SerializedName("accounttransferrefno")
    @Expose
    private String accounttransferrefno;
    @SerializedName("accounttransferbankid")
    @Expose
    private String accounttransferbankid;
    @SerializedName("accounttransferbank")
    @Expose
    private String accounttransferbank;
    @SerializedName("benid")
    @Expose
    private String benid;
    @SerializedName("benname")
    @Expose
    private String benname;
    @SerializedName("benaccountno")
    @Expose
    private String benaccountno;
    @SerializedName("benagent")
    @Expose
    private String benagent;
    @SerializedName("benbranch")
    @Expose
    private String benbranch;
    @SerializedName("bencurrency")
    @Expose
    private String bencurrency;
    @SerializedName("bencurrencycode")
    @Expose
    private String bencurrencycode;
    @SerializedName("bencountry")
    @Expose
    private String bencountry;
    @SerializedName("fcamount")
    @Expose
    private String fcamount;
    @SerializedName("rate")
    @Expose
    private String rate;
    @SerializedName("charges")
    @Expose
    private String charges;
    @SerializedName("lctotal")
    @Expose
    private String lctotal;
    @SerializedName("lccurrency")
    @Expose
    private String lccurrency;
    @SerializedName("isconfirmed")
    @Expose
    private String isconfirmed;
    @SerializedName("originalstatusmessage")
    @Expose
    private String originalstatusmessage;
    @SerializedName("transactionstatus")
    @Expose
    private String transactionstatus;

    @SerializedName("benaddress")
    @Expose
    private String benaddress;

    @SerializedName("contact")
    @Expose
    private String contact;

    @SerializedName("vatamount")
    @Expose
    private String vatamount;

    @SerializedName("rebate")
    @Expose
    private String rebate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransactionpin() {
        return transactionpin;
    }

    public void setTransactionpin(String transactionpin) {
        this.transactionpin = transactionpin;
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

    public String getTxntype() {
        return txntype;
    }

    public void setTxntype(String txntype) {
        this.txntype = txntype;
    }

    public String getRefno() {
        return refno;
    }

    public void setRefno(String refno) {
        this.refno = refno;
    }

    public String getSendername() {
        return sendername;
    }

    public void setSendername(String sendername) {
        this.sendername = sendername;
    }

    public String getSenderidtype() {
        return senderidtype;
    }

    public void setSenderidtype(String senderidtype) {
        this.senderidtype = senderidtype;
    }

    public String getSenderidno() {
        return senderidno;
    }

    public void setSenderidno(String senderidno) {
        this.senderidno = senderidno;
    }

    public String getPurposeid() {
        return purposeid;
    }

    public void setPurposeid(String purposeid) {
        this.purposeid = purposeid;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getSourceid() {
        return sourceid;
    }

    public void setSourceid(String sourceid) {
        this.sourceid = sourceid;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPayementmodeid() {
        return payementmodeid;
    }

    public void setPayementmodeid(String payementmodeid) {
        this.payementmodeid = payementmodeid;
    }

    public String getPayementmode() {
        return payementmode;
    }

    public void setPayementmode(String payementmode) {
        this.payementmode = payementmode;
    }

    public String getOrigincurrency() {
        return origincurrency;
    }

    public void setOrigincurrency(String origincurrency) {
        this.origincurrency = origincurrency;
    }

    public String getOrigincountry() {
        return origincountry;
    }

    public void setOrigincountry(String origincountry) {
        this.origincountry = origincountry;
    }

    public String getOrigincurrencycode() {
        return origincurrencycode;
    }

    public void setOrigincurrencycode(String origincurrencycode) {
        this.origincurrencycode = origincurrencycode;
    }

    public String getAccounttransferrefno() {
        return accounttransferrefno;
    }

    public void setAccounttransferrefno(String accounttransferrefno) {
        this.accounttransferrefno = accounttransferrefno;
    }

    public String getAccounttransferbankid() {
        return accounttransferbankid;
    }

    public void setAccounttransferbankid(String accounttransferbankid) {
        this.accounttransferbankid = accounttransferbankid;
    }

    public String getAccounttransferbank() {
        return accounttransferbank;
    }

    public void setAccounttransferbank(String accounttransferbank) {
        this.accounttransferbank = accounttransferbank;
    }

    public String getBenid() {
        return benid;
    }

    public void setBenid(String benid) {
        this.benid = benid;
    }

    public String getBenname() {
        return benname;
    }

    public void setBenname(String benname) {
        this.benname = benname;
    }

    public String getBenaccountno() {
        return benaccountno;
    }

    public void setBenaccountno(String benaccountno) {
        this.benaccountno = benaccountno;
    }

    public String getBenagent() {
        return benagent;
    }

    public void setBenagent(String benagent) {
        this.benagent = benagent;
    }

    public String getBenbranch() {
        return benbranch;
    }

    public void setBenbranch(String benbranch) {
        this.benbranch = benbranch;
    }

    public String getBencurrency() {
        return bencurrency;
    }

    public void setBencurrency(String bencurrency) {
        this.bencurrency = bencurrency;
    }

    public String getBencurrencycode() {
        return bencurrencycode;
    }

    public void setBencurrencycode(String bencurrencycode) {
        this.bencurrencycode = bencurrencycode;
    }

    public String getBencountry() {
        return bencountry;
    }

    public void setBencountry(String bencountry) {
        this.bencountry = bencountry;
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

    public String getLctotal() {
        return lctotal;
    }

    public void setLctotal(String lctotal) {
        this.lctotal = lctotal;
    }

    public String getLccurrency() {
        return lccurrency;
    }

    public void setLccurrency(String lccurrency) {
        this.lccurrency = lccurrency;
    }

    public String getIsconfirmed() {
        return isconfirmed;
    }

    public void setIsconfirmed(String isconfirmed) {
        this.isconfirmed = isconfirmed;
    }

    public String getOriginalstatusmessage() {
        return originalstatusmessage;
    }

    public void setOriginalstatusmessage(String originalstatusmessage) {
        this.originalstatusmessage = originalstatusmessage;
    }

    public String getTransactionstatus() {
        return transactionstatus;
    }

    public void setTransactionstatus(String transactionstatus) {
        this.transactionstatus = transactionstatus;
    }

    public String getBenaddress() {
        return benaddress;
    }

    public void setBenaddress(String benaddress) {
        this.benaddress = benaddress;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
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
}
