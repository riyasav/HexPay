package io.thoughtbox.hamdan.global;

import android.app.Application;
import android.content.Context;

import io.thoughtbox.hamdan.model.loginModel.LoginResponsedata;

public class Universal extends Application {

    public static Universal appInstance;
    LoginResponsedata loginResponsedata;
    Context context;
    String idNumber;

    public Universal() {
        appInstance = this;
    }

    public static Universal getInstance() {
        return appInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public LoginResponsedata getLoginResponsedata() {
        return loginResponsedata;
    }

    public void setLoginResponsedata(LoginResponsedata loginResponsedata) {
        this.loginResponsedata = loginResponsedata;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }
}
