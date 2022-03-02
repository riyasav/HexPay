package io.thoughtbox.hamdan.utls;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class AppData {
    Context context;

    public AppData(Context context) {
        this.context = context;
    }

    public void setDeviceLanguage(String lang) {
        SharedPreferences.Editor editor = context.getSharedPreferences("DeviceLanguage", MODE_PRIVATE).edit();
        editor.putString("deviceLanguage", lang);
        editor.apply();
    }

    public String getDeviceLanguage() {
        SharedPreferences sharedPref = context.getSharedPreferences("DeviceLanguage", MODE_PRIVATE);
        return sharedPref.getString("deviceLanguage", "ENGLISH");
    }

    public void setHasDefaultLanguage(boolean status) {
        SharedPreferences.Editor editor = context.getSharedPreferences("HasLanguage", MODE_PRIVATE).edit();
        editor.putBoolean("hasLanguage", status);
        editor.apply();
    }

    public boolean hasDefaultLanguage() {
        SharedPreferences sharedPref = context.getSharedPreferences("HasLanguage", MODE_PRIVATE);
        return sharedPref.getBoolean("hasLanguage", false);
    }
}
