package io.thoughtbox.hamdan.model.invoice;

public class InvoiceRequestModel {
    String txntype;
    String refno;

    public InvoiceRequestModel(String txntype, String refno) {
        this.txntype = txntype;
        this.refno = refno;
    }
}
