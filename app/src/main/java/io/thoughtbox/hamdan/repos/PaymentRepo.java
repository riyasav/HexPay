package io.thoughtbox.hamdan.repos;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.thoughtbox.hamdan.utls.MutableEventLiveData;
import io.thoughtbox.hamdan.global.Universal;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import io.thoughtbox.hamdan.services.DataService;
import io.thoughtbox.hamdan.services.ServiceRequest;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.HttpException;

public class PaymentRepo {
    @Inject
    public ServiceRequest serviceRequest;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableEventLiveData<Boolean> isLoading = new MutableEventLiveData<>();
    private MutableEventLiveData<String> loadingError = new MutableEventLiveData<>();
    private MutableEventLiveData<String> htmlLiveData = new MutableEventLiveData<>();
    private MutableEventLiveData<Boolean> isSessionValid = new MutableEventLiveData<>();

    public PaymentRepo() {
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

    public void getPaymentGateway(String transferType, String referenceNum) {

        RequestBody txntype = RequestBody.create(MediaType.parse("text/plain"), transferType);
        RequestBody refNum = RequestBody.create(MediaType.parse("text/plain"), referenceNum);

        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();

        Observable<String> otpObservable = dataService.paymentGatewayRequest(txntype, refNum,
                "Bearer " + Universal.getInstance().getLoginResponsedata().getToken());
//       String Token=Universal.getInstance().getLoginResponsedata().getToken();
        disposable.add(otpObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String html) {
                        if (html != null) {
                            isLoading.postValue(false);
                            htmlLiveData.postValue(html);
                        } else {
                            isLoading.postValue(false);
                            loadingError.postValue("Empty response");
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

    public MutableEventLiveData<String> getHtmlLiveData() {
        return htmlLiveData;
    }
}
