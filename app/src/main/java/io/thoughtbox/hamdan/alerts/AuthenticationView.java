package io.thoughtbox.hamdan.alerts;


import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import javax.inject.Inject;

import io.thoughtbox.hamdan.Adapters.OnChangeNumber;
import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.databinding.TokenlessOtpSheetBinding;
import io.thoughtbox.hamdan.utls.AuthenticationHelper;
import io.thoughtbox.hamdan.utls.FingerAuthentication;
import io.thoughtbox.hamdan.utls.FingerIdentification;
import io.thoughtbox.hamdan.utls.MutableEventLiveData;
import io.thoughtbox.hamdan.utls.OnResendOtp;
import io.thoughtbox.hamdan.databinding.AuthorizeFingerSheetBinding;
import io.thoughtbox.hamdan.databinding.PinOtpSheetBinding;
import io.thoughtbox.hamdan.global.Dictionary;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;

public class AuthenticationView implements PassOtpPin, FingerAuthentication {
    @Inject
    Dictionary dictionary;
    private Context context;
    private BottomSheetDialog mBottomSheetDialogOTP;
    private MutableEventLiveData<String> otpLiveData = new MutableEventLiveData<>();
    private AuthenticationHelper helper;
    private OnResendOtp resendOtp;
    private OnChangeNumber onChangeNumber;
    private boolean isOtp = false;

    public AuthenticationView(Context context, OnResendOtp onResendOtp) {
        this.context = context;
        DaggerApiComponents.create().inject(this);
        this.resendOtp = onResendOtp;
    }

    public AuthenticationView(Context context, OnResendOtp onResendOtp, OnChangeNumber onChangeNumber) {
        this.context = context;
        DaggerApiComponents.create().inject(this);
        this.resendOtp = onResendOtp;
        this.onChangeNumber = onChangeNumber;
    }


    public void fingerLayout() {
        isOtp = false;
        mBottomSheetDialogOTP = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        AuthorizeFingerSheetBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.authorize_finger_sheet, null, false);
        mBottomSheetDialogOTP.setContentView(binding.getRoot());
        mBottomSheetDialogOTP.setCancelable(false);
        binding.setLanguage(dictionary);
        Button cancel = binding.cancelBtn;
        binding.animationview.useHardwareAcceleration(true);
        binding.animationview.enableMergePathsForKitKatAndAbove(true);
        binding.animationview.playAnimation();
        assert cancel != null;
        cancel.setOnClickListener(v -> mBottomSheetDialogOTP.dismiss());

        mBottomSheetDialogOTP.show();

        FingerIdentification fingerprintSensor = new FingerIdentification(context, this);
        fingerprintSensor.InitFinger();

    }

    public BottomSheetDialog OtpLayout() {
        isOtp = true;
        mBottomSheetDialogOTP = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        PinOtpSheetBinding otpBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.pin_otp_sheet, null, false);
        otpBinding.setLanguage(dictionary);
        helper = new AuthenticationHelper(otpBinding, context, this);
        mBottomSheetDialogOTP.setContentView(otpBinding.getRoot());
        mBottomSheetDialogOTP.setCancelable(true);
        mBottomSheetDialogOTP.show();

        otpBinding.resend.setOnClickListener(view -> {
            resendOtp.onResendOtpClicked();
        });
        return mBottomSheetDialogOTP;
    }

    public BottomSheetDialog OtpLayoutTokenLess(boolean isChangeNumberRequired) {

        isOtp = true;
        mBottomSheetDialogOTP = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        TokenlessOtpSheetBinding otpBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.tokenless_otp_sheet, null, false);
        helper = new AuthenticationHelper(otpBinding, context, this, "");
        mBottomSheetDialogOTP.setContentView(otpBinding.getRoot());
        otpBinding.resend.setOnClickListener(view -> {
            resendOtp.onResendOtpClicked();
        });
        if (isChangeNumberRequired) {
            otpBinding.changeNumber.setVisibility(View.VISIBLE);
        } else {
            otpBinding.changeNumber.setVisibility(View.GONE);
        }
        otpBinding.changeNumber.setOnClickListener(v -> {
            onChangeNumber.onMobileNumberChanged();
            mBottomSheetDialogOTP.dismiss();

        });
        mBottomSheetDialogOTP.setCancelable(true);
        mBottomSheetDialogOTP.show();
        return mBottomSheetDialogOTP;
    }

//    public BottomSheetDialog OtpLayoutTokenLess(boolean isChangeNumberRequired) {
//
//        isOtp = true;
//        mBottomSheetDialogOTP = new BottomSheetDialog(context, R.style.BottomSheetDialog);
//        PinOtpSheetBinding otpBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.pin_otp_sheet, null, false);
//        helper = new AuthenticationHelper(otpBinding, context, this, "");
//        mBottomSheetDialogOTP.setContentView(otpBinding.getRoot());
//        otpBinding.resend.setOnClickListener(view -> {
//            resendOtp.onResendOtpClicked();
//        });
//        if (isChangeNumberRequired) {
//            otpBinding.changeNumber.setVisibility(View.VISIBLE);
//        } else {
//            otpBinding.changeNumber.setVisibility(View.GONE);
//        }
//        otpBinding.changeNumber.setOnClickListener(v -> {
//            onChangeNumber.onMobileNumberChanged();
//            mBottomSheetDialogOTP.dismiss();
//
//        });
//        mBottomSheetDialogOTP.setCancelable(true);
//        mBottomSheetDialogOTP.show();
//        return mBottomSheetDialogOTP;
//    }



    @Override
    public void onPinListener(String pin) {
        otpLiveData.setValue(pin);
    }

    @Override
    public void onSuccessfulFingerAuthentication(String androidKey) {
        @SuppressLint("HardwareIds")
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("DeviceToken", android_id);
        otpLiveData.postValue(android_id);
    }

    @Override
    public void onDeviceFailedToAuthenticate(String message) {
        Toast.makeText(context, "fingerprint failed.try again", Toast.LENGTH_SHORT).show();
    }

    public MutableEventLiveData<String> getOtpLiveData() {
        return otpLiveData;
    }

    public void clearFields() {
        if (isOtp) {
            helper.clearFields();
        }
    }

    public void dismissView() {
        if (mBottomSheetDialogOTP != null) {
            mBottomSheetDialogOTP.dismiss();
        }

    }

}
