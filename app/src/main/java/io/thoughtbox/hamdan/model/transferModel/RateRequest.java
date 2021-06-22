package io.thoughtbox.hamdan.model.transferModel;

public class RateRequest {
    private String txntype;
    private String beneficiary;
    private String fcamount;
    private String lcamount;
    private String isonline;
    private String discountcode;

    public RateRequest(String txntype, String beneficiary, String fcamount, String lcamount, String isonline, String discountcode) {

        this.txntype = txntype;
        this.beneficiary = beneficiary;
        this.fcamount = fcamount;
        this.lcamount = lcamount;
        this.isonline = isonline;
        this.discountcode = discountcode;
    }

    public String getTxntype() {
        return txntype;
    }

    public void setTxntype(String txntype) {
        this.txntype = txntype;
    }

    public String getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(String beneficiary) {
        this.beneficiary = beneficiary;
    }

    public String getFcamount() {
        return fcamount;
    }

    public void setFcamount(String fcamount) {
        this.fcamount = fcamount;
    }

    public String getLcamount() {
        return lcamount;
    }

    public void setLcamount(String lcamount) {
        this.lcamount = lcamount;
    }

    public String getIsonline() {
        return isonline;
    }

    public void setIsonline(String isonline) {
        this.isonline = isonline;
    }

    public String getDiscountcode() {
        return discountcode;
    }

    public void setDiscountcode(String discountcode) {
        this.discountcode = discountcode;
    }
}
