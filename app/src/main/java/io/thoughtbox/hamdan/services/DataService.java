package io.thoughtbox.hamdan.services;


import com.google.gson.JsonObject;

import io.reactivex.Observable;
import io.thoughtbox.hamdan.model.BannerModel;
import io.thoughtbox.hamdan.model.BannerResponse;
import io.thoughtbox.hamdan.model.DateTimeResponse;
import io.thoughtbox.hamdan.model.FileUploadResponse;
import io.thoughtbox.hamdan.model.bankBenModel.BankBeneficiaryResponse;
import io.thoughtbox.hamdan.model.bankBenModel.CheckOutResponse;
import io.thoughtbox.hamdan.model.bankBenModel.CreateBenResponse;
import io.thoughtbox.hamdan.model.bankBenModel.DetailModel;
import io.thoughtbox.hamdan.model.benCreate.CreateBeneficiaryResponse;
import io.thoughtbox.hamdan.model.beneficiary.BanksRequest;
import io.thoughtbox.hamdan.model.beneficiary.BranchRequest;
import io.thoughtbox.hamdan.model.cashPickUpModel.CashPickUpResponse;
import io.thoughtbox.hamdan.model.checkUserId.CheckUserResponse;
import io.thoughtbox.hamdan.model.customerSupport.SupportModel;
import io.thoughtbox.hamdan.model.dictionaryModel.DictionaryResponse;
import io.thoughtbox.hamdan.model.invoice.ConfirmPaymentResponse;
import io.thoughtbox.hamdan.model.invoice.InvoiceRequestModel;
import io.thoughtbox.hamdan.model.languageModel.LanguageResponse;
import io.thoughtbox.hamdan.model.loginModel.LoginRequestModel;
import io.thoughtbox.hamdan.model.loginModel.LoginResponse;
import io.thoughtbox.hamdan.model.loginModel.Otp;
import io.thoughtbox.hamdan.model.loginModel.OtpRequestModel;
import io.thoughtbox.hamdan.model.notificationModel.NotificationResponseModel;
import io.thoughtbox.hamdan.model.profile.ProfileResponse;
import io.thoughtbox.hamdan.model.rateModels.DashboardRateModel;
import io.thoughtbox.hamdan.model.rateModels.RateResponse;
import io.thoughtbox.hamdan.model.reportModel.ReportResponse;
import io.thoughtbox.hamdan.model.reportModel.TrackTransferResponse;
import io.thoughtbox.hamdan.model.rewardsModel.RewardsResponse;
import io.thoughtbox.hamdan.model.transferModel.GetRateResponse;
import io.thoughtbox.hamdan.model.transferModel.RateRequest;
import io.thoughtbox.hamdan.model.transferModel.SelectionResponse;
import io.thoughtbox.hamdan.model.transferModel.TransferRequestModel;
import io.thoughtbox.hamdan.model.transferModel.TransferResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface DataService {

    @POST("users/login")
    Observable<LoginResponse> userLogin(@Body LoginRequestModel params);

    @POST("users/biometriclogin")
    Observable<LoginResponse> biometricLogin(@Body JsonObject params);

    @POST("users/resetpassword")
    Observable<Otp> forgotPassword(@Body JsonObject params);


    @POST("languagedetails/getbylanguage/{lang}")
    Observable<DictionaryResponse> getDictionaryResponse(@Path("lang") String language,
                                                         @Header("Authorization") String token);

    @POST("languagedetails/getbyloginregisterform/{lang}")
    Observable<DictionaryResponse> getTokenLessDictionary(@Path("lang") String language);

    @POST("aimotpservice/verifyloginotp")
    Observable<Otp> getOtpResponse(@Body OtpRequestModel params,
                                   @Header("Authorization") String token);

    @POST("ratecontrol/getuserratelist")
    Observable<RateResponse> getRate(@Header("Authorization") String token);

    @POST("userdashboard/getonlinerates")
    Observable<DashboardRateModel> getDashBoardRate(@Header("Authorization") String token);

    @GET("customersupport/getcustomercontact")
    Observable<SupportModel> getCustomercareNumber(@Header("Authorization") String token);

    @GET("customersupport/getgeneralcontact")
    Observable<SupportModel> getCustomercareNumberRegister();


    @GET("beneficiarybank/getall")
    Observable<BankBeneficiaryResponse> getBankBeneficiary(@Header("Authorization") String token);

    @GET("beneficiaryfastcash/getall")
    Observable<CashPickUpResponse> getCashPickUpBeneficiary(@Header("Authorization") String token);


    @GET("paymentmodes/getall")
    Observable<SelectionResponse> getPaymentModes(@Header("Authorization") String token);

    @GET("purpose/getallonline")
    Observable<SelectionResponse> getPurpose(@Header("Authorization") String token);

    @GET("incomesource/getallonline")
    Observable<SelectionResponse> getSource(@Header("Authorization") String token);

    @GET("banks/getallaccounttransferbanksmin")
    Observable<SelectionResponse> getBank(@Header("Authorization") String token);

    @POST("ratecontrol/getrate")
    Observable<GetRateResponse> getUserRate(@Header("Authorization") String token,
                                            @Body RateRequest param);

    @POST("remittancebank/create")
    Observable<TransferResponse> doTransfer(@Body TransferRequestModel params,
                                            @Header("Authorization") String token);

    @POST("remittancefastcash/create")
    Observable<TransferResponse> doFastCashTransfer(@Body TransferRequestModel params,
                                                    @Header("Authorization") String token);


    @POST("aimotpservice/verifytransactionpin")
    Observable<Otp> verifyOtpResponse(@Body OtpRequestModel params,
                                      @Header("Authorization") String token);

    @POST("aimotpservice/verifyregisterotp")
    Observable<Otp> verifyTokenLessOtpResponse(@Body JsonObject params,
                                               @Header("Authorization") String token);


    @Multipart
    @POST("onlinepayments/processpayment")
    Observable<String> paymentGatewayRequest(@Part("TransferType") RequestBody TransferType,
                                             @Part("ReferenceNo") RequestBody ReferenceNo,
                                             @Part("Token") String Token);

    @POST("usercontrol/getreportitemdetails")
    Observable<ConfirmPaymentResponse> genereateInvoice(@Body InvoiceRequestModel params,
                                                        @Header("Authorization") String token);

    @GET("country/getonlineforbank")
    Observable<SelectionResponse> getCountries(@Header("Authorization") String token);

    @GET("country/getonlineforfastcash")
    Observable<SelectionResponse> getFastCashCountries(@Header("Authorization") String token);

    @GET("transfertype/getbankbycountryid/{countryId}")
    Observable<SelectionResponse> getTransferType(@Path("countryId") String countryId,
                                                  @Header("Authorization") String token);


    @POST("remitbanks/getbycountryandtransfertype")
    Observable<DetailModel> getBanks(@Body BanksRequest params,
                                     @Header("Authorization") String token);


    @POST("remitbanks/getallbranches")
    Observable<SelectionResponse> getBranches(@Body BranchRequest params,
                                              @Header("Authorization") String token);

    @GET("userrelations/getall")
    Observable<SelectionResponse> getRelation(@Header("Authorization") String token);

    @GET("country/getallmin")
    Observable<SelectionResponse> getNationalities(@Header("Authorization") String token);

    @POST("beneficiarybank/create")
    Observable<CreateBeneficiaryResponse> createBankBeneficiary(@Body JsonObject params,
                                                                @Header("Authorization") String token);


    @GET("idtype/getallbeneficiary")
    Observable<SelectionResponse> getIdType(@Header("Authorization") String token);

    @GET("transfertype/getfastcashbycountryid/{countryId}")
    Observable<SelectionResponse> getFastTransferType(@Path("countryId") String countryId,
                                                      @Header("Authorization") String token);

    @POST("remitagents/getbycountryandtransfertype")
    Observable<DetailModel> getAgent(@Body BanksRequest params,
                                     @Header("Authorization") String token);

    @POST("tfpickupfromanywhere/getbranchbycountryandagent")
    Observable<CheckOutResponse> checkPayOut(@Body JsonObject params,
                                             @Header("Authorization") String token);

    @POST("remitagents/gettfstates")
    Observable<SelectionResponse> getState(@Body JsonObject params,
                                           @Header("Authorization") String token);

    @POST("remitagents/gettfcities")
    Observable<SelectionResponse> getCity(@Body JsonObject params,
                                          @Header("Authorization") String token);

    @POST("remitagents/gettfbranches")
    Observable<SelectionResponse> getBranch(@Body JsonObject params,
                                            @Header("Authorization") String token);

    @POST("beneficiaryfastcash/create")
    Observable<CreateBeneficiaryResponse> createFastCashBeneficiary(@Body JsonObject params,
                                                                    @Header("Authorization") String token);

    @POST("remitagents/getallbranches")
    Observable<SelectionResponse> getNormalAgentBranches(@Body JsonObject params,
                                                         @Header("Authorization") String token);

    @POST("usercontrol/getreportbydaterange")
    Observable<ReportResponse> getReports(@Body JsonObject params,
                                          @Header("Authorization") String token);

    @POST("usercontrol/setpassword")
    Observable<Otp> changePassword(@Body JsonObject params,
                                   @Header("Authorization") String token);

    @POST("usercontrol/setpinmode")
    Observable<Otp> changePin(@Body JsonObject params,
                              @Header("Authorization") String token);

    @POST("usercontrol/deactivateuser")
    Observable<Otp> blockAccount(@Header("Authorization") String token);


    @GET("country/getallcountries")
    Observable<SelectionResponse> getCountryList();

    @GET("idtype/getallidtypes")
    Observable<SelectionResponse> getIdType();

    @GET("profession/getallprofessiondetails")
    Observable<SelectionResponse> getProffessions();

    @GET("common/getallsalaryranges")
    Observable<SelectionResponse> getSalaryData();

    @Multipart
    @POST("users/register")
    Observable<CreateBenResponse> createRegister(
            @Part("idfiles0") RequestBody ID0,
            @Part("idfiles1") RequestBody ID1,
            @Part("video") RequestBody Video,
            @Part("signature") RequestBody Sign,

            @Part("fname") RequestBody FirstName,
            @Part("mname") RequestBody MiddileName,
            @Part("lname") RequestBody LastName,

            @Part("contact") RequestBody Mobile,
            @Part("email") RequestBody Email,

            @Part("city") RequestBody City,
            @Part("state") RequestBody State,
            @Part("nationality") RequestBody Country,

            @Part("idtype") RequestBody IdType,
            @Part("idno") RequestBody IdNo,
            @Part("idexpiry") RequestBody IdExpiry,

            @Part("salary") RequestBody Salary,
            @Part("professiondetail") RequestBody Profession,
            @Part("employer") RequestBody Employer,
            @Part("idfilescount") RequestBody Count);

    @Multipart
    @POST("users/uploadvideofile")
    Observable<FileUploadResponse> uploadVideo(@Part MultipartBody.Part filePart,
                                               @Header("Authorization") String token);

    @Multipart
    @POST("users/uploadsignaturefile")
    Observable<FileUploadResponse> uploadSignature(@Part MultipartBody.Part filePart,
                                                   @Header("Authorization") String token);

    @Multipart
    @POST("users/uploadidfiles")
    Observable<FileUploadResponse> uploadIdfiles(@Part MultipartBody.Part filePart1,
                                                 @Part MultipartBody.Part filePart2,
                                                 @Part("idfilescount") RequestBody Count,
                                                 @Header("Authorization") String token);


    @POST("usercontrol/getalllanguages")
    Observable<LanguageResponse> getAllLanguages(@Header("Authorization") String token);

    @POST("usercontrol/setlanguage")
    Observable<Otp> updateLanguage(@Body JsonObject params,
                                   @Header("Authorization") String token);

    @POST("usercontrol/registerbiometric")
    Observable<Otp> registerBiometric(@Body JsonObject params,
                                      @Header("Authorization") String token);

    @POST("usercontrol/removebiometric")
    Observable<Otp> removeBiometric(@Body JsonObject params,
                                    @Header("Authorization") String token);

    @POST("aimotpservice/verifybiometricotp")
    Observable<Otp> verifyBiometricOtp(@Body JsonObject params,
                                       @Header("Authorization") String token);

    @POST("usercontrol/updatedevicetoken")
    Observable<Otp> updateFcmToken(@Body JsonObject params,
                                   @Header("Authorization") String token);

    @POST("beneficiarybank/delete")
    Observable<Otp> deleteBankBen(@Body JsonObject params,
                                  @Header("Authorization") String token);

    @POST("beneficiaryfastcash/delete")
    Observable<Otp> deleteCashkBen(@Body JsonObject params,
                                   @Header("Authorization") String token);

    @GET("remittancebank/getprocesslevelinfobyidmin/{id}")
    Observable<TrackTransferResponse> getBankStatus(@Path("id") String refno,
                                                    @Header("Authorization") String token);

    @GET("remittancefastcash/getprocesslevelinfobyidmin/{id}")
    Observable<TrackTransferResponse> getFastCashStatus(@Path("id") String refno,
                                                        @Header("Authorization") String token);

    @POST("notification/getall")
    Observable<NotificationResponseModel> getAllNotification(@Body JsonObject params,
                                                             @Header("Authorization") String token);

    @GET("users/checkbyusername/{id}")
    Observable<CheckUserResponse> getUserStatus(@Path("id") String userId);

    @POST("aimotpservice/resendloginotp")
    Observable<Otp> resendLoginOtp(@Body OtpRequestModel params,
                                   @Header("Authorization") String token);

    @POST("aimotpservice/resendregisterotp")
    Observable<Otp> resendRegisterOtp(@Body OtpRequestModel params);

    @POST("aimotpservice/resendtransactionotp")
    Observable<Otp> resendBiometricOtp(@Body OtpRequestModel params,
                                       @Header("Authorization") String token);

    @GET("usercontrol/getreferralbonus")
    Observable<RewardsResponse> getAllRewards(@Header("Authorization") String token);

    @GET("common/getcurrentdatetime")
    Observable<DateTimeResponse> getDateTime();


    @Multipart
    @POST("usercontrol/uploadcustomerimage")
    Observable<FileUploadResponse> uploadProfilePic(@Part MultipartBody.Part filePart1,
                                                    @Part("id") RequestBody id,
                                                    @Header("Authorization") String token);

    @GET("employertypes/getallemployers")
    Observable<SelectionResponse> getEmployerType();

    @GET("usercontrol/downloadcustomerimage/{id}")
    Observable<String> getProfilePic(@Header("Authorization") String token,
                                     @Path("id") String userId);

    @GET("usercontrol/downloadidfilebyid/{id}")
    Observable<String> getIDFile(@Header("Authorization") String token,
                                 @Path("id") String userId);

    @GET("ratecontrol/getonlineuserratelist")
    Observable<RateResponse> getTokenLessRate();


    @GET("usercontrol/getbyidnomin/{id}")
    Observable<ProfileResponse> getProfileData(@Header("Authorization") String token,
                                               @Path("id") String userId);

    @GET("common/getapplicationstartmessage")
    Observable<BannerModel> getBannerResult();
}
