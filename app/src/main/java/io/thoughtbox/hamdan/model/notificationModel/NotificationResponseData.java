package io.thoughtbox.hamdan.model.notificationModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationResponseData {
    @SerializedName("notificationtitle")
    @Expose
    private String title;
    @SerializedName("notificationbody")
    @Expose
    private String body;
    @SerializedName("createddate")
    @Expose
    private String date;
    @SerializedName("createdtime")
    @Expose
    private String time;

    public NotificationResponseData(String title, String body, String date, String time) {
        this.title = title;
        this.body = body;
        this.date = date;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
