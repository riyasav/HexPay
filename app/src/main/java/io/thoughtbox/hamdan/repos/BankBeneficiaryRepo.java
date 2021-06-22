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
import io.thoughtbox.hamdan.model.bankBenModel.BankBeneficiaryResponse;
import io.thoughtbox.hamdan.model.bankBenModel.BankBeneficiaryResponseData;
import io.thoughtbox.hamdan.model.cashPickUpModel.CashPickUpResponse;
import io.thoughtbox.hamdan.model.cashPickUpModel.CashPickUpResponseData;
import io.thoughtbox.hamdan.model.loginModel.Otp;
import io.thoughtbox.hamdan.services.DataService;
import io.thoughtbox.hamdan.services.ServiceRequest;
import retrofit2.HttpException;

public class BankBeneficiaryRepo {
    @Inject
    public ServiceRequest serviceRequest;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableEventLiveData<Boolean> isLoading = new MutableEventLiveData<>();
    private MutableEventLiveData<String> loadingError = new MutableEventLiveData<>();
    private MutableEventLiveData<Boolean> isSessionValid = new MutableEventLiveData<>();
    private ArrayList<BankBeneficiaryResponseData> bankBenList;
    private ArrayList<CashPickUpResponseData> cashPickupBenList;
    private MutableEventLiveData<ArrayList<BankBeneficiaryResponseData>> bankBenLiveData = new MutableEventLiveData<>();
    private MutableEventLiveData<ArrayList<CashPickUpResponseData>> cashPickupBenLiveData = new MutableEventLiveData<>();
    private MutableEventLiveData<Boolean> isBenDeleted = new MutableEventLiveData<>();
    private MutableEventLiveData<Boolean> isCashBenDeleted = new MutableEventLiveData<>();

    public BankBeneficiaryRepo() {
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

    public void getBankBenList() {
        bankBenList = new ArrayList<>();
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();
        Observable<BankBeneficiaryResponse> observerCall = dataService.getBankBeneficiary("Bearer " + Universal.getInstance().getLoginResponsedata().getToken());
        disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<BankBeneficiaryResponse>() {
                    @Override
                    public void onNext(BankBeneficiaryResponse bankBeneficiaryResponse) {
                        isLoading.postValue(false);
                        if (bankBeneficiaryResponse.getTokenstatus().toUpperCase().equals("FALSE")) {
                            isSessionValid.postValue(false);
                        } else {
                            if (!bankBeneficiaryResponse.getRenewedtoken().equals("NIL")) {
                                Universal.getInstance().getLoginResponsedata().setToken(bankBeneficiaryResponse.getRenewedtoken());
                            }
                            if (bankBeneficiaryResponse.getResponsestatus().toUpperCase().equals("TRUE") || bankBeneficiaryResponse.getResponsestatus().toUpperCase().equals("")) {
                                bankBenList = (ArrayList<BankBeneficiaryResponseData>) bankBeneficiaryResponse.getResponsedata();
                                bankBenLiveData.postValue(bankBenList);
                                Log.d("bank", "" + bankBenList.size());
                            } else {
                                loadingError.postValue(bankBeneficiaryResponse.getResponsedescription());
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

    public MutableEventLiveData<ArrayList<BankBeneficiaryResponseData>> getBankBenLiveData() {
        return bankBenLiveData;
    }


    // ---------------------------------------------------------------------------------------------


    public MutableEventLiveData<ArrayList<CashPickUpResponseData>> getCashPickupBenLiveData() {
        return cashPickupBenLiveData;
    }

    public void getCashPickUpList() {
        cashPickupBenList = new ArrayList<>();
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();
        Observable<CashPickUpResponse> observerCall = dataService.getCashPickUpBeneficiary("Bearer " + Universal.getInstance().getLoginResponsedata().getToken());
        disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<CashPickUpResponse>() {
                    @Override
                    public void onNext(CashPickUpResponse cashPickUpResponse) {
                        isLoading.postValue(false);
                        if (cashPickUpResponse.getTokenstatus().toUpperCase().equals("FALSE")) {
                            isSessionValid.postValue(false);
                        } else {
                            if (!cashPickUpResponse.getRenewedtoken().equals("NIL")) {
                                Universal.getInstance().getLoginResponsedata().setToken(cashPickUpResponse.getRenewedtoken());
                            }
                            if (cashPickUpResponse.getResponsestatus().toUpperCase().equals("TRUE") || cashPickUpResponse.getResponsestatus().toUpperCase().equals("")) {
                                cashPickupBenList = (ArrayList<CashPickUpResponseData>) cashPickUpResponse.getResponsedata();
                                cashPickupBenLiveData.postValue(cashPickupBenList);

                            } else {
                                loadingError.postValue(cashPickUpResponse.getResponsedescription());
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


    public void deleteBankBen(JSONObject params) {
        JsonParser jsonParser = new JsonParser();
        JsonObject gsonParams = (JsonObject) jsonParser.parse(params.toString());

        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();
        Observable<Otp> observerCall = dataService.deleteBankBen(gsonParams, "Bearer " + Universal.getInstance().getLoginResponsedata().getToken());
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
                                    isBenDeleted.postValue(true);
                                } else {
                                    isBenDeleted.postValue(false);
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

    public MutableEventLiveData<Boolean> getIsBenDeleted() {
        return isBenDeleted;
    }

    public void deleteCashBen(JSONObject params) {
        JsonParser jsonParser = new JsonParser();
        JsonObject gsonParams = (JsonObject) jsonParser.parse(params.toString());

        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();
        Observable<Otp> observerCall = dataService.deleteCashkBen(gsonParams, "Bearer " + Universal.getInstance().getLoginResponsedata().getToken());
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
                                    isCashBenDeleted.postValue(true);
                                } else {
                                    isCashBenDeleted.postValue(false);
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

    public MutableEventLiveData<Boolean> getIsCashBenDeleted() {
        return isCashBenDeleted;
    }

    public MutableEventLiveData<Boolean> getIsSessionValid() {
        return isSessionValid;
    }
}
