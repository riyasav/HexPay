package io.thoughtbox.hamdan.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.json.JSONObject;

import java.util.ArrayList;

import io.thoughtbox.hamdan.utls.MutableEventLiveData;
import io.thoughtbox.hamdan.model.reportModel.ReportResponseData;
import io.thoughtbox.hamdan.repos.ReportRepo;

public class ReportViewModel extends AndroidViewModel {
    private ReportRepo reportRepo;

    public ReportViewModel(@NonNull Application application) {
        super(application);
        reportRepo = new ReportRepo();
    }

    public void clear() {
        reportRepo.clear();
    }

    public MutableEventLiveData<Boolean> getIsLoading() {
        return reportRepo.getIsLoading();
    }

    public MutableEventLiveData<String> getLoadingError() {
        return reportRepo.getLoadingError();
    }

    public MutableEventLiveData<Boolean> getSessionStatus() {
        return reportRepo.getIsSessionValid();
    }

    public void getReport(JSONObject params) {
        reportRepo.getReport(params);
    }

    public MutableEventLiveData<ArrayList<ReportResponseData>> getReportLiveData() {
        return reportRepo.getReportLiveData();
    }

    public void trackBank(String refno) {
        reportRepo.trackBank(refno);
    }

    public void trackCash(String refno) {
        reportRepo.trackBank(refno);
    }

    public MutableEventLiveData<String[]> getStatusLiveData() {
        return reportRepo.getStatusLiveData();
    }
}

