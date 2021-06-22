package io.thoughtbox.hamdan.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.json.JSONObject;

import java.util.ArrayList;

import io.thoughtbox.hamdan.utls.MutableEventLiveData;
import io.thoughtbox.hamdan.model.bankBenModel.BankBeneficiaryResponseData;
import io.thoughtbox.hamdan.model.cashPickUpModel.CashPickUpResponseData;
import io.thoughtbox.hamdan.repos.BankBeneficiaryRepo;

public class BankBeneficiaryViewModel extends AndroidViewModel {

    private BankBeneficiaryRepo bankBeneficiaryRepo;

    public BankBeneficiaryViewModel(@NonNull Application application) {
        super(application);
        bankBeneficiaryRepo = new BankBeneficiaryRepo();
    }

    public void clear() {
        bankBeneficiaryRepo.clear();
    }

    public MutableEventLiveData<Boolean> getIsLoading() {
        return bankBeneficiaryRepo.getIsLoading();
    }

    public MutableEventLiveData<String> getLoadingError() {
        return bankBeneficiaryRepo.getLoadingError();
    }

    public MutableEventLiveData<Boolean> getSessionStatus() {
        return bankBeneficiaryRepo.getIsSessionValid();
    }

    public MutableEventLiveData<ArrayList<BankBeneficiaryResponseData>> getBankBenData() {
        return bankBeneficiaryRepo.getBankBenLiveData();
    }

    public void getBankBenList() {
        bankBeneficiaryRepo.getBankBenList();
    }

    //<-----------------------------CASH PICK UP CALLS STARTING--------------------------------------->

    public MutableEventLiveData<ArrayList<CashPickUpResponseData>> getCashPickUpBenData() {
        return bankBeneficiaryRepo.getCashPickupBenLiveData();
    }

    public void getCashPickupBenList() {
        bankBeneficiaryRepo.getCashPickUpList();
    }

    //<------------------------------------------------------------------------------------------------>


    public void deleteBankBen(JSONObject params) {
        bankBeneficiaryRepo.deleteBankBen(params);
    }

    public MutableEventLiveData<Boolean> getIsBenDeleted() {
        return bankBeneficiaryRepo.getIsBenDeleted();
    }

    public void deleteCashBen(JSONObject params) {
        bankBeneficiaryRepo.deleteCashBen(params);
    }

    public MutableEventLiveData<Boolean> getCashBenDeleted() {
        return bankBeneficiaryRepo.getIsCashBenDeleted();
    }
}
