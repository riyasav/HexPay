package io.thoughtbox.hamdan.model.dictionaryModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DictionaryResponseData {

    @SerializedName("item")
    @Expose
    private String item;
    @SerializedName("value")
    @Expose
    private String value;

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}