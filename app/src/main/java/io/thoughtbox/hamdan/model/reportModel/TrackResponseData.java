package io.thoughtbox.hamdan.model.reportModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrackResponseData {
    @SerializedName("correspondentstatus")
    @Expose
    private
    String correspondentstatus;
    @SerializedName("paymentstatus")
    @Expose
    private
    String paymentstatus;

    public String getCorrespondentstatus() {
        return correspondentstatus;
    }

    public void setCorrespondentstatus(String correspondentstatus) {
        this.correspondentstatus = correspondentstatus;
    }

    public String getPaymentstatus() {
        return paymentstatus;
    }

    public void setPaymentstatus(String paymentstatus) {
        this.paymentstatus = paymentstatus;
    }
}
