package io.thoughtbox.hamdan.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.json.JSONObject;

import java.util.ArrayList;

import io.thoughtbox.hamdan.utls.MutableEventLiveData;
import io.thoughtbox.hamdan.model.bankBenModel.BenCreate;
import io.thoughtbox.hamdan.model.bankBenModel.CheckOutResponseData;
import io.thoughtbox.hamdan.model.bankBenModel.DetailData;
import io.thoughtbox.hamdan.model.beneficiary.BanksRequest;
import io.thoughtbox.hamdan.model.transferModel.SelectionModal;
import io.thoughtbox.hamdan.repos.AddFastCashBenRepo;

public class AddFastCashBenViewModel extends AndroidViewModel {
    private AddFastCashBenRepo benefciary;


    public AddFastCashBenViewModel(@NonNull Application application) {
        super(application);
        benefciary = new AddFastCashBenRepo();
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

    public void getAgent(BanksRequest params) {
        benefciary.getAgent(params);
    }

    public void getNormalbranch(JSONObject params) {
        benefciary.getNormalbranch(params);
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

    public MutableEventLiveData<ArrayList<DetailData>> getAgentLiveData() {
        return benefciary.getBankLiveData();
    }

    public MutableEventLiveData<ArrayList<SelectionModal>> getNormalBranchLiveData() {
        return benefciary.getNormalBranchLiveData();
    }

    public MutableEventLiveData<ArrayList<SelectionModal>> getRelationLivedata() {
        return benefciary.getRelationLiveData();
    }

    public MutableEventLiveData<ArrayList<SelectionModal>> getNationalityLiveData() {
        return benefciary.getNationalityLiveData();
    }

    public void createBen(JSONObject params) {
        benefciary.createCashBen(params);
    }

    public MutableEventLiveData<BenCreate> createFastCashBenLiveData() {
        return benefciary.getCreateFAstCashLiveData();
    }

    public void getIDtype() {
        benefciary.getIdType();
    }

    public MutableEventLiveData<ArrayList<SelectionModal>> getIdTypeLiveData() {
        return benefciary.getIdTypeLiveData();
    }

    public void checkPayOut(JSONObject params) {
        benefciary.checkPayOut(params);
    }

    public MutableEventLiveData<CheckOutResponseData> checkOutLiveData() {
        return benefciary.getCheckOutLiveData();
    }


    public void getState(JSONObject params) {
        benefciary.getState(params);
    }

    public MutableEventLiveData<ArrayList<SelectionModal>> getStateLiveData() {
        return benefciary.getStateLiveData();
    }

    public void getCity(JSONObject params) {
        benefciary.getCity(params);
    }

    public MutableEventLiveData<ArrayList<SelectionModal>> getCityLiveData() {
        return benefciary.getCityLiveData();
    }

    public void getTFbranch(JSONObject params) {
        benefciary.getbranches(params);
    }

    public MutableEventLiveData<ArrayList<SelectionModal>> getTFBranchesLiveData() {
        return benefciary.getBranchesLiveData();
    }

    public MutableEventLiveData<Boolean> getSessionStatus() {
        return benefciary.getIsSessionValid();
    }
}
