package io.thoughtbox.hamdan.repos;

import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.thoughtbox.hamdan.global.Universal;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import io.thoughtbox.hamdan.model.dictionaryModel.DictionaryResponse;
import io.thoughtbox.hamdan.model.dictionaryModel.DictionaryResponseData;
import io.thoughtbox.hamdan.model.languageModel.LanguageResponse;
import io.thoughtbox.hamdan.model.languageModel.LanguageResponseData;
import io.thoughtbox.hamdan.model.loginModel.LoginRequestModel;
import io.thoughtbox.hamdan.model.loginModel.LoginResponse;
import io.thoughtbox.hamdan.model.loginModel.Otp;
import io.thoughtbox.hamdan.model.loginModel.OtpRequestModel;
import io.thoughtbox.hamdan.services.DataService;
import io.thoughtbox.hamdan.services.ServiceRequest;
import io.thoughtbox.hamdan.utls.AppData;
import io.thoughtbox.hamdan.utls.MutableEventLiveData;
import retrofit2.HttpException;

public class LoginRepo {
    @Inject
    public ServiceRequest serviceRequest;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableEventLiveData<Boolean> isLoading = new MutableEventLiveData<>();
    private MutableEventLiveData<String> loadingError = new MutableEventLiveData<>();
    private MutableEventLiveData<Boolean> isSessionValid = new MutableEventLiveData<>();
    private ArrayList<DictionaryResponseData> dictionaryResponse;
    private ArrayList<LanguageResponseData> languageList;
    private MutableEventLiveData<ArrayList<DictionaryResponseData>> dictionaryLiveData = new MutableEventLiveData<>();
    //    private MutableEventLiveData<Boolean> isOtpValidated = new MutableEventLiveData<>();
    private MutableEventLiveData<ArrayList<LanguageResponseData>> languageListLiveData = new MutableEventLiveData<>();
    private MutableLiveData<Boolean> forgotPasswordLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> languageUpdateLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isDeviceTokenUpdated = new MutableLiveData<>();
    private MutableLiveData<Boolean> resendOtpLiveData = new MutableLiveData<>();

    private FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();


    public LoginRepo() {
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

    public void biometricLogin(AppData appData,JSONObject params) {
        isLoading.postValue(true);

        JsonParser jsonParser = new JsonParser();
        JsonObject gsonParams = (JsonObject) jsonParser.parse(params.toString());

        DataService dataService = serviceRequest.getDataService();
        Observable<LoginResponse> observerCall = dataService.biometricLogin(gsonParams);
        disposable.add(observerCall
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<LoginResponse>() {
                    @Override
                    public void onNext(LoginResponse loginResponseModel) {

                        if (loginResponseModel.getResponsestatus().toUpperCase().equals("TRUE")) {
                            Universal.getInstance().setLoginResponsedata(loginResponseModel.getResponsedata());
                            if (appData.hasDefaultLanguage()){
                                getDictionary(appData.getDeviceLanguage());
                                JSONObject params = new JSONObject();
                                try {
                                    params.put("language", appData.getDeviceLanguage());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                updateSelectedLanguage(params);

                            }else{
                                getDictionary(Universal.getInstance().getLoginResponsedata().getLang());
                            }                        } else {
                            isLoading.postValue(false);
                            loadingError.postValue(loginResponseModel.getResponsedescription());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        loadingError.setValue(e.getMessage());
                        isLoading.postValue(false);
                        Log.d("Login", "Login throw an error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d("Login", "Login Call Completed");
                    }
                })
        );
    }

    public void getLogin(AppData appData, LoginRequestModel requestParams) {
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();
        Observable<LoginResponse> observerCall = dataService.userLogin(requestParams);
        disposable.add(observerCall
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<LoginResponse>() {
                    @Override
                    public void onNext(LoginResponse loginResponseModel) {

                        if (loginResponseModel.getResponsestatus().toUpperCase().equals("TRUE")) {
                            Universal.getInstance().setLoginResponsedata(loginResponseModel.getResponsedata());
                            if (appData.hasDefaultLanguage()) {
                                getDictionary(appData.getDeviceLanguage());
                            } else {
                                getDictionary(Universal.getInstance().getLoginResponsedata().getLang());
                            }
                        } else {
                            isLoading.postValue(false);
                            loadingError.postValue(loginResponseModel.getResponsedescription());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        loadingError.setValue(e.getMessage());
                        isLoading.postValue(false);
                        Log.d("Login", "Login throw an error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d("Login", "Login Call Completed");
                    }
                })
        );
    }

    public void getDictionary(String language) {
        isLoading.postValue(true);
        dictionaryResponse = new ArrayList<>();
        DataService dataService = serviceRequest.getDataService();

        Observable<DictionaryResponse> dictionaryObserver = dataService.getDictionaryResponse(
                language,
                "Bearer " + Universal.getInstance().getLoginResponsedata().getToken()
        );

        disposable.add(dictionaryObserver
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<DictionaryResponse>() {
                    @Override
                    public void onNext(DictionaryResponse dictionary) {
                        if (dictionary.getResponsestatus().toUpperCase().equals("TRUE")) {
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

    public MutableEventLiveData<ArrayList<DictionaryResponseData>> getDictionaryLiveData() {
        return dictionaryLiveData;
    }


    public void getAllLanguages() {
        isLoading.postValue(true);
        languageList = new ArrayList<>();
        DataService dataService = serviceRequest.getDataService();

        Observable<LanguageResponse> dictionaryObserver = dataService.getAllLanguages(
                "Bearer " + Universal.getInstance().getLoginResponsedata().getToken()
        );

        disposable.add(dictionaryObserver
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<LanguageResponse>() {
                    @Override
                    public void onNext(LanguageResponse dictionary) {
                        isLoading.postValue(false);
                        if (dictionary.getTokenstatus().toUpperCase().equals("FALSE")) {
                            isSessionValid.postValue(false);
                        } else {
                            if (!dictionary.getRenewedtoken().equals("NIL")) {
                                Universal.getInstance().getLoginResponsedata().setToken(dictionary.getRenewedtoken());
                            }
                            if (!dictionary.getResponsestatus().toUpperCase().equals("FALSE")) {
                                languageList = (ArrayList<LanguageResponseData>) dictionary.getResponsedata();
                                languageListLiveData.postValue(languageList);
                            } else {
                                loadingError.postValue(dictionary.getResponsedescription());
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

    public MutableEventLiveData<ArrayList<LanguageResponseData>> getLanguageListLiveData() {
        return languageListLiveData;
    }

    public void updateSelectedLanguage(JSONObject params) {
        JsonParser jsonParser = new JsonParser();
        JsonObject gsonParams = (JsonObject) jsonParser.parse(params.toString());
        isLoading.postValue(true);

        DataService dataService = serviceRequest.getDataService();

        Observable<Otp> dictionaryObserver = dataService.updateLanguage(gsonParams,
                "Bearer " + Universal.getInstance().getLoginResponsedata().getToken()
        );

        disposable.add(dictionaryObserver
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Otp>() {
                    @Override
                    public void onNext(Otp dictionary) {
                        isLoading.postValue(false);
                        if (dictionary.getTokenstatus().toUpperCase().equals("FALSE")) {
                            isSessionValid.postValue(false);
                        } else {
                            if (!dictionary.getRenewedtoken().equals("NIL")) {
                                Universal.getInstance().getLoginResponsedata().setToken(dictionary.getRenewedtoken());
                            }
                            if (!dictionary.getResponsestatus().toUpperCase().equals("FALSE") || dictionary.getResponsestatus().equals("")) {
                                if (dictionary.getResponsedata().getResult().toUpperCase().equals("TRUE")) {
                                    languageUpdateLiveData.postValue(true);
                                } else {
                                    loadingError.postValue(dictionary.getResponsedescription());
                                }
                            } else {
                                loadingError.postValue(dictionary.getResponsedescription());
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

    public MutableLiveData<Boolean> getLanguageUpdateLiveData() {
        return languageUpdateLiveData;
    }


    public void verifyOtp(OtpRequestModel requestParams) {
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();

        Observable<Otp> otpObservable = dataService.getOtpResponse(requestParams,
                "Bearer " + Universal.getInstance().getLoginResponsedata().getToken());

        disposable.add(otpObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Otp>() {
                    @Override
                    public void onNext(Otp otp) {
                        if (otp.getResponsestatus().toUpperCase().equals("TRUE")) {
                            if (otp.getResponsedata().getResult().toUpperCase().equals("TRUE")) {
                                isLoading.postValue(false);
                                if (Build.MANUFACTURER.toUpperCase().trim().equals("HUAWEI")) {
                                    isDeviceTokenUpdated.setValue(true);
                                } else {
                                    getFCMToken();
                                }
                            } else {
                                isLoading.postValue(false);
                                loadingError.postValue(otp.getResponsedata().getResult());
                            }
                        } else {
//                            isOtpValidated.setValue(false);
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

    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        try {
                            String fcmToken = Objects.requireNonNull(task.getResult());
                            JSONObject object = new JSONObject();
                            object.put("platform", "ANDROID");
                            object.put("devicetoken", fcmToken);
                            updateFCMToken(object);
                        } catch (Exception e) {
                            Log.d("Exception:", Objects.requireNonNull(e.getMessage()));
                        }

                    } else {
                        loadingError.setValue("Device registration failed");
                    }
                });
    }

    private void updateFCMToken(JSONObject params) {

        JsonParser jsonParser = new JsonParser();
        JsonObject gsonParams = (JsonObject) jsonParser.parse(params.toString());

        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();

        Observable<Otp> otpObservable = dataService.updateFcmToken(gsonParams,
                "Bearer " + Universal.getInstance().getLoginResponsedata().getToken());

        disposable.add(otpObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Otp>() {
                    @Override
                    public void onNext(Otp otp) {
                        isLoading.postValue(false);
                        if (otp.getResponsestatus().toUpperCase().equals("TRUE")) {
                            if (otp.getResponsedata().getResult().toUpperCase().equals("TRUE")) {
                                isDeviceTokenUpdated.setValue(true);
                            } else {
                                isLoading.postValue(false);
                                loadingError.postValue("Device registration failed");
                            }

                        } else {
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

//    public MutableEventLiveData<Boolean> getIsOtpValidated() {
//        return isOtpValidated;
//    }

    public void forgotPassword(String username) {
        JSONObject object = new JSONObject();
        try {
            object.put("username", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonParser jsonParser = new JsonParser();
        JsonObject gsonParams = (JsonObject) jsonParser.parse(object.toString());

        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();
        Observable<Otp> observerCall = dataService.forgotPassword(gsonParams);
        disposable.add(observerCall
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Otp>() {
                    @Override
                    public void onNext(Otp response) {
                        isLoading.postValue(false);
                        if (response.getResponsestatus().toUpperCase().equals("TRUE")) {
                            if (response.getResponsedata().getResult().toUpperCase().equals("TRUE")) {
                                forgotPasswordLiveData.postValue(true);
                            } else {
                                forgotPasswordLiveData.postValue(false);
                            }

                        } else {
                            loadingError.postValue(response.getResponsedescription());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        isLoading.postValue(false);
                        loadingError.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                })
        );
    }

    public MutableLiveData<Boolean> getForgotPasswordLiveData() {
        return forgotPasswordLiveData;
    }

    public MutableLiveData<Boolean> getIsDeviceTokenUpdated() {
        return isDeviceTokenUpdated;
    }

    public void resendOtp(OtpRequestModel params) {
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();
        Observable<Otp> observerCall = dataService.resendLoginOtp(params, "Bearer " + Universal.getInstance().getLoginResponsedata().getToken());
        disposable.add(observerCall
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Otp>() {
                    @Override
                    public void onNext(Otp response) {
                        isLoading.postValue(false);
                        if (response.getResponsestatus().toUpperCase().equals("TRUE")) {
                            if (response.getResponsedata().getResult().toUpperCase().equals("TRUE")) {
                                resendOtpLiveData.postValue(true);
                            } else {
                                resendOtpLiveData.postValue(false);
                            }
                        } else {
                            resendOtpLiveData.postValue(false);
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
                    }
                })
        );
    }

    public MutableLiveData<Boolean> getResendOtpLiveData() {
        return resendOtpLiveData;
    }

    public void resendRegisterOtp(OtpRequestModel params) {
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();
        Observable<Otp> observerCall = dataService.resendRegisterOtp(params);
        disposable.add(observerCall
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Otp>() {
                    @Override
                    public void onNext(Otp response) {
                        isLoading.postValue(false);
                        if (response.getResponsestatus().toUpperCase().equals("TRUE")) {
                            if (response.getResponsedata().getResult().toUpperCase().equals("TRUE")) {
                                resendOtpLiveData.postValue(true);
                            } else {
                                resendOtpLiveData.postValue(false);
                            }
                        } else {
                            resendOtpLiveData.postValue(false);
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
                    }
                })
        );
    }

    public void resendBiometricOtp(OtpRequestModel params) {
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();
        Observable<Otp> observerCall = dataService.resendBiometricOtp(params, "Bearer " + Universal.getInstance().getLoginResponsedata().getToken());
        disposable.add(observerCall
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Otp>() {
                    @Override
                    public void onNext(Otp response) {
                        isLoading.postValue(false);
                        if (response.getResponsestatus().toUpperCase().equals("TRUE")) {
                            if (response.getResponsedata().getResult().toUpperCase().equals("TRUE")) {
                                resendOtpLiveData.postValue(true);
                            } else {
                                resendOtpLiveData.postValue(false);
                            }
                        } else {
                            resendOtpLiveData.postValue(false);
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
                    }
                })
        );
    }

    public MutableEventLiveData<Boolean> getIsSessionValid() {
        return isSessionValid;
    }
}
