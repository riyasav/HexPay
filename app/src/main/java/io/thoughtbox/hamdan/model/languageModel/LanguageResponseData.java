package io.thoughtbox.hamdan.model.languageModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LanguageResponseData {
    @SerializedName("id")
    @Expose
    String id;
    @SerializedName("name")
    @Expose
    String name;
    @SerializedName("text")
    @Expose
    String text;

    public LanguageResponseData(String id, String name, String text) {
        this.id = id;
        this.name = name;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
