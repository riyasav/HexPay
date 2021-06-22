package io.thoughtbox.hamdan.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.json.JSONObject;

import java.util.ArrayList;

import io.thoughtbox.hamdan.utls.MutableEventLiveData;
import io.thoughtbox.hamdan.model.bankBenModel.BenCreate;
import io.thoughtbox.hamdan.model.bankBenModel.DetailData;
import io.thoughtbox.hamdan.model.beneficiary.BanksRequest;
import io.thoughtbox.hamdan.model.beneficiary.BranchRequest;
import io.thoughtbox.hamdan.model.transferModel.SelectionModal;
import io.thoughtbox.hamdan.repos.AddBenefciaryRepo;

public class AddBankBenViewModel extends AndroidViewModel {
    private AddBenefciaryRepo benefciary;

    public AddBankBenViewModel(@NonNull Application application) {
        super(application);
        benefciary = new AddBenefciaryRepo();
    }

    public void clear() {
        benefciary.clear();
    }

    public MutableEventLiveData<Boolean> getIsLoading() {
        return benefciary.getIsLoading();
    }

    public MutableEventLiveData<String> getLoadingError() {
        return benefciary.getLoadingError();
    }

    public void getcountry() {
        benefciary.getcountry();
    }

    public void getTxntype(String countryId) {
        benefciary.getTxntype(countryId);
    }

    public void getbank(BanksRequest params) {
        benefciary.getbank(params);
    }

    public void getbranch(BranchRequest params) {
        benefciary.getbranch(params);
    }

    public void getRelation() {
        benefciary.getRelation();
    }

    public void getNationality() {
        benefciary.getNationality();
    }

    public MutableEventLiveData<ArrayList<SelectionModal>> getTxntypeLiveData() {
        return benefciary.getTxntypeLiveData();
    }

    public MutableEventLiveData<ArrayList<SelectionModal>> getCountryLiveDat() {
        return benefciary.getCountryLiveData();
    }

    public MutableEventLiveData<ArrayList<DetailData>> getBankLiveData() {
        return benefciary.getBankLiveData();
    }

    public MutableEventLiveData<ArrayList<SelectionModal>> getBranchLiveData() {
        return benefciary.getBranchLiveData();
    }

    public MutableEventLiveData<ArrayList<SelectionModal>> getRelationLivedata() {
        return benefciary.getRelationLiveData();
    }

    public MutableEventLiveData<ArrayList<SelectionModal>> getNationalityLiveData() {
        return benefciary.getNationalityLiveData();
    }

    public void createBankBen(JSONObject params) {
        benefciary.createBankBen(params);
    }

    public MutableEventLiveData<BenCreate> createBankBenLiveData() {
        return benefciary.getCreateBankLiveData();
    }

    public MutableEventLiveData<Boolean> getSessionStatus() {
        return benefciary.getIsSessionValid();
    }

}
