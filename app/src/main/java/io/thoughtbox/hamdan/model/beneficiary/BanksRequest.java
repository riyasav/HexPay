package io.thoughtbox.hamdan.model.beneficiary;

public class BanksRequest {
    String country;
    String transfertype;

    public BanksRequest(String country, String transfertype) {
        this.country = country;
        this.transfertype = transfertype;
    }
}
