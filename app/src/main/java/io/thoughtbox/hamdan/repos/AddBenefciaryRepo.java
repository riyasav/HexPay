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
import io.thoughtbox.hamdan.model.bankBenModel.BenCreate;
import io.thoughtbox.hamdan.model.bankBenModel.DetailData;
import io.thoughtbox.hamdan.model.bankBenModel.DetailModel;
import io.thoughtbox.hamdan.model.benCreate.CreateBeneficiaryResponse;
import io.thoughtbox.hamdan.model.beneficiary.BanksRequest;
import io.thoughtbox.hamdan.model.beneficiary.BranchRequest;
import io.thoughtbox.hamdan.model.transferModel.SelectionModal;
import io.thoughtbox.hamdan.model.transferModel.SelectionResponse;
import io.thoughtbox.hamdan.services.DataService;
import io.thoughtbox.hamdan.services.ServiceRequest;
import retrofit2.HttpException;

public class AddBenefciaryRepo {

    @Inject
    public ServiceRequest serviceRequest;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableEventLiveData<Boolean> isLoading = new MutableEventLiveData<>();
    private MutableEventLiveData<String> loadingError = new MutableEventLiveData<>();
    private MutableEventLiveData<Boolean> isSessionValid = new MutableEventLiveData<>();
    private ArrayList<SelectionModal> countryList;
    private MutableEventLiveData<ArrayList<SelectionModal>> countryLiveData = new MutableEventLiveData<>();
    private ArrayList<SelectionModal> txntypeList;
    private MutableEventLiveData<ArrayList<SelectionModal>> txntypeLiveData = new MutableEventLiveData<>();
    private ArrayList<DetailData> bankList;
    private MutableEventLiveData<ArrayList<DetailData>> bankLiveData = new MutableEventLiveData<>();
    private ArrayList<SelectionModal> branchList;
    private MutableEventLiveData<ArrayList<SelectionModal>> branchLiveData = new MutableEventLiveData<>();
    private ArrayList<SelectionModal> relationList;
    private MutableEventLiveData<ArrayList<SelectionModal>> relationLiveData = new MutableEventLiveData<>();
    private ArrayList<SelectionModal> nationalityList;
    private MutableEventLiveData<ArrayList<SelectionModal>> nationalityLiveData = new MutableEventLiveData<>();
    private MutableEventLiveData<BenCreate> createBankLiveData = new MutableEventLiveData<>();

    public AddBenefciaryRepo() {
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

    public void getcountry() {
        countryList = new ArrayList<>();
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();

        Observable<SelectionResponse> observerCall = dataService.getCountries("Bearer " + Universal.getInstance().getLoginResponsedata().getToken());
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
                                countryList = (ArrayList<SelectionModal>) response.getResponsedata();
                                countryLiveData.postValue(countryList);

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

    public MutableEventLiveData<ArrayList<SelectionModal>> getCountryLiveData() {
        return countryLiveData;
    }

    public void getTxntype(String countryId) {
        txntypeList = new ArrayList<>();
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();

        Observable<SelectionResponse> observerCall = dataService.getTransferType(countryId, "Bearer " + Universal.getInstance().getLoginResponsedata().getToken());
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
                                txntypeList = (ArrayList<SelectionModal>) response.getResponsedata();
                                txntypeLiveData.postValue(txntypeList);

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

    public MutableEventLiveData<ArrayList<SelectionModal>> getTxntypeLiveData() {
        return txntypeLiveData;
    }

    public void getbank(BanksRequest params) {
        bankList = new ArrayList<>();
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();

        Observable<DetailModel> observerCall = dataService.getBanks(params, "Bearer " + Universal.getInstance().getLoginResponsedata().getToken());
        disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<DetailModel>() {
                    @Override
                    public void onNext(DetailModel response) {
                        isLoading.postValue(false);
                        if (response.getTokenstatus().toUpperCase().equals("FALSE")) {
                            isSessionValid.postValue(false);
                        } else {
                            if (!response.getRenewedtoken().equals("NIL")) {
                                Universal.getInstance().getLoginResponsedata().setToken(response.getRenewedtoken());
                            }
                            if (response.getResponsestatus().toUpperCase().equals("TRUE") || response.getResponsestatus().toUpperCase().equals("")) {
                                bankList = (ArrayList<DetailData>) response.getResponsedata();
                                bankLiveData.postValue(bankList);

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

    public MutableEventLiveData<ArrayList<DetailData>> getBankLiveData() {
        return bankLiveData;
    }

    public void getbranch(BranchRequest params) {
        branchList = new ArrayList<>();
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();

        Observable<SelectionResponse> observerCall = dataService.getBranches(params, "Bearer " + Universal.getInstance().getLoginResponsedata().getToken());
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
                                branchList = (ArrayList<SelectionModal>) response.getResponsedata();
                                branchLiveData.postValue(branchList);

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

    public MutableEventLiveData<ArrayList<SelectionModal>> getBranchLiveData() {
        return branchLiveData;
    }

    public void getRelation() {
        relationList = new ArrayList<>();
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();

        Observable<SelectionResponse> observerCall = dataService.getRelation("Bearer " + Universal.getInstance().getLoginResponsedata().getToken());
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
                                relationList = (ArrayList<SelectionModal>) response.getResponsedata();
                                relationLiveData.postValue(relationList);

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

    public MutableEventLiveData<ArrayList<SelectionModal>> getRelationLiveData() {
        return relationLiveData;
    }

    public void getNationality() {
        nationalityList = new ArrayList<>();
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();

        Observable<SelectionResponse> observerCall = dataService.getNationalities("Bearer " + Universal.getInstance().getLoginResponsedata().getToken());
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
                                nationalityList = (ArrayList<SelectionModal>) response.getResponsedata();
                                nationalityLiveData.postValue(nationalityList);

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

    public MutableEventLiveData<ArrayList<SelectionModal>> getNationalityLiveData() {
        return nationalityLiveData;
    }

    public void createBankBen(JSONObject params) {
        JsonParser jsonParser = new JsonParser();
        JsonObject gsonParams = (JsonObject) jsonParser.parse(params.toString());
        Log.d("json", gsonParams.toString());
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();

        Observable<CreateBeneficiaryResponse> observerCall = dataService.createBankBeneficiary(gsonParams, "Bearer " + Universal.getInstance().getLoginResponsedata().getToken());
        disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<CreateBeneficiaryResponse>() {
                    @Override
                    public void onNext(CreateBeneficiaryResponse response) {
                        isLoading.postValue(false);
                        if (response.getTokenstatus().toUpperCase().equals("FALSE")) {
                            isSessionValid.postValue(false);
                        } else {
                            if (!response.getRenewedtoken().equals("NIL")) {
                                Universal.getInstance().getLoginResponsedata().setToken(response.getRenewedtoken());
                            }
                            if (response.getResponsestatus().toUpperCase().equals("SUCCESS") || response.getResponsestatus().toUpperCase().equals("")) {
                                createBankLiveData.postValue(response.getResponsedata());

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

    public MutableEventLiveData<BenCreate> getCreateBankLiveData() {
        return createBankLiveData;
    }

    public MutableEventLiveData<Boolean> getIsSessionValid() {
        return isSessionValid;
    }
}
