package io.thoughtbox.hamdan.model.beneficiary;

public class BranchRequest {
    String remitbank;
    String bencountry;
    String searchkey;
    String isonline;
    String transfertype;

    public BranchRequest(String remitbank, String bencountry, String searchkey, String isonline, String transfertype) {
        this.remitbank = remitbank;
        this.bencountry = bencountry;
        this.searchkey = searchkey;
        this.isonline = isonline;
        this.transfertype = transfertype;
    }
}
