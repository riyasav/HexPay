package io.thoughtbox.hamdan.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import io.thoughtbox.hamdan.utls.MutableEventLiveData;
import io.thoughtbox.hamdan.model.invoice.ConfirmPaymentResponseData;
import io.thoughtbox.hamdan.model.invoice.InvoiceRequestModel;
import io.thoughtbox.hamdan.repos.InvoiceRepo;

public class InvoiceViewModel extends AndroidViewModel {
    private InvoiceRepo invoiceRepo;

    public InvoiceViewModel(@NonNull Application application) {
        super(application);
        invoiceRepo = new InvoiceRepo();
    }

    public void clear() {
        invoiceRepo.clear();
    }

    public MutableEventLiveData<Boolean> getIsLoading() {
        return invoiceRepo.getIsLoading();
    }

    public MutableEventLiveData<String> getLoadingError() {
        return invoiceRepo.getLoadingError();
    }

    public MutableEventLiveData<Boolean> getSessionStatus() {
        return invoiceRepo.getIsSessionValid();
    }

    public void getInvoice(InvoiceRequestModel requestModel) {
        invoiceRepo.getInvoice(requestModel);
    }

    public MutableEventLiveData<ConfirmPaymentResponseData> getInvoiceData() {
        return invoiceRepo.getInvoiceLiveData();
    }
}
