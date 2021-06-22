package io.thoughtbox.hamdan.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import io.thoughtbox.hamdan.utls.MutableEventLiveData;
import io.thoughtbox.hamdan.repos.PaymentRepo;

public class PaymentViewModel extends AndroidViewModel {
    private PaymentRepo paymentRepo;

    public PaymentViewModel(@NonNull Application application) {
        super(application);
        paymentRepo = new PaymentRepo();
    }

    public void clear() {
        paymentRepo.clear();
    }

    public MutableEventLiveData<Boolean> getIsLoading() {
        return paymentRepo.getIsLoading();
    }

    public MutableEventLiveData<String> getLoadingError() {
        return paymentRepo.getLoadingError();
    }


    public void getPaymentGateway(String transferType, String referenceNum) {
        paymentRepo.getPaymentGateway(transferType, referenceNum);
    }

    public MutableEventLiveData<String> htmlLiveData() {
        return paymentRepo.getHtmlLiveData();
    }
}
