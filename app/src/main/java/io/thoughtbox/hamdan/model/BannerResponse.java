package io.thoughtbox.hamdan.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BannerResponse {
    @SerializedName("hasmessage")
    @Expose
    private String hasmessage;
    @SerializedName("message")
    @Expose
    private String message;


    public BannerResponse(String hasmessage, String message) {
        super();
        this.hasmessage = hasmessage;
        this.message = message;
    }

    public String getHasmessage() {
        return hasmessage;
    }

    public void setHasmessage(String hasmessage) {
        this.hasmessage = hasmessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
