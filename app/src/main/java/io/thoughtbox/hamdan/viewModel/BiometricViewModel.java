package io.thoughtbox.hamdan.viewModel;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONObject;

import io.thoughtbox.hamdan.utls.MutableEventLiveData;
import io.thoughtbox.hamdan.model.loginModel.OtpRequestModel;
import io.thoughtbox.hamdan.repos.BiometricRepo;
import io.thoughtbox.hamdan.repos.LoginRepo;

public class BiometricViewModel extends ViewModel {
    private BiometricRepo biometricRepo;
    private LoginRepo repo;

    public BiometricViewModel() {
        biometricRepo = new BiometricRepo();
        repo = new LoginRepo();
    }

    public void clear() {
        biometricRepo.clear();
    }

    public MutableEventLiveData<Boolean> getIsSessionValid() {
        return biometricRepo.getIsSessionValid();
    }

    public MutableEventLiveData<Boolean> getIsBiometricRegistered() {
        return biometricRepo.getIsBiometricRegistered();
    }

    public MutableEventLiveData<Boolean> getIsBiometricRemoved() {
        return biometricRepo.getIsBiometricRemoved();
    }


    public MutableEventLiveData<Boolean> getIsLoading() {
        return biometricRepo.getIsLoading();
    }

    public MutableEventLiveData<String> getLoadingError() {
        return biometricRepo.getLoadingError();
    }

    public void registerBiometric(JSONObject object) {
        biometricRepo.registerBiometric(object);
    }

    public void removeBiometric(JSONObject object) {
        biometricRepo.removeBiometric(object);
    }

    public void verifyOtp(String otp) {
        biometricRepo.verifyOtp(otp);
    }

    public MutableEventLiveData<Boolean> getIsOtpValid() {
        return biometricRepo.getIsOtpValid();
    }

    public void resendBiometricOtp(OtpRequestModel params) {
        repo.resendBiometricOtp(params);
    }

    public MutableLiveData<Boolean> getResendOtpLiveData() {
        return repo.getResendOtpLiveData();
    }
}
