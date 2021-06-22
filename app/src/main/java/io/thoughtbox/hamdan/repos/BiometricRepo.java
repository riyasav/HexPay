package io.thoughtbox.hamdan.repos;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.thoughtbox.hamdan.utls.MutableEventLiveData;
import io.thoughtbox.hamdan.global.Universal;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import io.thoughtbox.hamdan.model.loginModel.Otp;
import io.thoughtbox.hamdan.services.DataService;
import io.thoughtbox.hamdan.services.ServiceRequest;
import retrofit2.HttpException;

public class BiometricRepo {

    @Inject
    public ServiceRequest serviceRequest;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableEventLiveData<Boolean> isLoading = new MutableEventLiveData<>();
    private MutableEventLiveData<String> loadingError = new MutableEventLiveData<>();
    private MutableEventLiveData<Boolean> isSessionValid = new MutableEventLiveData<>();
    private MutableEventLiveData<Boolean> isBiometricRegistered = new MutableEventLiveData<>();
    private MutableEventLiveData<Boolean> isBiometricRemoved = new MutableEventLiveData<>();
    private MutableEventLiveData<Boolean> isOtpValid = new MutableEventLiveData<>();

    public BiometricRepo() {
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


    public void registerBiometric(JSONObject params) {
        JsonParser jsonParser = new JsonParser();
        JsonObject gsonParams = (JsonObject) jsonParser.parse(params.toString());

        isLoading.postValue(true);

        DataService dataService = serviceRequest.getDataService();
        Observable<Otp> observerCall = dataService.registerBiometric(gsonParams, "Bearer " + Universal.getInstance().getLoginResponsedata().getToken());

        disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Otp>() {
                    @Override
                    public void onNext(Otp response) {
                        isLoading.postValue(false);
                        if (response.getTokenstatus().toUpperCase().equals("FALSE")) {
                            isSessionValid.postValue(false);
                        } else {
                            if (!response.getRenewedtoken().equals("NIL")) {
                                Universal.getInstance().getLoginResponsedata().setToken(response.getRenewedtoken());
                            }
                            if (response.getResponsestatus().toUpperCase().equals("TRUE") || response.getResponsestatus().toUpperCase().equals("")) {
                                if (response.getResponsedata().getResult().toUpperCase().equals("TRUE")) {
                                    isBiometricRegistered.postValue(true);
                                } else {
                                    loadingError.postValue(response.getResponsedescription());
                                }

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
                        isLoading.postValue(false);
                    }

                })
        );
    }


    public MutableEventLiveData<Boolean> getIsSessionValid() {
        return isSessionValid;
    }

    public MutableEventLiveData<Boolean> getIsBiometricRegistered() {
        return isBiometricRegistered;
    }

    public void removeBiometric(JSONObject params) {
        JsonParser jsonParser = new JsonParser();
        JsonObject gsonParams = (JsonObject) jsonParser.parse(params.toString());

        isLoading.postValue(true);

        DataService dataService = serviceRequest.getDataService();
        Observable<Otp> observerCall = dataService.removeBiometric(gsonParams, "Bearer " + Universal.getInstance().getLoginResponsedata().getToken());

        disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Otp>() {
                    @Override
                    public void onNext(Otp response) {
                        isLoading.postValue(false);
                        if (response.getTokenstatus().toUpperCase().equals("FALSE")) {
                            isSessionValid.postValue(false);
                        } else {
                            if (!response.getRenewedtoken().equals("NIL")) {
                                Universal.getInstance().getLoginResponsedata().setToken(response.getRenewedtoken());
                            }
                            if (response.getResponsestatus().toUpperCase().equals("TRUE") || response.getResponsestatus().toUpperCase().equals("")) {
                                if (response.getResponsedata().getResult().toUpperCase().equals("TRUE")) {
                                    isBiometricRemoved.postValue(true);
                                } else {
                                    loadingError.postValue(response.getResponsedescription());
                                }

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

                    }

                })
        );
    }

    public MutableEventLiveData<Boolean> getIsBiometricRemoved() {
        return isBiometricRemoved;
    }

    public void verifyOtp(String otp) {
        JSONObject params = new JSONObject();
        try {
            params.put("otp", otp);
            params.put("transfertype", "Biometric");
            params.put("platform", "ANDROID");

        } catch (JSONException e) {

        }
        JsonParser jsonParser = new JsonParser();
        JsonObject gsonParams = (JsonObject) jsonParser.parse(params.toString());
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();

        Observable<Otp> otpObservable = dataService.verifyBiometricOtp(gsonParams,
                "Bearer " + Universal.getInstance().getLoginResponsedata().getToken());

        disposable.add(otpObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Otp>() {
                    @Override
                    public void onNext(Otp otp) {
                        isLoading.postValue(false);
                        if (otp.getTokenstatus().toUpperCase().equals("FALSE")) {
                            isSessionValid.postValue(false);
                        } else {
                            if (!otp.getRenewedtoken().equals("NIL")) {
                                Universal.getInstance().getLoginResponsedata().setToken(otp.getRenewedtoken());
                            }
                            if (otp.getResponsestatus().toUpperCase().equals("TRUE") || otp.getResponsestatus().toUpperCase().equals("")) {
                                if (otp.getResponsedata().getResult().toUpperCase().equals("TRUE")) {
                                    isOtpValid.setValue(true);
                                } else {
                                    isOtpValid.setValue(false);
                                    loadingError.postValue(otp.getResponsedescription());
                                }
                            } else {
                                loadingError.postValue(otp.getResponsedescription());
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
                    }
                }));
    }

    public MutableEventLiveData<Boolean> getIsOtpValid() {
        return isOtpValid;
    }
}
