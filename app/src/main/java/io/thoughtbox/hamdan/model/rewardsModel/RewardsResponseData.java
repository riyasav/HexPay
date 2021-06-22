package io.thoughtbox.hamdan.model.rewardsModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RewardsResponseData {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("referralcode")
    @Expose
    private String referralcode;
    @SerializedName("referredby")
    @Expose
    private String referredby;
    @SerializedName("claimedby")
    @Expose
    private String claimedby;
    @SerializedName("txntype")
    @Expose
    private String txntype;
    @SerializedName("refno")
    @Expose
    private String refno;
    @SerializedName("serialno")
    @Expose
    private String serialno;
    @SerializedName("discountclaimed")
    @Expose
    private String discountclaimed;
    @SerializedName("createddate")
    @Expose
    private String createddate;
    @SerializedName("createdtime")
    @Expose
    private String createdtime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReferralcode() {
        return referralcode;
    }

    public void setReferralcode(String referralcode) {
        this.referralcode = referralcode;
    }

    public String getReferredby() {
        return referredby;
    }

    public void setReferredby(String referredby) {
        this.referredby = referredby;
    }

    public String getClaimedby() {
        return claimedby;
    }

    public void setClaimedby(String claimedby) {
        this.claimedby = claimedby;
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

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    public String getDiscountclaimed() {
        return discountclaimed;
    }

    public void setDiscountclaimed(String discountclaimed) {
        this.discountclaimed = discountclaimed;
    }

    public String getCreateddate() {
        return createddate;
    }

    public void setCreateddate(String createddate) {
        this.createddate = createddate;
    }

    public String getCreatedtime() {
        return createdtime;
    }

    public void setCreatedtime(String createdtime) {
        this.createdtime = createdtime;
    }
}
