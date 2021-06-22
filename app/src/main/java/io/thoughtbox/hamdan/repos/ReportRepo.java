package io.thoughtbox.hamdan.repos;

import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.thoughtbox.hamdan.utls.MutableEventLiveData;
import io.thoughtbox.hamdan.global.Universal;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import io.thoughtbox.hamdan.model.reportModel.ReportResponse;
import io.thoughtbox.hamdan.model.reportModel.ReportResponseData;
import io.thoughtbox.hamdan.model.reportModel.TrackTransferResponse;
import io.thoughtbox.hamdan.services.DataService;
import io.thoughtbox.hamdan.services.ServiceRequest;
import retrofit2.HttpException;

public class ReportRepo {
    @Inject
    public ServiceRequest serviceRequest;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableEventLiveData<Boolean> isLoading = new MutableEventLiveData<>();
    private MutableEventLiveData<String> loadingError = new MutableEventLiveData<>();
    private MutableEventLiveData<Boolean> isSessionValid = new MutableEventLiveData<>();
    private ArrayList<ReportResponseData> reportList;
    private MutableEventLiveData<ArrayList<ReportResponseData>> reportLiveData = new MutableEventLiveData<>();
    private MutableEventLiveData<String[]> statusLiveData = new MutableEventLiveData<>();

    public ReportRepo() {
        DaggerApiComponents.create().inject(this);
    }

    public void clear() {
        disposable.clear();
    }

    public MutableEventLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableEventLiveData<String> getLoadingError() {
        return loadingError;
    }

    public void getReport(JSONObject params) {
        JsonParser jsonParser = new JsonParser();
        JsonObject gsonParams = (JsonObject) jsonParser.parse(params.toString());
        reportList = new ArrayList<>();
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();
        Observable<ReportResponse> observerCall = dataService.getReports(gsonParams, "Bearer " + Universal.getInstance().getLoginResponsedata().getToken());
        disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<ReportResponse>() {
                    @Override
                    public void onNext(ReportResponse response) {
                        isLoading.postValue(false);
                        if (response.getTokenstatus().toUpperCase().equals("FALSE")) {
                            isSessionValid.postValue(false);
                        } else {
                            if (!response.getRenewedtoken().equals("NIL")) {
                                Universal.getInstance().getLoginResponsedata().setToken(response.getRenewedtoken());
                            }
                            if (response.getResponsestatus().toUpperCase().equals("TRUE") || response.getResponsestatus().toUpperCase().equals("")) {
                                reportList = (ArrayList<ReportResponseData>) response.getResponsedata();
                                reportLiveData.postValue(reportList);
                            } else {
                                loadingError.postValue(response.getResponsedescription());
                            }
                        }
                    }

                   @Override
                    public void onError(Throwable e) {
                        isLoading.postValue(false);
                        if (((HttpException) e).code() == 401) {
                            isSessionValid.postValue(false);
                        } else {
                            loadingError.setValue(e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {
                        Log.d("dashboard", "Login Call Completed");
                    }
                })
        );
    }

    public MutableEventLiveData<ArrayList<ReportResponseData>> getReportLiveData() {
        return reportLiveData;
    }

    public void trackBank(String refno) {
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();
        Observable<TrackTransferResponse> observerCall = dataService.getBankStatus(refno, "Bearer " + Universal.getInstance().getLoginResponsedata().getToken());
        disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<TrackTransferResponse>() {
                    @Override
                    public void onNext(TrackTransferResponse response) {
                        isLoading.postValue(false);
                        if (response.getRenewedtoken() != null) {
                            Universal.getInstance().getLoginResponsedata().setToken(response.getRenewedtoken());
                        }
                        if (response.getResponsestatus().toUpperCase().equals("TRUE") || response.getResponsestatus().toUpperCase().equals("")) {
                            String[] status = new String[2];
                            status[0] = response.getResponsedata().getPaymentstatus();
                            status[1] = response.getResponsedata().getCorrespondentstatus();


                            statusLiveData.postValue(status);
                        } else {
                            loadingError.postValue(response.getResponsedescription());
                        }
                    }

                   @Override
                    public void onError(Throwable e) {
                        isLoading.postValue(false);
                        if (((HttpException) e).code() == 401) {
                            isSessionValid.postValue(false);
                        } else {
                            loadingError.setValue(e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {
                        Log.d("dashboard", "Login Call Completed");
                    }
                })
        );
    }

    public void trackCash(String refno) {
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();
        Observable<TrackTransferResponse> observerCall = dataService.getFastCashStatus(refno, "Bearer " + Universal.getInstance().getLoginResponsedata().getToken());
        disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<TrackTransferResponse>() {
                    @Override
                    public void onNext(TrackTransferResponse response) {
                        isLoading.postValue(false);
                        if (response.getRenewedtoken() != null) {
                            Universal.getInstance().getLoginResponsedata().setToken(response.getRenewedtoken());
                        }
                        if (response.getResponsestatus().toUpperCase().equals("TRUE") || response.getResponsestatus().toUpperCase().equals("")) {
                            String[] status = new String[2];
                            status[0] = response.getResponsedata().getPaymentstatus();
                            status[1] = response.getResponsedata().getCorrespondentstatus();


                            statusLiveData.postValue(status);
                        } else {
                            loadingError.postValue(response.getResponsedescription());
                        }
                    }

                   @Override
                    public void onError(Throwable e) {
                        isLoading.postValue(false);
                        if (((HttpException) e).code() == 401) {
                            isSessionValid.postValue(false);
                        } else {
                            loadingError.setValue(e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {
                        Log.d("dashboard", "Login Call Completed");
                    }
                })
        );
    }

    public MutableEventLiveData<String[]> getStatusLiveData() {
        return statusLiveData;
    }

    public MutableEventLiveData<Boolean> getIsSessionValid() {
        return isSessionValid;
    }
}
