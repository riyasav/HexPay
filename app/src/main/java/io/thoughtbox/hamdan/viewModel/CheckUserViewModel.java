package io.thoughtbox.hamdan.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import io.thoughtbox.hamdan.utls.MutableEventLiveData;
import io.thoughtbox.hamdan.repos.CheckUserIdRepo;

public class CheckUserViewModel extends AndroidViewModel {
    private CheckUserIdRepo checkUserIdRepo;

    public CheckUserViewModel(@NonNull Application application) {
        super(application);
        checkUserIdRepo = new CheckUserIdRepo();
    }

    public void checkUserId(String idNumber) {
        checkUserIdRepo.checkUserId(idNumber);
    }

    public void clear() {
        checkUserIdRepo.clear();
    }

    public MutableEventLiveData<Boolean> getIsLoading() {
        return checkUserIdRepo.getIsLoading();
    }

    public MutableEventLiveData<String> getLoadingError() {
        return checkUserIdRepo.getLoadingError();
    }

    public MutableEventLiveData<Boolean> getIdStatus() {
        return checkUserIdRepo.getIsUserExist();
    }

//    public MutableEventLiveData<Boolean> getSessionStatus() {
//        return checkUserIdRepo.getIsSessionValid();
//    }

}
