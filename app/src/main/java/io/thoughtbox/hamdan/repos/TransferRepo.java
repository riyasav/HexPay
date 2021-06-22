package io.thoughtbox.hamdan.repos;

import android.util.Log;

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
import io.thoughtbox.hamdan.model.transferModel.GetRateResponse;
import io.thoughtbox.hamdan.model.transferModel.GetRateResponseData;
import io.thoughtbox.hamdan.model.transferModel.RateRequest;
import io.thoughtbox.hamdan.model.transferModel.SelectionModal;
import io.thoughtbox.hamdan.model.transferModel.SelectionResponse;
import io.thoughtbox.hamdan.model.transferModel.TransferRequestModel;
import io.thoughtbox.hamdan.model.transferModel.TransferResponse;
import io.thoughtbox.hamdan.model.transferModel.TransferResponseData;
import io.thoughtbox.hamdan.services.DataService;
import io.thoughtbox.hamdan.services.ServiceRequest;
import retrofit2.HttpException;

public class TransferRepo {

    @Inject
    public ServiceRequest serviceRequest;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableEventLiveData<Boolean> isLoading = new MutableEventLiveData<>();
    private MutableEventLiveData<String> loadingError = new MutableEventLiveData<>();
    private MutableEventLiveData<Boolean> isSessionValid = new MutableEventLiveData<>();
    private ArrayList<SelectionModal> modeList;

    private MutableEventLiveData<ArrayList<SelectionModal>> modeLiveData = new MutableEventLiveData<>();

    private ArrayList<SelectionModal> purposeList;

    private MutableEventLiveData<ArrayList<SelectionModal>> purposeLiveData = new MutableEventLiveData<>();

    private ArrayList<SelectionModal> sourceList;

    private MutableEventLiveData<ArrayList<SelectionModal>> sourceLiveData = new MutableEventLiveData<>();

    private ArrayList<SelectionModal> bankList;

    private MutableEventLiveData<ArrayList<SelectionModal>> bankLiveData = new MutableEventLiveData<>();

    private MutableEventLiveData<GetRateResponseData> rateResponseLiveData = new MutableEventLiveData<>();

    private MutableEventLiveData<TransferResponseData> transferResponseLiveData = new MutableEventLiveData<>();


    public TransferRepo() {
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

    public void getMode() {
        modeList = new ArrayList<>();
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();

        Observable<SelectionResponse> observerCall = dataService.getPaymentModes("Bearer " + Universal.getInstance().getLoginResponsedata().getToken());
        disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<SelectionResponse>() {
                    @Override
                    public void onNext(SelectionResponse response) {
                        isLoading.postValue(false);
                        if (response.getTokenstatus().toUpperCase().equals("FALSE")) {
                            isSessionValid.postValue(false);
                        } else {
                            if (response.getResponsestatus().toUpperCase().equals("TRUE") || response.getResponsestatus().toUpperCase().equals("")) {
                                modeList = (ArrayList<SelectionModal>) response.getResponsedata();
                                modeLiveData.postValue(modeList);
                                Log.d("bank", "" + modeList.size());
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

    public MutableEventLiveData<ArrayList<SelectionModal>> getModeLiveData() {
        return modeLiveData;
    }

    public void getPurpose() {
        purposeList = new ArrayList<>();
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();

        Observable<SelectionResponse> observerCall = dataService.getPurpose("Bearer " + Universal.getInstance().getLoginResponsedata().getToken());
        disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<SelectionResponse>() {
                    @Override
                    public void onNext(SelectionResponse response) {
                        isLoading.postValue(false);
                        if (response.getTokenstatus().toUpperCase().equals("FALSE")) {
                            isSessionValid.postValue(false);
                        } else {
                            if (!response.getRenewedtoken().equals("NIL")) {
                                Universal.getInstance().getLoginResponsedata().setToken(response.getRenewedtoken());
                            }
                            if (response.getResponsestatus().toUpperCase().equals("TRUE") || response.getResponsestatus().toUpperCase().equals("")) {
                                purposeList = (ArrayList<SelectionModal>) response.getResponsedata();
                                purposeLiveData.postValue(purposeList);
                                Log.d("bank", "" + purposeList.size());
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

    public MutableEventLiveData<ArrayList<SelectionModal>> getPurposeLiveData() {
        return purposeLiveData;
    }

    public void getSource() {
        sourceList = new ArrayList<>();
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();

        Observable<SelectionResponse> observerCall = dataService.getSource("Bearer " + Universal.getInstance().getLoginResponsedata().getToken());
        disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<SelectionResponse>() {
                    @Override
                    public void onNext(SelectionResponse response) {
                        isLoading.postValue(false);
                        if (response.getTokenstatus().toUpperCase().equals("FALSE")) {
                            isSessionValid.postValue(false);
                        } else {
                            if (!response.getRenewedtoken().equals("NIL")) {
                                Universal.getInstance().getLoginResponsedata().setToken(response.getRenewedtoken());
                            }
                            if (response.getResponsestatus().toUpperCase().equals("TRUE") || response.getResponsestatus().toUpperCase().equals("")) {
                                sourceList = (ArrayList<SelectionModal>) response.getResponsedata();
                                sourceLiveData.postValue(sourceList);
                                Log.d("bank", "" + sourceList.size());
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

    public MutableEventLiveData<ArrayList<SelectionModal>> getSourceLiveData() {
        return sourceLiveData;
    }

    public void getBank() {
        bankList = new ArrayList<>();
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();

        Observable<SelectionResponse> observerCall = dataService.getBank("Bearer " + Universal.getInstance().getLoginResponsedata().getToken());
        disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<SelectionResponse>() {
                    @Override
                    public void onNext(SelectionResponse response) {
                        isLoading.postValue(false);
                        if (response.getTokenstatus().toUpperCase().equals("FALSE")) {
                            isSessionValid.postValue(false);
                        } else {
                            if (!response.getRenewedtoken().equals("NIL")) {
                                Universal.getInstance().getLoginResponsedata().setToken(response.getRenewedtoken());
                            }
                            if (response.getResponsestatus().toUpperCase().equals("TRUE") || response.getResponsestatus().toUpperCase().equals("")) {
                                bankList = (ArrayList<SelectionModal>) response.getResponsedata();
                                bankLiveData.postValue(bankList);
                                Log.d("bank", "" + bankList.size());
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

    public MutableEventLiveData<ArrayList<SelectionModal>> getBankLiveData() {
        return bankLiveData;
    }


    public void getRate(RateRequest params) {
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();

        Observable<GetRateResponse> observerCall = dataService.getUserRate("Bearer " + Universal.getInstance().getLoginResponsedata().getToken(), params);
        disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<GetRateResponse>() {
                    @Override
                    public void onNext(GetRateResponse response) {
                        isLoading.postValue(false);
                        if (response.getTokenstatus().toUpperCase().equals("FALSE")) {
                            isSessionValid.postValue(false);
                        } else {
                            if (!response.getRenewedtoken().equals("NIL")) {
                                Universal.getInstance().getLoginResponsedata().setToken(response.getRenewedtoken());
                            }
                            if (response.getResponsestatus().toUpperCase().equals("TRUE")) {
                                rateResponseLiveData.postValue(response.getResponsedata());
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

    public MutableEventLiveData<GetRateResponseData> getRateResponseLiveData() {
        return rateResponseLiveData;
    }

    public void createTransfer(TransferRequestModel params) {
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();

        Observable<TransferResponse> observerCall = dataService.doTransfer(params, "Bearer " + Universal.getInstance().getLoginResponsedata().getToken());
        disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<TransferResponse>() {
                    @Override
                    public void onNext(TransferResponse response) {
                        isLoading.postValue(false);
                        if (response.getTokenstatus().toUpperCase().equals("FALSE")) {
                            isSessionValid.postValue(false);
                        } else {
                            if (!response.getRenewedtoken().equals("NIL")) {
                                Universal.getInstance().getLoginResponsedata().setToken(response.getRenewedtoken());
                            }
                            if (response.getResponsestatus().toUpperCase().equals("TRUE")) {
                                transferResponseLiveData.postValue(response.getResponsedata());
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

    public MutableEventLiveData<TransferResponseData> getTransferResponseLiveData() {
        return transferResponseLiveData;
    }

    public void createFastCashTransfer(TransferRequestModel params) {
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();

        Observable<TransferResponse> observerCall = dataService.doFastCashTransfer(params, "Bearer " + Universal.getInstance().getLoginResponsedata().getToken());
        disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<TransferResponse>() {
                    @Override
                    public void onNext(TransferResponse response) {
                        isLoading.postValue(false);
                        if (response.getTokenstatus().toUpperCase().equals("FALSE")) {
                            isSessionValid.postValue(false);
                        } else {
                            if (!response.getRenewedtoken().equals("NIL")) {
                                Universal.getInstance().getLoginResponsedata().setToken(response.getRenewedtoken());
                            }
                            if (response.getResponsestatus().toUpperCase().equals("TRUE")) {
                                transferResponseLiveData.postValue(response.getResponsedata());
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

    public MutableEventLiveData<Boolean> getIsSessionValid() {
        return isSessionValid;
    }
}
