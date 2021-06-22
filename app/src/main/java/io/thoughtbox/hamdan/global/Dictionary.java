package io.thoughtbox.hamdan.global;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.util.HashMap;

public class Dictionary extends BaseObservable {

    private static final Dictionary holder = new Dictionary();
    private HashMap<String, String> LangMap;
    private HashMap<String, String> tempLangMap;

    public static Dictionary getInstance() {
        return holder;
    }

    @Bindable
    public HashMap<String, String> getLangMap() {
        return LangMap;
    }

    public void setLangMap(HashMap<String, String> langMap) {
        LangMap = langMap;
        notifyPropertyChanged(BR.langMap);
    }

    public String get(String key) {
        return getLangMap().get(key);
    }

    @Bindable
    public HashMap<String, String> getTempLangMap() {
        return tempLangMap;
    }

    public void setTempLangMap(HashMap<String, String> tempLangMap) {
        this.tempLangMap = tempLangMap;
        notifyPropertyChanged(BR.tempLangMap);
    }

    public String getTemp(String key) {
        return getTempLangMap().get(key);
    }
}
