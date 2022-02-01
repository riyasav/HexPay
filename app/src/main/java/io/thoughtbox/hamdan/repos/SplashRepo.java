package io.thoughtbox.hamdan.repos;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.thoughtbox.hamdan.model.BannerModel;
import io.thoughtbox.hamdan.model.BannerResponse;
import io.thoughtbox.hamdan.utls.MutableEventLiveData;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import io.thoughtbox.hamdan.model.dictionaryModel.DictionaryResponse;
import io.thoughtbox.hamdan.model.dictionaryModel.DictionaryResponseData;
import io.thoughtbox.hamdan.services.DataService;
import io.thoughtbox.hamdan.services.ServiceRequest;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.TlsVersion;
import retrofit2.HttpException;

public class SplashRepo {
    @Inject
    public ServiceRequest serviceRequest;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableEventLiveData<Boolean> isLoading = new MutableEventLiveData<>();
    private MutableEventLiveData<String> loadingError = new MutableEventLiveData<>();
    private ArrayList<DictionaryResponseData> dictionaryResponse;
    private MutableEventLiveData<ArrayList<DictionaryResponseData>> dictionaryLiveData = new MutableEventLiveData<>();
    private MutableEventLiveData<Boolean> isSessionValid = new MutableEventLiveData<>();
    private MutableEventLiveData<BannerResponse> bannerLiveData = new MutableEventLiveData<>();


    public SplashRepo() {
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

    public void getBannerData() {
        DataService dataService = serviceRequest.getDataService();

        Observable<BannerModel> bannerObservable = dataService.getBannerResult();

        disposable.add(bannerObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<BannerModel>() {
                    @Override
                    public void onNext(@NonNull BannerModel bannerModel) {
                        if (bannerModel.getResponsedata() != null)
                            bannerLiveData.postValue(bannerModel.getResponsedata());
                        else
                            loadingError.postValue(bannerModel.getResponsedescription());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        try {
                            if (((HttpException) e).code() == 401) {
                                isSessionValid.postValue(false);
                            } else {
                                loadingError.setValue(e.getMessage());
                            }
                        } catch (ClassCastException e1) {

                            loadingError.setValue(e1.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onComplete() {
                    }
                }));

    }
    public void getDictionary(String lang) {
        isLoading.postValue(true);
        dictionaryResponse = new ArrayList<>();
        DataService dataService = serviceRequest.getDataService();

        Observable<DictionaryResponse> dictionaryObserver = dataService.getTokenLessDictionary(lang);

        disposable.add(dictionaryObserver
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<DictionaryResponse>() {
                    @Override
                    public void onNext(@NonNull DictionaryResponse dictionary) {
                        if (dictionary.getRenewedtoken().equals("NIL")) {
                            Log.d("SplashToken", dictionary.getResponsestatus());
                        }
                        if (dictionary.getResponsedata() != null && dictionary.getResponsedata().size() > 1) {
                            dictionaryResponse = (ArrayList<DictionaryResponseData>) dictionary.getResponsedata();
                            dictionaryLiveData.postValue(dictionaryResponse);
                            isLoading.postValue(false);
                        } else {
                            isLoading.postValue(false);
                            loadingError.postValue(dictionary.getResponsedescription());
                        }
                    }


                    @Override
                    public void onError(Throwable e) {
                        isLoading.postValue(false);
                        try {
                            if (((HttpException) e).code() == 401) {
                                isSessionValid.postValue(false);
                            } else {
                                loadingError.setValue(e.getMessage());
                            }
                        } catch (ClassCastException e1) {
                            loadingError.setValue(e1.getLocalizedMessage());
                        }

                    }

                    @Override
                    public void onComplete() {
                        isLoading.postValue(false);
                    }
                })
        );
    }

    public MutableEventLiveData<ArrayList<DictionaryResponseData>> getDictionaryLiveData() {
        return dictionaryLiveData;
    }

    public MutableEventLiveData<BannerResponse> getBannerLiveData() {
        return bannerLiveData;
    }
}
