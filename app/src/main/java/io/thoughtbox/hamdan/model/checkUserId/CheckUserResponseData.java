package io.thoughtbox.hamdan.model.checkUserId;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckUserResponseData {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("isexist")
    @Expose
    private String isexist;
    @SerializedName("isactivates")
    @Expose
    private String isactivates;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsexist() {
        return isexist;
    }

    public void setIsexist(String isexist) {
        this.isexist = isexist;
    }

    public String getIsactivates() {
        return isactivates;
    }

    public void setIsactivates(String isactivates) {
        this.isactivates = isactivates;
    }
}
