package io.thoughtbox.hamdan.model.transferModel;

public class TransferRequestModel {
    private String platform, txntype, beneficiary, fcamount, remittancepurpose, remittanceincomesource, paymentmode, accounttrasnsferbank, accounttrasnsferrefno, costrate, rate, charges, sessionid, discountcodetype, discountcodeapplied, discountredeemed, vatamount, rebate, agentsessionid;

    public TransferRequestModel(String platform, String txntype, String beneficiary, String fcamount, String remittancepurpose, String remittanceincomesource, String paymentmode, String accounttrasnsferbank, String accounttrasnsferrefno, String costrate, String rate, String charges, String sessionid, String discountcodetype, String discountcodeapplied, String discountredeemed, String vatamount, String rebate, String agentsessionid) {
        this.platform = platform;
        this.txntype = txntype;
        this.beneficiary = beneficiary;
        this.fcamount = fcamount;
        this.remittancepurpose = remittancepurpose;
        this.remittanceincomesource = remittanceincomesource;
        this.paymentmode = paymentmode;
        this.accounttrasnsferbank = accounttrasnsferbank;
        this.accounttrasnsferrefno = accounttrasnsferrefno;
        this.costrate = costrate;
        this.rate = rate;
        this.charges = charges;
        this.sessionid = sessionid;
        this.discountcodetype = discountcodetype;
        this.discountcodeapplied = discountcodeapplied;
        this.discountredeemed = discountredeemed;
        this.vatamount = vatamount;
        this.rebate = rebate;
        this.agentsessionid = agentsessionid;
    }
}
