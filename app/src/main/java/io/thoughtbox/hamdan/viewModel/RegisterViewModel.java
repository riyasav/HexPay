package io.thoughtbox.hamdan.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.io.File;
import java.util.ArrayList;

import io.thoughtbox.hamdan.model.DateTimeResponseDate;
import io.thoughtbox.hamdan.utls.MutableEventLiveData;
import io.thoughtbox.hamdan.model.bankBenModel.CreateBenResponseData;
import io.thoughtbox.hamdan.model.transferModel.SelectionModal;
import io.thoughtbox.hamdan.repos.RegisterRepo;

public class RegisterViewModel extends AndroidViewModel {
    RegisterRepo registerRepo;

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        registerRepo = new RegisterRepo();
    }

    public void clear() {
        registerRepo.clear();
    }

    public MutableEventLiveData<Boolean> getIsLoading() {
        return registerRepo.getIsLoading();
    }

    public MutableEventLiveData<String> getLoadingError() {
        return registerRepo.getLoadingError();
    }

    public MutableEventLiveData<ArrayList<SelectionModal>> getCountriesLiveData() {
        return registerRepo.getCountryLiveData();
    }

    public void getCountries() {
        registerRepo.getCountry();
    }

    public MutableEventLiveData<ArrayList<SelectionModal>> getIdTypeLiveData() {
        return registerRepo.getIdTypeLiveData();
    }

    public void getIdtype() {
        registerRepo.getidType();
    }

    public MutableEventLiveData<ArrayList<SelectionModal>> getProffessionLivedata() {
        return registerRepo.getProffessionLivedata();
    }

    public void getProffessions() {
        registerRepo.getProffessions();
    }

    public MutableEventLiveData<ArrayList<SelectionModal>> getSalaryLiveData() {
        return registerRepo.getSalaryLiveData();
    }

    public void getSalaryData() {
        registerRepo.getSalary();
    }

    public void createRegister(File idfile0,
                               File idfile1,
                               File video,
                               File signature,
                               String fname,
                               String mname,
                               String lname,
                               String mobile,
                               String email,
                               String city,
                               String state,
                               String country,
                               String idtype,
                               String idnum,
                               String idexpiry,
                               String salary,
                               String profession,
                               String employer
    ) {
        registerRepo.createRegister(
                idfile0,
                idfile1,
                video,
                signature,
                fname,
                mname,
                lname,
                mobile,
                email,
                city,
                state,
                country,
                idtype,
                idnum,
                idexpiry,
                salary,
                profession,
                employer
        );
    }

    public MutableEventLiveData<CreateBenResponseData> getRegisterLiveData() {
        return registerRepo.getRegisterLiveData();
    }


    public void uploadIdFiles(File idfile0, File idfile1, String token) {
        registerRepo.uploadIdFiles(idfile0, idfile1, token);
    }

    public void uploadVideo(File video, String token) {
        registerRepo.uploadVideoFiles(video, token);
    }

    public void uploadSignature(File signature, String token) {
        registerRepo.uploadSignature(signature, token);
    }

    public MutableEventLiveData<Boolean> getVideoLiveData() {
        return registerRepo.getVideoLiveData();
    }

    public MutableEventLiveData<String> getVideLoadingError() {
        return registerRepo.getVideLoadingError();
    }

    public MutableEventLiveData<Boolean> getIdFilesLiveData() {
        return registerRepo.getIdFilesLiveData();
    }

    public MutableEventLiveData<String> getIdFileLoadingError() {
        return registerRepo.getIdFileLoadingError();
    }

    public MutableEventLiveData<Boolean> getSignatureLiveData() {
        return registerRepo.getSignatureLiveData();
    }

    public MutableEventLiveData<String> getSignatureLoadingError() {
        return registerRepo.getSignatureLoadingError();
    }


    public void customerCare() {
        registerRepo.getCustomerCareNumber();
    }

    public MutableEventLiveData<String> getContactLiveData() {
        return registerRepo.getContactLiveData();
    }

    public void getDate() {
        registerRepo.getCurrentDate();
    }

    public MutableEventLiveData<DateTimeResponseDate> getDateTimeLiveData() {
        return registerRepo.getDateTimeLiveData();
    }
}
