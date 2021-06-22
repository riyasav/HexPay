package io.thoughtbox.hamdan.model.loginModel;

public class OtpRequestModel {
    String platform;
    String transfertype;
    String otp;
    String refno;

    public OtpRequestModel(String transfertype, String otp, String refno) {
        this.transfertype = transfertype;
        this.otp = otp;
        this.refno = refno;
    }

    public OtpRequestModel(String platform, String transfertype, String otp, String refno) {
        this.platform = platform;
        this.transfertype = transfertype;
        this.otp = otp;
        this.refno = refno;
    }

    public String getTransfertype() {
        return transfertype;
    }

    public void setTransfertype(String transfertype) {
        this.transfertype = transfertype;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getRefno() {
        return refno;
    }

    public void setRefno(String refno) {
        this.refno = refno;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}