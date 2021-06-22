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
import io.thoughtbox.hamdan.model.notificationModel.NotificationResponseData;
import io.thoughtbox.hamdan.model.notificationModel.NotificationResponseModel;
import io.thoughtbox.hamdan.services.DataService;
import io.thoughtbox.hamdan.services.ServiceRequest;
import retrofit2.HttpException;

public class NotificationRepo {
    @Inject
    public ServiceRequest serviceRequest;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableEventLiveData<Boolean> isLoading = new MutableEventLiveData<>();
    private MutableEventLiveData<String> loadingError = new MutableEventLiveData<>();
    private MutableEventLiveData<Boolean> isSessionValid = new MutableEventLiveData<>();
    private ArrayList<NotificationResponseData> notificationList;
    private MutableEventLiveData<ArrayList<NotificationResponseData>> notificationLiveData = new MutableEventLiveData<>();

    public NotificationRepo() {
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

    public void getNotification(JSONObject params) {
        JsonParser jsonParser = new JsonParser();
        JsonObject gsonParams = (JsonObject) jsonParser.parse(params.toString());

        notificationList = new ArrayList<>();
        isLoading.postValue(true);

        DataService dataService = serviceRequest.getDataService();
        Observable<NotificationResponseModel> observerCall = dataService.getAllNotification(gsonParams, "Bearer " + Universal.getInstance().getLoginResponsedata().getToken());
        disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<NotificationResponseModel>() {
                    @Override
                    public void onNext(NotificationResponseModel response) {
                        isLoading.postValue(false);
                        if (response.getTokenstatus().toUpperCase().equals("FALSE")) {
                            isSessionValid.postValue(false);
                        } else {
                            if (!response.getRenewedtoken().equals("NIL")) {
                                Universal.getInstance().getLoginResponsedata().setToken(response.getRenewedtoken());
                            }
                            if (response.getResponsestatus().toUpperCase().equals("TRUE") || response.getResponsestatus().toUpperCase().equals("")) {
                                notificationList = (ArrayList<NotificationResponseData>) response.getResponsedata();
                                notificationLiveData.postValue(notificationList);
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

    public MutableEventLiveData<ArrayList<NotificationResponseData>> getNotificationLiveData() {
        return notificationLiveData;
    }

    public MutableEventLiveData<Boolean> getIsSessionValid() {
        return isSessionValid;
    }
}
