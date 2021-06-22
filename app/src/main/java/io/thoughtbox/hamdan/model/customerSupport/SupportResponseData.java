package io.thoughtbox.hamdan.model.customerSupport;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SupportResponseData {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("starttime")
    @Expose
    private String starttime;
    @SerializedName("endtime")
    @Expose
    private String endtime;
    @SerializedName("contactno")
    @Expose
    private String contactno;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getContactno() {
        return contactno;
    }

    public void setContactno(String contactno) {
        this.contactno = contactno;
    }

}
