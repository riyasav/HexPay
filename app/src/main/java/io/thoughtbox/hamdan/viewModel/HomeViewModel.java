package io.thoughtbox.hamdan.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.io.File;
import java.util.ArrayList;

import io.thoughtbox.hamdan.model.DateTimeResponseDate;
import io.thoughtbox.hamdan.model.profile.ProfileResponseData;
import io.thoughtbox.hamdan.model.transferModel.SelectionModal;
import io.thoughtbox.hamdan.utls.MutableEventLiveData;
import io.thoughtbox.hamdan.model.rateModels.RateResponseData;
import io.thoughtbox.hamdan.repos.HomeRepo;

public class HomeViewModel  extends AndroidViewModel {
    private HomeRepo homeRepo;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        homeRepo = new HomeRepo();
    }


    public void clear() {
        homeRepo.clear();
    }

    public MutableEventLiveData<Boolean> getIsLoading() {
        return homeRepo.getIsLoading();
    }

    public MutableEventLiveData<String> getLoadingError() {
        return homeRepo.getLoadingError();
    }

    public void getUserRate() {
        homeRepo.getUserRate();
    }

    public MutableEventLiveData<ArrayList<RateResponseData>> getRateData() {
        return homeRepo.getRateLiveData();
    }

    public MutableEventLiveData<Boolean> getSessionStatus() {
        return homeRepo.getIsSessionValid();
    }

    public void getSupportNumber() {
        homeRepo.getCustomerCareNumber();
    }

    public MutableEventLiveData<String> getContactLiveData() {
        return homeRepo.getContactLiveData();
    }

    public void getDashboardRate() {
        homeRepo.getDashBoardRate();
    }

    public MutableEventLiveData<String[]> getDashBoardRateData() {
        return homeRepo.getDashboardRateLiveData();
    }

    public void getProfile() {
        homeRepo.getProfileData();
    }

    public MutableEventLiveData<ProfileResponseData> getProfileLiveData() {
        return homeRepo.getProfileLiveData();
    }
    public void getTokenLessUserRate() {
        homeRepo.getTokenLessUserRate();
    }

    public MutableEventLiveData<ArrayList<RateResponseData>> getTokenLessRateLiveData() {
        return  homeRepo.getTokenLessRateLiveData();
    }


    public void getIDFile(String id) {
        homeRepo.getIDFile(id);
    }

    public void getProfilePic(String id) {
        homeRepo.getProfilePic(id);
    }


    public MutableEventLiveData<String> getIdImage() {
        return homeRepo.getIdImage();
    }

    public void getDate() {
        homeRepo.getCurrentDate();
    }

    public MutableEventLiveData<DateTimeResponseDate> getDateTimeLiveData() {
        return homeRepo.getDateTimeLiveData();
    }


    public void getProffessions() {
        homeRepo.getProffessions();
    }

    public MutableEventLiveData<ArrayList<SelectionModal>> getProffessionLivedata() {
        return homeRepo.getProffessionLivedata();
    }


    public MutableEventLiveData<ArrayList<SelectionModal>> getSalaryLiveData() {
        return homeRepo.getSalaryLiveData();
    }

    public void getSalaryData() {
        homeRepo.getSalary();
    }

    public void getEmployerType() {
        homeRepo.getEmployerType();
    }

    public MutableEventLiveData<ArrayList<SelectionModal>> getEmployerTypeLiveData() {
        return homeRepo.getEmployerTypeLiveData();
    }

    public void uploadProfilePic(File pic) {
        homeRepo.uploadProfilePicFile(pic);
    }

    public MutableEventLiveData<Boolean> getIsProfilePicUpdated() {
        return homeRepo.getIsProfilePicUpdated();
    }

    public MutableEventLiveData<String> getDpImage() {
        return homeRepo.getDpImage();
    }
}
