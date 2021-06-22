package io.thoughtbox.hamdan.repos;

import android.util.Log;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.thoughtbox.hamdan.utls.MutableEventLiveData;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import io.thoughtbox.hamdan.model.checkUserId.CheckUserResponse;
import io.thoughtbox.hamdan.services.DataService;
import io.thoughtbox.hamdan.services.ServiceRequest;
import retrofit2.HttpException;

public class CheckUserIdRepo {

    @Inject
    public ServiceRequest serviceRequest;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableEventLiveData<Boolean> isLoading = new MutableEventLiveData<>();
    private MutableEventLiveData<String> loadingError = new MutableEventLiveData<>();
        private MutableEventLiveData<Boolean> isSessionValid = new MutableEventLiveData<>();
    private MutableEventLiveData<Boolean> isUserExist = new MutableEventLiveData<>();

    public CheckUserIdRepo() {
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

//    public MutableEventLiveData<Boolean> getIsSessionValid() {
//        return isSessionValid;
//    }

    public void checkUserId(String idnumber) {
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();
        Observable<CheckUserResponse> observerCall = dataService.getUserStatus(idnumber);
        disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<CheckUserResponse>() {
                    @Override
                    public void onNext(CheckUserResponse response) {
                        isLoading.postValue(false);

                        if (response.getResponsestatus().toUpperCase().equals("TRUE") || response.getResponsestatus().toUpperCase().equals("")) {
                            if (response.getResponsedata().getIsexist().trim().equals("0")) {
                                isUserExist.postValue(false);
                            } else {
                                isUserExist.postValue(true);
                            }

                        } else {
                            isUserExist.postValue(false);
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

    public MutableEventLiveData<Boolean> getIsUserExist() {
        return isUserExist;
    }

}
