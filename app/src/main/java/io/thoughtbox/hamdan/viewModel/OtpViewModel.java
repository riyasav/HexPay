package io.thoughtbox.hamdan.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import io.thoughtbox.hamdan.utls.MutableEventLiveData;
import io.thoughtbox.hamdan.model.loginModel.OtpRequestModel;
import io.thoughtbox.hamdan.repos.LoginRepo;
import io.thoughtbox.hamdan.repos.OtpRepo;

public class OtpViewModel extends AndroidViewModel {
    private OtpRepo otpRepo;
    private LoginRepo loginRepo;

    public OtpViewModel(@NonNull Application application) {
        super(application);
        otpRepo = new OtpRepo();
        loginRepo = new LoginRepo();
    }


    public void clear() {
        otpRepo.clear();
    }

    public MutableEventLiveData<Boolean> getIsLoading() {
        return otpRepo.getIsLoading();
    }

    public MutableEventLiveData<String> getLoadingError() {
        return otpRepo.getLoadingError();
    }

    public void validateOtp(OtpRequestModel requestModel) {
        otpRepo.verifyOtp(requestModel);
    }

    public MutableEventLiveData<Boolean> getOtpStatus() {
        return otpRepo.getIsOtpValidated();
    }

    public void validateTokenLessOTP(OtpRequestModel requestModel) {
        otpRepo.verifyTokenLessOtp(requestModel);
    }

    public MutableEventLiveData<Boolean> getTokenlessOtpStatus() {
        return otpRepo.getIsTokenLessOtpValid();
    }

    public void resendRegisterOtp(OtpRequestModel params) {
        loginRepo.resendRegisterOtp(params);
    }

    public void resendTransactionOtp(OtpRequestModel params) {
        loginRepo.resendBiometricOtp(params);
    }


    public MutableLiveData<Boolean> getResendOtpLiveData() {
        return loginRepo.getResendOtpLiveData();
    }


}
