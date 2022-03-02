package io.thoughtbox.hamdan.repos;

import android.util.Log;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.thoughtbox.hamdan.global.Universal;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import io.thoughtbox.hamdan.model.rateModels.DashboardRateModel;
import io.thoughtbox.hamdan.model.rateModels.RateResponse;
import io.thoughtbox.hamdan.model.rateModels.RateResponseData;
import io.thoughtbox.hamdan.services.DataService;
import io.thoughtbox.hamdan.services.ServiceRequest;
import io.thoughtbox.hamdan.utls.MutableEventLiveData;
import retrofit2.HttpException;

public class RateCheckerRepo {

    public RateCheckerRepo(){
      DaggerApiComponents.create().inject(this);
    }


    @Inject
    public ServiceRequest serviceRequest;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableEventLiveData<Boolean> isLoading = new MutableEventLiveData<>();
    private MutableEventLiveData<String> loadingError = new MutableEventLiveData<>();
    private MutableEventLiveData<Boolean> isSessionValid = new MutableEventLiveData<>();
    private MutableEventLiveData<ArrayList<RateResponseData>> tokenLessRateLiveData = new MutableEventLiveData<>();
    private ArrayList<RateResponseData> rateList;


    public void getCheckRaterRate() {
        rateList = new ArrayList<>();
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();
        Observable<RateResponse> observerCall = dataService.getTokenLessRate();
        disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<RateResponse>() {
                    @Override
                    public void onNext(RateResponse rateResponse) {
                        isLoading.postValue(false);

                            if (rateResponse.getResponsestatus().toUpperCase().equals("TRUE") || rateResponse.getResponsestatus().toUpperCase().equals("")) {
                              rateList=(ArrayList<RateResponseData>) rateResponse.getResponsedata();
                              tokenLessRateLiveData.postValue(rateList);


                            } else {
                                loadingError.postValue(rateResponse.getResponsedescription());
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

                    }

                })
        );
    }

    public MutableEventLiveData<ArrayList<RateResponseData>> getTokenLessRateLiveData() {
        return tokenLessRateLiveData;
    }

    public MutableEventLiveData<String> getLoadingError() {
        return loadingError;
    }

    public MutableEventLiveData<Boolean> getIsSessionValid() {
        return isSessionValid;
    }
}
