package io.thoughtbox.hamdan.repos;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.thoughtbox.hamdan.model.DateTimeResponse;
import io.thoughtbox.hamdan.model.DateTimeResponseDate;
import io.thoughtbox.hamdan.model.FileUploadResponse;
import io.thoughtbox.hamdan.model.profile.ProfileResponse;
import io.thoughtbox.hamdan.model.profile.ProfileResponseData;
import io.thoughtbox.hamdan.model.transferModel.SelectionModal;
import io.thoughtbox.hamdan.model.transferModel.SelectionResponse;
import io.thoughtbox.hamdan.utls.MutableEventLiveData;
import io.thoughtbox.hamdan.global.Universal;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import io.thoughtbox.hamdan.model.customerSupport.SupportModel;
import io.thoughtbox.hamdan.model.rateModels.DashboardRateModel;
import io.thoughtbox.hamdan.model.rateModels.RateResponse;
import io.thoughtbox.hamdan.model.rateModels.RateResponseData;
import io.thoughtbox.hamdan.services.DataService;
import io.thoughtbox.hamdan.services.ServiceRequest;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class HomeRepo {

    @Inject
    public ServiceRequest serviceRequest;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableEventLiveData<Boolean> isLoading = new MutableEventLiveData<>();
    private MutableEventLiveData<String> loadingError = new MutableEventLiveData<>();
    private MutableEventLiveData<Boolean> isSessionValid = new MutableEventLiveData<>();
    private ArrayList<RateResponseData> rateList;
    private MutableEventLiveData<ArrayList<RateResponseData>> rateLiveData = new MutableEventLiveData<>();

    private MutableEventLiveData<String[]> dashboardRateLiveData = new MutableEventLiveData<>();
    private MutableEventLiveData<String> contactLiveData = new MutableEventLiveData<>();

    private MutableEventLiveData<ProfileResponseData> profileLiveData = new MutableEventLiveData<>();

    private MutableEventLiveData<ArrayList<RateResponseData>> tokenLessRateLiveData = new MutableEventLiveData<>();

    private MutableEventLiveData<String> idImage = new MutableEventLiveData<>();

    private MutableEventLiveData<String> dpImage = new MutableEventLiveData<>();

    public HomeRepo() {
        DaggerApiComponents.create().inject(this);
    }

    public void clear() {
        disposable.clear();
    }

    private MutableEventLiveData<DateTimeResponseDate> dateTimeLiveData = new MutableEventLiveData<>();

    private ArrayList<SelectionModal> employerTypeList;
    private MutableEventLiveData<ArrayList<SelectionModal>> employerTypeLiveData = new MutableEventLiveData<>();

    private ArrayList<SelectionModal> proffessionList;
    private MutableEventLiveData<ArrayList<SelectionModal>> proffessionLiveData = new MutableEventLiveData<>();

    private ArrayList<SelectionModal> salaryList;
    private MutableEventLiveData<ArrayList<SelectionModal>> salaryLiveData = new MutableEventLiveData<>();

    private MutableEventLiveData<Boolean> isProfilePicUpdated = new MutableEventLiveData<>();


    public MutableEventLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableEventLiveData<String> getLoadingError() {
        return loadingError;
    }

    public void getUserRate() {
        rateList = new ArrayList<>();
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();
        String Token = Universal.getInstance().getLoginResponsedata().getToken();
        if (Token != null) {
            Observable<RateResponse> observerCall = dataService.getRate("Bearer " + Token);
            disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<RateResponse>() {
                        @Override
                        public void onNext(RateResponse rateResponse) {
                            isLoading.postValue(false);
                            if (rateResponse.getRenewedtoken() != null) {
                                Universal.getInstance().getLoginResponsedata().setToken(rateResponse.getRenewedtoken());
                            }
                            if (rateResponse.getTokenstatus().toUpperCase().equals("FALSE")) {
                                isSessionValid.postValue(false);
                            } else {
                                if (rateResponse.getResponsestatus().toUpperCase().equals("TRUE") || rateResponse.getResponsestatus().toUpperCase().equals("")) {
                                    rateList = (ArrayList<RateResponseData>) rateResponse.getResponsedata();
                                    rateLiveData.postValue(rateList);
                                } else {
                                    loadingError.postValue(rateResponse.getResponsedescription());
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            loadingError.setValue(e.getMessage());
                            isLoading.postValue(false);
                            Log.d("dashboard", "Login throw an error: " + e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            Log.d("dashboard", "Login Call Completed");
                        }
                    })
            );
        }

    }

    public void getDashBoardRate() {
        rateList = new ArrayList<>();
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();
        Observable<DashboardRateModel> observerCall = dataService.getDashBoardRate("Bearer " + Universal.getInstance().getLoginResponsedata().getToken());
        disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<DashboardRateModel>() {
                    @Override
                    public void onNext(DashboardRateModel rateResponse) {
                        isLoading.postValue(false);
                        if (rateResponse.getTokenstatus().toUpperCase().equals("FALSE")) {
                            isSessionValid.postValue(false);
                        } else {
                            if (rateResponse.getRenewedtoken() != null) {
                                Universal.getInstance().getLoginResponsedata().setToken(rateResponse.getRenewedtoken());
                            }
                            if (rateResponse.getResponsestatus().toUpperCase().equals("TRUE") || rateResponse.getResponsestatus().toUpperCase().equals("")) {
                                String rate = rateResponse.getResponsedata().getBanktransferrate();
                                String currencyCode = rateResponse.getResponsedata().getCurrency();
                                String[] rateData = new String[]{rate, currencyCode};

                                dashboardRateLiveData.postValue(rateData);
                            } else {
                                loadingError.postValue(rateResponse.getResponsedescription());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        loadingError.setValue(e.getMessage());
                        isLoading.postValue(false);
                        Log.d("dashboard", "Login throw an error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d("dashboard", "Login Call Completed");
                    }
                })
        );
    }

    public MutableEventLiveData<ArrayList<RateResponseData>> getRateLiveData() {
        return rateLiveData;
    }

    public MutableEventLiveData<Boolean> getIsSessionValid() {
        return isSessionValid;
    }

    public void getCustomerCareNumber() {
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();
        Observable<SupportModel> observerCall = dataService.getCustomercareNumber("Bearer " + Universal.getInstance().getLoginResponsedata().getToken());
        disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<SupportModel>() {
                    @Override
                    public void onNext(SupportModel response) {
                        isLoading.postValue(false);
                        if (response.getRenewedtoken() != null) {
                            Universal.getInstance().getLoginResponsedata().setToken(response.getRenewedtoken());
                        }
                        if (response.getTokenstatus().toUpperCase().equals("FALSE")) {
                            isSessionValid.postValue(false);
                        } else {
                            if (response.getResponsestatus().toUpperCase().equals("TRUE")) {
                                contactLiveData.postValue(response.getResponsedata().getContactno());
                            } else {
                                loadingError.postValue(response.getResponsedescription());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        loadingError.setValue(e.getMessage());
                        isLoading.postValue(false);
                    }

                    @Override
                    public void onComplete() {
                    }
                })
        );
    }

    public MutableEventLiveData<String> getContactLiveData() {
        return contactLiveData;
    }

    public MutableEventLiveData<String[]> getDashboardRateLiveData() {
        return dashboardRateLiveData;
    }

    public void getProfileData() {
        rateList = new ArrayList<>();
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();
        String Token = Universal.getInstance().getLoginResponsedata().getToken();
        if (Token != null) {
            Observable<ProfileResponse> observerCall = dataService.getProfileData("Bearer " + Token, Universal.getInstance().getLoginResponsedata().getIdno());
            disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<ProfileResponse>() {
                        @Override
                        public void onNext(ProfileResponse profileResponse) {
                            isLoading.postValue(false);
                            if (profileResponse.getRenewedtoken() != null) {
                                Universal.getInstance().getLoginResponsedata().setToken(profileResponse.getRenewedtoken());
                            }
                            if (profileResponse.getTokenstatus().toUpperCase().equals("FALSE")) {
                                isSessionValid.postValue(false);
                            } else {
                                if (profileResponse.getResponsestatus().toUpperCase().equals("TRUE") || profileResponse.getResponsestatus().toUpperCase().equals("")) {
                                    profileLiveData.postValue(profileResponse.getResponsedata());
                                } else {
                                    loadingError.postValue(profileResponse.getResponsedescription());
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            loadingError.setValue(e.getMessage());
                            isLoading.postValue(false);
                            Log.d("dashboard", "Login throw an error: " + e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            Log.d("dashboard", "Login Call Completed");
                        }
                    })
            );
        }

    }

    public MutableEventLiveData<ProfileResponseData> getProfileLiveData() {
        return profileLiveData;
    }


    public void getTokenLessUserRate() {
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
                            rateList = (ArrayList<RateResponseData>) rateResponse.getResponsedata();
                            tokenLessRateLiveData.postValue(rateList);
                        } else {
                            loadingError.postValue(rateResponse.getResponsedescription());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        loadingError.setValue(e.getMessage());
                        isLoading.postValue(false);
                        Log.d("dashboard", "Login throw an error: " + e.getMessage());
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


    public void getIDFile(String id) {
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();
        String Token = Universal.getInstance().getLoginResponsedata().getToken();
        if (Token != null) {
            Observable<String> observerCall = dataService.getIDFile("Bearer " + Token, id);
            disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<String>() {
                        @Override
                        public void onNext(String image) {
                            isLoading.postValue(false);
                            idImage.postValue(image);
                        }

                        @Override
                        public void onError(Throwable e) {
                            loadingError.setValue(e.getMessage());
                            isLoading.postValue(false);
                        }

                        @Override
                        public void onComplete() {

                        }
                    })
            );
        }

    }

    public MutableEventLiveData<String> getIdImage() {
        return idImage;
    }

    public void getProfilePic(String id) {
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();
        String Token = Universal.getInstance().getLoginResponsedata().getToken();
        if (Token != null) {
            Observable<String> observerCall = dataService.getProfilePic("Bearer " + Token, id);
            disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<String>() {
                        @Override
                        public void onNext(String image) {
                            isLoading.postValue(false);
                            dpImage.postValue(image);
                        }

                        @Override
                        public void onError(Throwable e) {
                            loadingError.setValue(e.getMessage());
                            isLoading.postValue(false);
                        }

                        @Override
                        public void onComplete() {

                        }
                    })
            );
        }

    }


    public MutableEventLiveData<String> getDpImage() {
        return dpImage;
    }

    public void getCurrentDate(){
        isLoading.postValue(true);

        DataService dataService = serviceRequest.getDataService();
        Observable<DateTimeResponse> observerCall = dataService.getDateTime();

        disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<DateTimeResponse>() {
                    @Override
                    public void onNext(DateTimeResponse response) {
                        isLoading.postValue(false);

                        if (response.getResponsestatus().toUpperCase().equals("TRUE")) {
                            if (response.getResponsedata()!=null) {
                                dateTimeLiveData.postValue(response.getResponsedata());
                            } else {
                                loadingError.postValue(response.getResponsedescription());
                            }

                        } else {
                            loadingError.postValue(response.getResponsedescription());
                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                        loadingError.setValue(e.getMessage());
                        isLoading.postValue(false);

                    }

                    @Override
                    public void onComplete() {
                        isLoading.postValue(false);
                    }

                })
        );
    }

    public MutableEventLiveData<DateTimeResponseDate> getDateTimeLiveData() {
        return dateTimeLiveData;
    }

    public void getEmployerType(){
        employerTypeList = new ArrayList<>();
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();

        Observable<SelectionResponse> observerCall = dataService.getEmployerType();
        disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<SelectionResponse>() {
                    @Override
                    public void onNext(SelectionResponse response) {
                        isLoading.postValue(false);
                        if (response.getResponsestatus().toUpperCase().equals("TRUE") || response.getResponsestatus().toUpperCase().equals("")) {
                            employerTypeList = (ArrayList<SelectionModal>) response.getResponsedata();
                            employerTypeLiveData.postValue(employerTypeList);
                        } else {
                            loadingError.postValue(response.getResponsedescription());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        loadingError.setValue(e.getMessage());
                        isLoading.postValue(false);
                    }

                    @Override
                    public void onComplete() {
                    }
                })
        );
    }

    public MutableEventLiveData<ArrayList<SelectionModal>> getEmployerTypeLiveData() {
        return employerTypeLiveData;
    }

    public void getProffessions() {
        proffessionList = new ArrayList<>();
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();

        Observable<SelectionResponse> observerCall = dataService.getProffessions();
        disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<SelectionResponse>() {
                    @Override
                    public void onNext(SelectionResponse response) {
                        isLoading.postValue(false);
                        if (response.getResponsestatus().toUpperCase().equals("TRUE") || response.getResponsestatus().toUpperCase().equals("")) {
                            proffessionList = (ArrayList<SelectionModal>) response.getResponsedata();
                            proffessionLiveData.postValue(proffessionList);
                        } else {
                            loadingError.postValue(response.getResponsedescription());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        loadingError.setValue(e.getMessage());
                        isLoading.postValue(false);

                    }

                    @Override
                    public void onComplete() {

                    }
                })
        );
    }

    public MutableEventLiveData<ArrayList<SelectionModal>> getProffessionLivedata() {
        return proffessionLiveData;
    }

    public void getSalary() {
        salaryList = new ArrayList<>();
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();

        Observable<SelectionResponse> observerCall = dataService.getSalaryData();
        disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<SelectionResponse>() {
                    @Override
                    public void onNext(SelectionResponse response) {
                        isLoading.postValue(false);
                        if (response.getResponsestatus().toUpperCase().equals("TRUE") || response.getResponsestatus().toUpperCase().equals("")) {
                            salaryList = (ArrayList<SelectionModal>) response.getResponsedata();
                            salaryLiveData.postValue(salaryList);
                        } else {
                            loadingError.postValue(response.getResponsedescription());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        loadingError.setValue(e.getMessage());
                        isLoading.postValue(false);

                    }

                    @Override
                    public void onComplete() {

                    }
                })
        );
    }

    public MutableEventLiveData<ArrayList<SelectionModal>> getSalaryLiveData() {
        return salaryLiveData;
    }

    public void uploadProfilePicFile(File pic) {
        MultipartBody.Part idFile1 = MultipartBody.Part.createFormData("customerimage", "idFile0.jpeg", RequestBody.create(MediaType.parse("multipart/form-data"), pic));
        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), Universal.getInstance().getLoginResponsedata().getId());


        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();
        Observable<FileUploadResponse> otpObservable = dataService.uploadProfilePic(idFile1, id, "Bearer " +  Universal.getInstance().getLoginResponsedata().getToken());
        disposable.add(otpObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<FileUploadResponse>() {
                    @Override
                    public void onNext(FileUploadResponse response) {
                        isLoading.postValue(false);
                        if (response.getRenewedtoken() != null) {
                            Universal.getInstance().getLoginResponsedata().setToken(response.getRenewedtoken());
                        }
                        if (response.getResponsestatus().toUpperCase().equals("TRUE")) {
                            isProfilePicUpdated.postValue(true);
                        } else {
                            loadingError.postValue(response.getResponsedescription());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        isLoading.postValue(false);
                        loadingError.postValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        isLoading.postValue(false);
                    }
                }));
    }

    public MutableEventLiveData<Boolean> getIsProfilePicUpdated() {
        return isProfilePicUpdated;
    }
}
