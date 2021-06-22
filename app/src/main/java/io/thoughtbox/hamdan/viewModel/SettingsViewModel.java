package io.thoughtbox.hamdan.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.json.JSONObject;

import io.thoughtbox.hamdan.utls.MutableEventLiveData;
import io.thoughtbox.hamdan.model.loginModel.OtpResponsedata;
import io.thoughtbox.hamdan.repos.SettingsRepo;

public class SettingsViewModel extends AndroidViewModel {
    private SettingsRepo settingsRepo;

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        settingsRepo = new SettingsRepo();
    }

    public void clear() {
        settingsRepo.clear();
    }

    public MutableEventLiveData<Boolean> getIsLoading() {
        return settingsRepo.getIsLoading();
    }

    public MutableEventLiveData<String> getLoadingError() {
        return settingsRepo.getLoadingError();
    }

    public MutableEventLiveData<Boolean> getSessionStatus() {
        return settingsRepo.getIsSessionValid();
    }

    public void onPasswordChange(JSONObject params) {
        settingsRepo.changePassword(params);
    }

    public MutableEventLiveData<OtpResponsedata> getChangePassWordLiveData() {
        return settingsRepo.getChangePassWord();
    }

    public void onPinChange(JSONObject params) {
        settingsRepo.changePin(params);
    }

    public MutableEventLiveData<OtpResponsedata> getChangePinLiveData() {
        return settingsRepo.getChangePinLiveData();
    }

    public void block() {
        settingsRepo.blockAccount();
    }

    public MutableEventLiveData<OtpResponsedata> getBlockPinLiveData() {
        return settingsRepo.blockLiveData();
    }
}
