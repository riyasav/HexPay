package io.thoughtbox.hamdan.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.ArrayList;

import io.thoughtbox.hamdan.utls.MutableEventLiveData;
import io.thoughtbox.hamdan.model.transferModel.GetRateResponseData;
import io.thoughtbox.hamdan.model.transferModel.RateRequest;
import io.thoughtbox.hamdan.model.transferModel.SelectionModal;
import io.thoughtbox.hamdan.model.transferModel.TransferRequestModel;
import io.thoughtbox.hamdan.model.transferModel.TransferResponseData;
import io.thoughtbox.hamdan.repos.TransferRepo;

public class TransferViewModel extends AndroidViewModel {
    private TransferRepo transferRepo;

    public TransferViewModel(@NonNull Application application) {
        super(application);
        transferRepo = new TransferRepo();
    }

    public void clear() {
        transferRepo.clear();
    }

    public MutableEventLiveData<Boolean> getIsLoading() {
        return transferRepo.getIsLoading();
    }

    public MutableEventLiveData<String> getLoadingError() {
        return transferRepo.getLoadingError();
    }

    public MutableEventLiveData<Boolean> getSessionStatus() {
        return transferRepo.getIsSessionValid();
    }

    public MutableEventLiveData<ArrayList<SelectionModal>> getModesLiveData() {
        return transferRepo.getModeLiveData();
    }

    public void getModes() {
        transferRepo.getMode();
    }

    public MutableEventLiveData<ArrayList<SelectionModal>> getPurposeLiveData() {
        return transferRepo.getPurposeLiveData();
    }

    public void getPurposes() {
        transferRepo.getPurpose();
    }

    public MutableEventLiveData<ArrayList<SelectionModal>> getSourceLiveData() {
        return transferRepo.getSourceLiveData();
    }

    public void getSources() {
        transferRepo.getSource();
    }


    public MutableEventLiveData<ArrayList<SelectionModal>> getBankLiveData() {
        return transferRepo.getBankLiveData();
    }

    public void getBanks() {
        transferRepo.getBank();
    }

    public MutableEventLiveData<GetRateResponseData> getRateResponseLiveData() {
        return transferRepo.getRateResponseLiveData();
    }

    public void getRate(RateRequest params) {
        transferRepo.getRate(params);
    }

    public void createTransfer(TransferRequestModel params) {
        transferRepo.createTransfer(params);
    }

    public MutableEventLiveData<TransferResponseData> getTransferResponseLiveData() {
        return transferRepo.getTransferResponseLiveData();
    }

    public void createFastCashTransfer(TransferRequestModel params) {
        transferRepo.createFastCashTransfer(params);
    }


}
