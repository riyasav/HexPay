package io.thoughtbox.hamdan.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DateTimeResponseDate {
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("time")
    @Expose
    private String time;

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