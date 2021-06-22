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
import io.thoughtbox.hamdan.utls.MutableEventLiveData;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import io.thoughtbox.hamdan.model.FileUploadResponse;
import io.thoughtbox.hamdan.model.bankBenModel.CreateBenResponse;
import io.thoughtbox.hamdan.model.bankBenModel.CreateBenResponseData;
import io.thoughtbox.hamdan.model.customerSupport.SupportModel;
import io.thoughtbox.hamdan.model.transferModel.SelectionModal;
import io.thoughtbox.hamdan.model.transferModel.SelectionResponse;
import io.thoughtbox.hamdan.services.DataService;
import io.thoughtbox.hamdan.services.ServiceRequest;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.HttpException;

public class RegisterRepo {
    @Inject
    public ServiceRequest serviceRequest;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableEventLiveData<Boolean> isLoading = new MutableEventLiveData<>();
    private MutableEventLiveData<String> loadingError = new MutableEventLiveData<>();
    private ArrayList<SelectionModal> countryList;
    private MutableEventLiveData<ArrayList<SelectionModal>> countryLiveData = new MutableEventLiveData<>();
    private ArrayList<SelectionModal> idTypeList;
    private MutableEventLiveData<ArrayList<SelectionModal>> idTypeLiveData = new MutableEventLiveData<>();
    private ArrayList<SelectionModal> proffessionList;
    private MutableEventLiveData<ArrayList<SelectionModal>> proffessionLiveData = new MutableEventLiveData<>();
    private ArrayList<SelectionModal> salaryList;
    private MutableEventLiveData<ArrayList<SelectionModal>> salaryLiveData = new MutableEventLiveData<>();
    private MutableEventLiveData<CreateBenResponseData> registerLiveData = new MutableEventLiveData<>();
    private MutableEventLiveData<Boolean> videoLiveData = new MutableEventLiveData<>();
    private MutableEventLiveData<String> videLoadingError = new MutableEventLiveData<>();
    private MutableEventLiveData<Boolean> idFilesLiveData = new MutableEventLiveData<>();
    private MutableEventLiveData<String> idFileLoadingError = new MutableEventLiveData<>();
    private MutableEventLiveData<Boolean> signatureLiveData = new MutableEventLiveData<>();
    private MutableEventLiveData<String> signatureLoadingError = new MutableEventLiveData<>();
    private MutableEventLiveData<String> contactLiveData = new MutableEventLiveData<>();
    private MutableEventLiveData<DateTimeResponseDate> dateTimeLiveData = new MutableEventLiveData<>();
    private MutableEventLiveData<Boolean> isSessionValid = new MutableEventLiveData<>();

    public RegisterRepo() {

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

    public void getCountry() {
        countryList = new ArrayList<>();
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();

        Observable<SelectionResponse> observerCall = dataService.getCountryList();
        disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<SelectionResponse>() {
                    @Override
                    public void onNext(SelectionResponse response) {
                        isLoading.postValue(false);
                        if (response.getResponsestatus().toUpperCase().equals("TRUE") || response.getResponsestatus().toUpperCase().equals("")) {
                            countryList = (ArrayList<SelectionModal>) response.getResponsedata();
                            countryLiveData.postValue(countryList);
                            Log.d("bank", "" + countryList.size());
                        } else {
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

    public MutableEventLiveData<ArrayList<SelectionModal>> getCountryLiveData() {
        return countryLiveData;
    }

    public void getidType() {
        idTypeList = new ArrayList<>();
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();

        Observable<SelectionResponse> observerCall = dataService.getIdType();
        disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<SelectionResponse>() {
                    @Override
                    public void onNext(SelectionResponse response) {
                        isLoading.postValue(false);
                        if (response.getResponsestatus().toUpperCase().equals("TRUE") || response.getResponsestatus().toUpperCase().equals("")) {
                            idTypeList = (ArrayList<SelectionModal>) response.getResponsedata();
                            idTypeLiveData.postValue(idTypeList);
                        } else {
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

    public MutableEventLiveData<ArrayList<SelectionModal>> getIdTypeLiveData() {
        return idTypeLiveData;
    }

    public MutableEventLiveData<ArrayList<SelectionModal>> getProffessionLivedata() {
        return proffessionLiveData;
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

    public MutableEventLiveData<ArrayList<SelectionModal>> getSalaryLiveData() {
        return salaryLiveData;
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

//        private static final MediaType MEDIA_TYPE_PNG = MediaType.get("image/jpg");

    public void createRegister(File idfile0,
                               File idfile1,
                               File video,
                               File signature,
                               String fname,
                               String mname,
                               String lname,
                               String mobile,
                               String email,
                               String city,
                               String state,
                               String country,
                               String idtype,
                               String idnum,
                               String IdExpy,
                               String salary,
                               String profession,
                               String employer

    ) {

        RequestBody idFile0 = RequestBody.create(MediaType.parse("image/jpg"), idfile0);
        RequestBody idFile1 = RequestBody.create(MediaType.parse("multipart/form-data"), idfile1);
        RequestBody videoFile = RequestBody.create(MediaType.parse("multipart/form-data"), "");
        RequestBody sign = RequestBody.create(MediaType.parse("multipart/form-data"), "");

        RequestBody firstName = RequestBody.create(MediaType.parse("text/plain"), fname);
        RequestBody middleName = RequestBody.create(MediaType.parse("text/plain"), mname);
        RequestBody lastName = RequestBody.create(MediaType.parse("text/plain"), lname);

        RequestBody contact = RequestBody.create(MediaType.parse("text/plain"), mobile);
        RequestBody mail = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody address1 = RequestBody.create(MediaType.parse("text/plain"), city);
        RequestBody address2 = RequestBody.create(MediaType.parse("text/plain"), state);
        RequestBody nationality = RequestBody.create(MediaType.parse("text/plain"), country);
        RequestBody idtyp = RequestBody.create(MediaType.parse("text/plain"), idtype);
        RequestBody idno = RequestBody.create(MediaType.parse("text/plain"), idnum);
        RequestBody idexpiry = RequestBody.create(MediaType.parse("text/plain"), IdExpy);

        RequestBody salaryData = RequestBody.create(MediaType.parse("text/plain"), salary);
        RequestBody professionalData = RequestBody.create(MediaType.parse("text/plain"), profession);
        RequestBody employerData = RequestBody.create(MediaType.parse("text/plain"), employer);
        RequestBody FilesCount = RequestBody.create(MediaType.parse("text/plain"), "2");


        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();
//        idFile0, idFile1,videoFile,sign,
        Observable<CreateBenResponse> otpObservable = dataService.createRegister(
                idFile0,
                idFile1,
                videoFile,
                sign,
                firstName,
                middleName,
                lastName,
                contact,
                mail,
                address1,
                address2,
                nationality,
                idtyp,
                idno,
                idexpiry,
                salaryData,
                professionalData,
                employerData,
                FilesCount
        );

        disposable.add(otpObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<CreateBenResponse>() {
                    @Override
                    public void onNext(CreateBenResponse response) {
                        isLoading.postValue(false);
                        registerLiveData.postValue(response.getResponsedata());
                        Log.d("responseDesc", response.getResponsedescription());
                        Log.d("responseStatus", response.getResponsestatus());
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
                }));

    }

    public MutableEventLiveData<CreateBenResponseData> getRegisterLiveData() {
        return registerLiveData;
    }

    public void uploadVideoFiles(File videoFile, String token) {
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("video", "selfieVideo.mp4", RequestBody.create(MediaType.parse("multipart/form-data"), videoFile));

        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();
        Observable<FileUploadResponse> otpObservable = dataService.uploadVideo(filePart, "Bearer " + token);
        disposable.add(otpObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<FileUploadResponse>() {
                    @Override
                    public void onNext(FileUploadResponse response) {
                        isLoading.postValue(false);
                        if (response.getResponsestatus().toUpperCase().equals("TRUE")) {
                            videoLiveData.postValue(true);
                        } else {
                            videLoadingError.postValue(response.getResponsedescription());
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
                }));
    }

    public void uploadIdFiles(File file, File file2, String token) {
        MultipartBody.Part idFile1 = MultipartBody.Part.createFormData("idfiles0", "idFile0.jpeg", RequestBody.create(MediaType.parse("multipart/form-data"), file));
        MultipartBody.Part idFile2 = MultipartBody.Part.createFormData("idfiles1", "idFiles1.jpeg", RequestBody.create(MediaType.parse("multipart/form-data"), file2));


        RequestBody filesCount = RequestBody.create(MediaType.parse("text/plain"), "2");


        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();
        Observable<FileUploadResponse> otpObservable = dataService.uploadIdfiles(idFile1, idFile2, filesCount, "Bearer " + token);
        disposable.add(otpObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<FileUploadResponse>() {
                    @Override
                    public void onNext(FileUploadResponse response) {
                        isLoading.postValue(false);
                        if (response.getResponsestatus().toUpperCase().equals("TRUE")) {
                            idFilesLiveData.postValue(true);
                        } else {
                            idFileLoadingError.postValue(response.getResponsedescription());
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
                }));
    }

    public void uploadSignature(File file, String token) {
//        RequestBody signature = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part signature = MultipartBody.Part.createFormData("signature", "signature.jpeg", RequestBody.create(MediaType.parse("multipart/form-data"), file));
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();
        Observable<FileUploadResponse> otpObservable = dataService.uploadSignature(signature, "Bearer " + token);
        disposable.add(otpObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<FileUploadResponse>() {
                    @Override
                    public void onNext(FileUploadResponse response) {

                        isLoading.postValue(false);
                        if (response.getResponsestatus().toUpperCase().equals("TRUE")) {
                            signatureLiveData.postValue(true);
                        } else {
                            signatureLoadingError.postValue(response.getResponsedescription());
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
                }));
    }

    public MutableEventLiveData<Boolean> getVideoLiveData() {
        return videoLiveData;
    }

    public MutableEventLiveData<String> getVideLoadingError() {
        return videLoadingError;
    }

    public MutableEventLiveData<Boolean> getIdFilesLiveData() {
        return idFilesLiveData;
    }

    public MutableEventLiveData<String> getIdFileLoadingError() {
        return idFileLoadingError;
    }

    public MutableEventLiveData<Boolean> getSignatureLiveData() {
        return signatureLiveData;
    }

    public MutableEventLiveData<String> getSignatureLoadingError() {
        return signatureLoadingError;
    }

    public void getCustomerCareNumber() {
        isLoading.postValue(true);
        DataService dataService = serviceRequest.getDataService();
        Observable<SupportModel> observerCall = dataService.getCustomercareNumberRegister();
        disposable.add(observerCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<SupportModel>() {
                    @Override
                    public void onNext(SupportModel response) {
                        isLoading.postValue(false);
                        if (response.getResponsestatus().toUpperCase().equals("TRUE")) {
                            contactLiveData.postValue(response.getResponsedata().getContactno());
                        } else {
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

    public MutableEventLiveData<String> getContactLiveData() {
        return contactLiveData;
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

    public MutableEventLiveData<DateTimeResponseDate> getDateTimeLiveData() {
        return dateTimeLiveData;
    }
}
