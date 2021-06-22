package io.thoughtbox.hamdan.views;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.utls.ConnectionLiveData;
import io.thoughtbox.hamdan.utls.FingerAuthentication;
import io.thoughtbox.hamdan.utls.FingerIdentification;
import io.thoughtbox.hamdan.utls.Loader;
import io.thoughtbox.hamdan.utls.OnResendOtp;
import io.thoughtbox.hamdan.alerts.AuthenticationView;
import io.thoughtbox.hamdan.alerts.NotificationAlerts;
import io.thoughtbox.hamdan.databinding.ActivityManageBiometricBinding;
import io.thoughtbox.hamdan.global.Dictionary;
import io.thoughtbox.hamdan.global.Universal;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import io.thoughtbox.hamdan.model.loginModel.OtpRequestModel;
import io.thoughtbox.hamdan.viewModel.BiometricViewModel;

public class ManageBiometric extends AppCompatActivity implements FingerAuthentication, OnResendOtp {
    @Inject
    Dictionary dictionary;
    private BiometricViewModel viewModel;
    private ActivityManageBiometricBinding binding;
    private View addBiometric, removeBiometric;
    private boolean isSensorActive = false;
    private FingerIdentification fingerprintSensor;
    private NotificationAlerts alerts;
    private Dialog progressDialog;
    private AuthenticationView otpView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_biometric);
        init();

        LiveData<Boolean> isSessionValid = viewModel.getIsSessionValid();
        isSessionValid.observe(this, isTokenValid -> {
            if (!isTokenValid) {
                alerts.timeOut();
            }
        });

        LiveData<Boolean> isLoadingData = viewModel.getIsLoading();
        isLoadingData.observe(this, isLoading -> {
            if (isLoading) {
                progressDialog.show();
            } else {
                progressDialog.dismiss();
            }
        });

        viewModel.getLoadingError().observe(this, errorMsg -> {
            if (errorMsg != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(this, "" + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        LiveData<Boolean> isBiometricRegistered = viewModel.getIsBiometricRegistered();
        isBiometricRegistered.observe(this, isRegistered -> {
            if (isRegistered) {
                otpView.OtpLayout();
            } else {
                alerts.errorDialog("Your attempt to register fingerprint failed");
            }
        });

        LiveData<String> otpLiveData = otpView.getOtpLiveData();
        otpLiveData.observe(this, otp -> viewModel.verifyOtp(otp));

        LiveData<Boolean> isOtpVerified = viewModel.getIsOtpValid();
        isOtpVerified.observe(this, isValid -> {
            if (isValid) {
                otpView.dismissView();
                alerts.successDialog("Your fingerprint registration is successful");
                updateBiometricStatus();
            } else {
                otpView.clearFields();
                Toast.makeText(this, "Invalid pin supplied", Toast.LENGTH_SHORT).show();
            }
        });

        LiveData<Boolean> isBiometricRemoved = viewModel.getIsBiometricRemoved();
        isBiometricRemoved.observe(this, isRemoved -> {
            if (isRemoved) {
                updateRemoveBiometricStatus();
                alerts.successDialog("Fingerprint removed successfully");
            } else {
                alerts.errorDialog("Your attempt to remove fingerprint failed");
            }
        });

        LiveData<Boolean> resendOtp = viewModel.getResendOtpLiveData();
        resendOtp.observe(this, isOtpResend -> {
            if (isOtpResend) {
                Toast.makeText(this, "Otp Send", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Otp resend failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateBiometricStatus() {
        isSensorActive = false;
        addBiometric.setVisibility(View.GONE);
        removeBiometric.setVisibility(View.VISIBLE);
        setBiometricRegistered(true);
        Universal.getInstance().getLoginResponsedata().setIsandroidbiometricregistered("1");
    }

    private void updateRemoveBiometricStatus() {
        isSensorActive = false;
        removeBiometric.setVisibility(View.GONE);
        addBiometric.setVisibility(View.VISIBLE);
        setBiometricRegistered(false);
        Universal.getInstance().getLoginResponsedata().setIsandroidbiometricregistered("0");
    }

    private void setBiometricRegistered(boolean status) {
        SharedPreferences.Editor editor = getSharedPreferences("Bio-Status", MODE_PRIVATE).edit();
        editor.putBoolean("isExistingUser", status);
        editor.apply();
    }

    private void checkInternet() {
        ConnectionLiveData connectionLiveData = new ConnectionLiveData(this);
        connectionLiveData.observe(this, connection -> {
            if (!connection.getIsConnected()) {
                alerts.noInternet();
            }
            if (connection.getIsVpnConnected()) {
                Toast.makeText(this, "VPN Connected", Toast.LENGTH_SHORT).show();
                alerts.vpnAlert();
            } else {
                alerts.dismissVpnDialog();
            }
        });
    }

    private void init() {
        checkInternet();
        DaggerApiComponents.create().inject(this);
        viewModel = new ViewModelProvider(this).get(BiometricViewModel.class);
        UiInteractions interactions = new UiInteractions();
        alerts = new NotificationAlerts(this);
        binding.setClickers(interactions);
        binding.setLanguage(dictionary);
        progressDialog = new Loader(this).progress();
        binding.animationview.useHardwareAcceleration(true);
        binding.animationview.enableMergePathsForKitKatAndAbove(true);
        binding.animationview.playAnimation();
        otpView = new AuthenticationView(this, this);
        String infoTxt = dictionary.get("accessTitle") + "\n\n" + dictionary.get("usage1") + "\n" + dictionary.get("usage2") + "\n" + dictionary.get("usage3");
        binding.addbio.info.setText(infoTxt);

        String brandName = Universal.getInstance().getLoginResponsedata().getAndroidbiometricdevice();
        String biometricRegStatus = Universal.getInstance().getLoginResponsedata().getIsandroidbiometricregistered();

        addBiometric = binding.addbio.addCard;
        removeBiometric = binding.removebio.removeCard;

        if (brandName.contains("-")) {
            String[] deviceInfo = brandName.split("-");
            binding.removebio.deviceName.setText(deviceInfo[0]);
            binding.removebio.deviceModal.setText(deviceInfo[1]);
        }

        if (biometricRegStatus.equals("0")) {
            addFingerPrintView();
        } else if (biometricRegStatus.equals("1")) {
            removeFingerprintView();
        }
    }

    private void removeFingerprintView() {
        isSensorActive = false;
        addBiometric.setVisibility(View.GONE);
        removeBiometric.setVisibility(View.VISIBLE);
    }

    private void addFingerPrintView() {
        isSensorActive = true;
        addBiometric.setVisibility(View.VISIBLE);
        removeBiometric.setVisibility(View.GONE);
        fingerprintSensor = new FingerIdentification(this);
        fingerprintSensor.InitFinger();
    }

    @Override
    public void onResendOtpClicked() {
        OtpRequestModel params = new OtpRequestModel("ANDROID", "Biometric", "", "");
        viewModel.resendBiometricOtp(params);
    }

    @Override
    public void onSuccessfulFingerAuthentication(String androidKey) {
        try {
            String Brand_name = Build.BRAND;
            String device_name = Build.DEVICE;
            String name = Brand_name + "-" + device_name;

            JSONObject params = new JSONObject();
            params.put("platform", "ANDROID");
            params.put("biometrictoken", androidKey);
            params.put("biometricdevice", name);

            viewModel.registerBiometric(params);


        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public void onDeviceFailedToAuthenticate(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        viewModel.clear();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (isSensorActive) {
            fingerprintSensor.stopBiometricSensor();
        }
        Intent intent = new Intent(ManageBiometric.this, SettingsGrid.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        super.onBackPressed();
    }

    public class UiInteractions {
        public UiInteractions() {
        }

        public void onBackClicked(View view) {
            onBackPressed();
        }

        public void onRemoveBiometricClicked(View view) {
            JSONObject params = new JSONObject();
            try {
                params.put("platform", "ANDROID");
            } catch (JSONException e) {
                e.getMessage();
            }
            viewModel.removeBiometric(params);
        }
    }
}
