package io.thoughtbox.hamdan.fragments;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import io.thoughtbox.hamdan.Adapters.OnChangeNumber;
import io.thoughtbox.hamdan.MainActivity;
import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.databinding.ChangeNumberBinding;
import io.thoughtbox.hamdan.databinding.PinOtpSheetBinding;
import io.thoughtbox.hamdan.utls.Constants;
import io.thoughtbox.hamdan.utls.Loader;
import io.thoughtbox.hamdan.utls.OnResendOtp;
import io.thoughtbox.hamdan.alerts.AuthenticationView;
import io.thoughtbox.hamdan.databinding.FragmentUploadRegisterDataBinding;
import io.thoughtbox.hamdan.model.loginModel.OtpRequestModel;
import io.thoughtbox.hamdan.viewModel.OtpViewModel;
import io.thoughtbox.hamdan.viewModel.RegisterViewModel;
import io.thoughtbox.hamdan.views.Signup;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadRegisterData extends Fragment implements OnResendOtp, OnChangeNumber {
    LottieAnimationView animationview;
    private FragmentUploadRegisterDataBinding binding;
    private Dialog progressDialog;
    private String token = null;
    private String otpRefNum = null;
    private AuthenticationView authenticationView;
    private RegisterViewModel viewModel;
    private OtpViewModel otpViewModel;

    public UploadRegisterData() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_upload_register_data, container, false);
        binding.setLifecycleOwner(this);
        init();

        LiveData<Boolean> isLoadingData = viewModel.getIsLoading();
        isLoadingData.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                progressDialog.show();
                Log.d("Loader", "stated new loader");
            } else {
                progressDialog.dismiss();
                Log.d("Loader", "stopped a loader");
            }
        });

        viewModel.getLoadingError().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(getContext(), "" + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        LiveData<String> otpLiveData = authenticationView.getOtpLiveData();
        otpLiveData.observe(getViewLifecycleOwner(), otp -> {
            OtpRequestModel params = new OtpRequestModel("Register", otp, token);
            otpViewModel.validateTokenLessOTP(params);
        });

        LiveData<Boolean> isOtpLoadingData = otpViewModel.getIsLoading();
        isOtpLoadingData.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                progressDialog.show();
                Log.d("Loader", "stated new loader");
            } else {
                progressDialog.dismiss();
                Log.d("Loader", "stopped a loader");
            }
        });

        otpViewModel.getLoadingError().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(getContext(), "" + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
        LiveData<Boolean> isOtpValid = otpViewModel.getTokenlessOtpStatus();
        isOtpValid.observe(getViewLifecycleOwner(), isValid -> {
            if (isValid) {
                authenticationView.dismissView();
                binding.progressBar2.setVisibility(View.GONE);
                binding.success2.setVisibility(View.VISIBLE);
                binding.failed2.setVisibility(View.GONE);
                binding.retry2.setVisibility(View.GONE);
                binding.textView2.setText("OTP Verified");
                uploadIDfiles();
                uploadSignature();
                uploadVideo();
            } else {
                authenticationView.clearFields();
            }
        });


        LiveData<Boolean> resendOtp = otpViewModel.getResendOtpLiveData();
        resendOtp.observe(getViewLifecycleOwner(), isOtpResend -> {
            if (isOtpResend) {
                Toast.makeText(getContext(), "Otp Send", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Otp resend failed", Toast.LENGTH_SHORT).show();
            }
        });

        //-------------------------------------------------------------------

        viewModel.getIdFileLoadingError().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(getContext(), "ID File Upload Error" + errorMsg, Toast.LENGTH_SHORT).show();
            }
            binding.progressBar3.setVisibility(View.GONE);
            binding.failed3.setVisibility(View.VISIBLE);
            binding.success3.setVisibility(View.GONE);
            binding.retry3.setVisibility(View.VISIBLE);

        });

        LiveData<Boolean> idUploadStatus = viewModel.getIdFilesLiveData();
        idUploadStatus.observe(getViewLifecycleOwner(), isValid -> {
            if (isValid) {
                binding.progressBar3.setVisibility(View.GONE);
                binding.success3.setVisibility(View.VISIBLE);
                binding.failed3.setVisibility(View.GONE);
                binding.retry3.setVisibility(View.GONE);
                binding.textView3.setText("ID Files Uploaded");
            }
        });

        viewModel.getVideLoadingError().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(getContext(), "Video uploading error:" + errorMsg, Toast.LENGTH_SHORT).show();
            }
            binding.progressBar4.setVisibility(View.GONE);
            binding.failed4.setVisibility(View.VISIBLE);
            binding.success4.setVisibility(View.GONE);
            binding.retry4.setVisibility(View.VISIBLE);

        });

        LiveData<Boolean> videoUploadStatus = viewModel.getVideoLiveData();
        videoUploadStatus.observe(getViewLifecycleOwner(), isValid -> {
            if (isValid) {
                binding.progressBar4.setVisibility(View.GONE);
                binding.success4.setVisibility(View.VISIBLE);
                binding.failed4.setVisibility(View.GONE);
                binding.retry4.setVisibility(View.GONE);
                binding.textView4.setText("Video Uploaded");
            }
        });

        viewModel.getSignatureLoadingError().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(getContext(), "Signature upload error: " + errorMsg, Toast.LENGTH_SHORT).show();
            }
            binding.progressBar5.setVisibility(View.GONE);
            binding.failed5.setVisibility(View.VISIBLE);
            binding.success5.setVisibility(View.GONE);
            binding.retry5.setVisibility(View.VISIBLE);
        });

        LiveData<Boolean> isSignatureUploaded = viewModel.getSignatureLiveData();
        isSignatureUploaded.observe(getViewLifecycleOwner(), isValid -> {
            if (isValid) {
                authenticationView.dismissView();
                binding.progressBar5.setVisibility(View.GONE);
                binding.success5.setVisibility(View.VISIBLE);
                binding.failed5.setVisibility(View.GONE);
                binding.retry5.setVisibility(View.GONE);
                binding.textView5.setText("Signature Uploaded");

            }
        });
        return binding.getRoot();
    }

    private void init() {
        Loader loader = new Loader(getContext());
        progressDialog = loader.progress();
        binding.setClickers(this);
        authenticationView = new AuthenticationView(getContext(), this,this);
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        otpViewModel = new ViewModelProvider(this).get(OtpViewModel.class);
        signupUser();
        animationview = binding.animationview;
        animationview.useHardwareAcceleration(true);
        animationview.enableMergePathsForKitKatAndAbove(true);
        animationview.playAnimation();


    }

    private void signupUser() {
        progressDialog.show();
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        builder.addFormDataPart("platform", "ANDROID");
        builder.addFormDataPart("fname", Objects.requireNonNull(Signup.userData.get("fname")));
        builder.addFormDataPart("mname", Objects.requireNonNull(Signup.userData.get("mname")));
        builder.addFormDataPart("lname", Objects.requireNonNull(Signup.userData.get("lname")));

        builder.addFormDataPart("dob", Objects.requireNonNull(Signup.userData.get("dob")));
        builder.addFormDataPart("sex", Objects.requireNonNull(Signup.userData.get("gender")));
        builder.addFormDataPart("idissuedcountry", Objects.requireNonNull(Signup.userData.get("idCountry")));
        builder.addFormDataPart("customertype", "Individual");

        builder.addFormDataPart("city", Objects.requireNonNull(Signup.userData.get("address1")));
        builder.addFormDataPart("state", Objects.requireNonNull(Signup.userData.get("address2")));
        builder.addFormDataPart("nationality", Objects.requireNonNull(Signup.userData.get("nationality")));
        builder.addFormDataPart("idno", Objects.requireNonNull(Signup.userData.get("idNumber")));
        builder.addFormDataPart("idtype", Objects.requireNonNull(Signup.userData.get("idType")));
        builder.addFormDataPart("idexpiry", Objects.requireNonNull(Signup.userData.get("idExpiry")));
        builder.addFormDataPart("email", Objects.requireNonNull(Signup.userData.get("email")));
        builder.addFormDataPart("contact", Objects.requireNonNull(Signup.userData.get("mobile")));
        builder.addFormDataPart("salary", Objects.requireNonNull(Signup.userData.get("salary")));
        builder.addFormDataPart("professiondetail", Objects.requireNonNull(Signup.userData.get("profession")));
        builder.addFormDataPart("employer", Objects.requireNonNull(Signup.userData.get("employer")));

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(Constants.BaseURL + "users/register")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                progressDialog.dismiss();
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                progressDialog.dismiss();
                final String myResponse = response.body().string();

                getActivity().runOnUiThread(() -> {
                    try {
                        JSONObject respons = new JSONObject(myResponse);
                        String responsDesc = respons.getString("responsedescription");
                        String responsstatus = respons.getString("responsestatus");
                        JSONObject responsData = respons.getJSONObject("responsedata");
                        if (responsstatus.toUpperCase().equals("TRUE")) {
                            token = responsData.getString("token");
                            otpRefNum = responsData.getString("id");
                            binding.success1.setVisibility(View.VISIBLE);
                            binding.failed1.setVisibility(View.GONE);
                            binding.progressBar1.setVisibility(View.GONE);
                            binding.retry1.setVisibility(View.GONE);
                            binding.textView1.setText("Data Uploaded");
                            authenticationView.OtpLayoutTokenLess(true).show();

                        } else {
                            binding.success1.setVisibility(View.GONE);
                            binding.failed1.setVisibility(View.VISIBLE);
                            binding.progressBar1.setVisibility(View.VISIBLE);
                            binding.retry1.setVisibility(View.VISIBLE);
                            Toast.makeText(getContext(), responsDesc, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });
            }

        });
    }

    private void updateSignedUpUser() {
        progressDialog.show();
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        builder.addFormDataPart("platform", "ANDROID");
        builder.addFormDataPart("fname", Objects.requireNonNull(Signup.userData.get("fname")));
        builder.addFormDataPart("mname", Objects.requireNonNull(Signup.userData.get("mname")));
        builder.addFormDataPart("lname", Objects.requireNonNull(Signup.userData.get("lname")));

        builder.addFormDataPart("dob", Objects.requireNonNull(Signup.userData.get("dob")));
        builder.addFormDataPart("sex", Objects.requireNonNull(Signup.userData.get("gender")));
        builder.addFormDataPart("idissuedcountry", Objects.requireNonNull(Signup.userData.get("idCountry")));
        builder.addFormDataPart("customertype", "Individual");

        builder.addFormDataPart("city", Objects.requireNonNull(Signup.userData.get("address1")));
        builder.addFormDataPart("state", Objects.requireNonNull(Signup.userData.get("address2")));
        builder.addFormDataPart("nationality", Objects.requireNonNull(Signup.userData.get("nationality")));
        builder.addFormDataPart("idno", Objects.requireNonNull(Signup.userData.get("idNumber")));
        builder.addFormDataPart("idtype", Objects.requireNonNull(Signup.userData.get("idType")));
        builder.addFormDataPart("idexpiry", Objects.requireNonNull(Signup.userData.get("idExpiry")));
        builder.addFormDataPart("email", Objects.requireNonNull(Signup.userData.get("email")));
        builder.addFormDataPart("contact", Objects.requireNonNull(Signup.userData.get("mobile")));
        builder.addFormDataPart("salary", Objects.requireNonNull(Signup.userData.get("salary")));
        builder.addFormDataPart("professiondetail", Objects.requireNonNull(Signup.userData.get("profession")));
        builder.addFormDataPart("employer", Objects.requireNonNull(Signup.userData.get("employer")));

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(Constants.BaseURL + "user/updateuserdetails")
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + token)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                progressDialog.dismiss();
                call.cancel();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                progressDialog.dismiss();
                final String myResponse = response.body().string();

                getActivity().runOnUiThread(() -> {
                    try {
                        JSONObject respons = new JSONObject(myResponse);
                        String responsDesc = respons.getString("responsedescription");
                        String responsstatus = respons.getString("responsestatus");
                        boolean responsData = respons.getBoolean("responsedata");
                        if (responsData) {
                            binding.success1.setVisibility(View.VISIBLE);
                            binding.failed1.setVisibility(View.GONE);
                            binding.progressBar1.setVisibility(View.GONE);
                            binding.retry1.setVisibility(View.GONE);
                            binding.textView1.setText("Data Uploaded");
                            authenticationView.OtpLayoutTokenLess(true).show();

                        } else {
                            binding.success1.setVisibility(View.GONE);
                            binding.failed1.setVisibility(View.VISIBLE);
                            binding.progressBar1.setVisibility(View.VISIBLE);
                            binding.retry1.setVisibility(View.VISIBLE);
                            Toast.makeText(getContext(), responsDesc, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });
            }

        });
    }

    private void uploadIDfiles() {

        viewModel.uploadIdFiles(Signup.imageData.get("front"), Signup.imageData.get("back"), token);
    }

    private void uploadVideo() {
        viewModel.uploadVideo(Signup.imageData.get("selfie"), token);
    }

    private void uploadSignature() {
        viewModel.uploadSignature(Signup.imageData.get("sign"), token);
    }

    public void onRetry1Clicked(View view) {
        signupUser();
    }

    public void onRetry2Clicked(View view) {
        authenticationView.OtpLayoutTokenLess(true).show();
    }

    public void onRetry3Clicked(View view) {
        uploadIDfiles();
    }

    public void onRetry4Clicked(View view) {
        uploadVideo();
    }

    public void onRetry5Clicked(View view) {
        uploadSignature();
    }

    public void onLoginClicked(View view) {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra("mode", "CREDENTIAL");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    public void onResendOtpClicked() {
        OtpRequestModel params = new OtpRequestModel("Register", "", otpRefNum);
        otpViewModel.resendRegisterOtp(params);
    }

    public void onBackClicked(View view) {
        getActivity().getSupportFragmentManager().popBackStack();
//        SignaturePad signPad = new SignaturePad();
//        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
//        ft.replace(R.id.container, signPad);
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        ft.commit();
    }

    @Override
    public void onDestroyView() {
        viewModel.clear();
        otpViewModel.clear();
        super.onDestroyView();
    }

    @Override
    public void onMobileNumberChanged() {
        showMobileNumberChangeView();
    }

    private void showMobileNumberChangeView() {
        BottomSheetDialog  mBottomSheetDialogOTP = new BottomSheetDialog(getContext(), R.style.BottomSheetDialog);
        ChangeNumberBinding changeNumberBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.change_number, null, false);
        mBottomSheetDialogOTP.setContentView(changeNumberBinding.getRoot());
        mBottomSheetDialogOTP.setCancelable(true);
        mBottomSheetDialogOTP.show();
        changeNumberBinding.submit.setOnClickListener(v -> {
        String mobileNumber=changeNumberBinding.mobileNumber.getText().toString();
        Signup.userData.put("mobile",mobileNumber);
        updateSignedUpUser();
        mBottomSheetDialogOTP.dismiss();
        });
    }
}
