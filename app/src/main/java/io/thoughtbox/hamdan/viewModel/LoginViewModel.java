package io.thoughtbox.hamdan.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONObject;

import java.util.ArrayList;

import io.thoughtbox.hamdan.utls.MutableEventLiveData;
import io.thoughtbox.hamdan.model.dictionaryModel.DictionaryResponseData;
import io.thoughtbox.hamdan.model.languageModel.LanguageResponseData;
import io.thoughtbox.hamdan.model.loginModel.LoginRequestModel;
import io.thoughtbox.hamdan.model.loginModel.OtpRequestModel;
import io.thoughtbox.hamdan.repos.LoginRepo;

public class LoginViewModel extends AndroidViewModel {
    private LoginRepo loginRepo;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        loginRepo = new LoginRepo();
    }

    public void clear() {
        loginRepo.clear();
    }

    public void doLogin(LoginRequestModel params) {
        loginRepo.getLogin(params);
    }

    public void biometricLogin(JSONObject params) {
        loginRepo.biometricLogin(params);
    }

    public MutableEventLiveData<ArrayList<DictionaryResponseData>> getDictionaryLiveData() {
        return loginRepo.getDictionaryLiveData();
    }

    public void getDictionry() {
        loginRepo.getDictionary();
    }

    public void getAllLanguages() {
        loginRepo.getAllLanguages();
    }

    public MutableEventLiveData<ArrayList<LanguageResponseData>> getLanguageListLiveData() {
        return loginRepo.getLanguageListLiveData();
    }

    public void updateLanguage(JSONObject params) {
        loginRepo.updateSelectedLanguage(params);
    }

    public MutableLiveData<Boolean> getLanguageUpdateLiveData() {
        return loginRepo.getLanguageUpdateLiveData();
    }

    public MutableEventLiveData<Boolean> getIsLoading() {
        return loginRepo.getIsLoading();
    }

    public MutableEventLiveData<String> getLoadingError() {
        return loginRepo.getLoadingError();
    }


    public void validateOtp(OtpRequestModel params) {

        loginRepo.verifyOtp(params);
    }

//    public MutableEventLiveData<Boolean> isOtpValidated() {
//        return loginRepo.getIsOtpValidated();
//    }

    public void forgotPassword(String username) {
        loginRepo.forgotPassword(username);
    }

    public MutableLiveData<Boolean> getForgotPasswordLiveData() {
        return loginRepo.getForgotPasswordLiveData();
    }

    public MutableLiveData<Boolean> getIsDeviceTokenUpdated() {
        return loginRepo.getIsDeviceTokenUpdated();
    }

    public void resendOtp(OtpRequestModel params) {
        loginRepo.resendOtp(params);
    }

    public MutableLiveData<Boolean> getResendOtpLiveData() {
        return loginRepo.getResendOtpLiveData();
    }

    public MutableEventLiveData<Boolean> getIsSessionValid() {
        return loginRepo.getIsSessionValid();
    }
}
