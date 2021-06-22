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
import io.thoughtbox.hamdan.model.loginModel.OtpRequestModel;
import io.thoughtbox.hamdan.services.DataService;
import io.thoughtbox.hamdan.services.ServiceRequest;
import retrofit2.HttpException;

public class OtpRepo {
    @Inject
    public ServiceRequest serviceRequest;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableEventLiveData<Boolean> isLoading = new MutableEventLiveData<>();
    private MutableEventLiveData<String> loadingError = new MutableEventLiveData<>();
    private MutableEventLiveData<Boolean> isOtpValid = new MutableEventLiveData<>();
    private MutableEventLiveData<Boolean> isTokenLessOtpValid = new MutableEventLiveData<>();
    private MutableEventLiveData<Boolean> isSessionValid = new MutableEventLiveData<>();

    public OtpRepo() {
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


    public void verifyOtp(OtpRequestModel requestParams) {
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();

        Observable<Otp> otpObservable = dataService.verifyOtpResponse(requestParams,
                "Bearer " + Universal.getInstance().getLoginResponsedata().getToken());

        disposable.add(otpObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Otp>() {
                    @Override
                    public void onNext(Otp otp) {
                        if (!otp.getRenewedtoken().equals("NIL")) {
                            Universal.getInstance().getLoginResponsedata().setToken(otp.getRenewedtoken());
                        }
                        if (otp.getResponsestatus().toUpperCase().equals("TRUE")) {
                            if (otp.getResponsedata().getResult().toUpperCase().equals("TRUE")) {
                                isLoading.postValue(false);
                                isOtpValid.setValue(true);
                            } else {
                                isOtpValid.setValue(false);
                                isLoading.postValue(false);
                            }

                        } else {
                            isOtpValid.setValue(false);
                            isLoading.postValue(false);
                            loadingError.postValue(otp.getResponsedescription());
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
//                        isLoading.postValue(false);
                    }
                }));
    }

    public MutableEventLiveData<Boolean> getIsOtpValidated() {
        return isOtpValid;
    }

    public void verifyTokenLessOtp(OtpRequestModel requestParams) {
        JSONObject params = new JSONObject();
        try {
            params.put("transfertype", requestParams.getTransfertype());
            params.put("otp", requestParams.getOtp());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonParser jsonParser = new JsonParser();
        JsonObject gsonParams = (JsonObject) jsonParser.parse(params.toString());

        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();

        Observable<Otp> otpObservable = dataService.verifyTokenLessOtpResponse(gsonParams, "Bearer " + requestParams.getRefno());

        disposable.add(otpObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Otp>() {
                    @Override
                    public void onNext(Otp otp) {

                        if (otp.getResponsestatus().toUpperCase().equals("TRUE")) {
                            if (otp.getResponsedata().getResult().toUpperCase().equals("TRUE")) {
                                isLoading.postValue(false);
                                isTokenLessOtpValid.setValue(true);
                            } else {
                                isTokenLessOtpValid.setValue(false);
                                isLoading.postValue(false);
                                loadingError.postValue(otp.getResponsedescription());
                            }

                        } else {
                            isTokenLessOtpValid.setValue(false);
                            isLoading.postValue(false);
                            loadingError.postValue(otp.getResponsedescription());
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
//                        isLoading.postValue(false);
                    }
                }));
    }

    public MutableEventLiveData<Boolean> getIsTokenLessOtpValid() {
        return isTokenLessOtpValid;
    }
}
