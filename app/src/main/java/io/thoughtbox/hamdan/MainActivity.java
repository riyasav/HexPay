package io.thoughtbox.hamdan;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import io.thoughtbox.hamdan.model.loginModel.Otp;
import io.thoughtbox.hamdan.utls.AppData;
import io.thoughtbox.hamdan.utls.ConnectionLiveData;
import io.thoughtbox.hamdan.utls.FingerAuthentication;
import io.thoughtbox.hamdan.utls.FingerIdentification;
import io.thoughtbox.hamdan.utls.IPCapture;
import io.thoughtbox.hamdan.utls.Loader;
import io.thoughtbox.hamdan.utls.OtpViewHandler;
import io.thoughtbox.hamdan.alerts.NotificationAlerts;
import io.thoughtbox.hamdan.databinding.ActivityMainBinding;
import io.thoughtbox.hamdan.databinding.ForgotPasswordBinding;
import io.thoughtbox.hamdan.global.Dictionary;
import io.thoughtbox.hamdan.global.Universal;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import io.thoughtbox.hamdan.model.dictionaryModel.DictionaryResponseData;
import io.thoughtbox.hamdan.model.loginModel.LoginRequestModel;
import io.thoughtbox.hamdan.model.loginModel.OtpRequestModel;
import io.thoughtbox.hamdan.viewModel.LoginViewModel;
import io.thoughtbox.hamdan.viewModel.OtpViewModel;
import io.thoughtbox.hamdan.views.ChangePassword;
import io.thoughtbox.hamdan.views.CheckUser;
import io.thoughtbox.hamdan.views.RateChecker;
import io.thoughtbox.hamdan.views.SettingsGrid;
import io.thoughtbox.hamdan.views.branches.BranchPager;

public class MainActivity extends AppCompatActivity implements FingerAuthentication {

    @Inject
    Dictionary dictionary;
    private ActivityMainBinding mainBinding;
    private LoginViewModel loginViewModel;
    private Dialog progressDialog;
    private LoginRequestModel requestModel;
    private HashMap<String, String> dictMap = new HashMap<>();
    private OtpViewHandler otpViewHandler;
    private OtpViewModel otpViewModel;
    private SettingsGrid settingsGrid;
    private NotificationAlerts alerts;
    private boolean isSensorActive, isChangePressed = false;
    private FingerIdentification fingerprintSensor;
    private String loginMode;
    String ipAddress;
    AppData appData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainBinding.setLifecycleOwner(this);
        init();

        LiveData<ArrayList<DictionaryResponseData>> liveDictObj = loginViewModel.getDictionaryLiveData();
        liveDictObj.observe(this, dictionaryResponsesList -> {

            for (DictionaryResponseData dictionaryResponse : dictionaryResponsesList) {
                dictMap.put(dictionaryResponse.getItem(), dictionaryResponse.getValue());
                Dictionary.getInstance().setLangMap(dictMap);
            }
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            changeTransaction();

        });

        LiveData<Boolean> isLoadingData = loginViewModel.getIsLoading();
        isLoadingData.observe(this, isLoading -> {
            if (isLoading) {
                progressDialog.show();
                Log.d("Loader", "stated new loader");
            } else {
                progressDialog.dismiss();
                Log.d("Loader", "stopped a loader");
            }
        });


        loginViewModel.getLoadingError().observe(this, errorMsg -> {
            if (errorMsg != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(this, "" + errorMsg, Toast.LENGTH_SHORT).show();
                if (errorMsg.toString().toLowerCase().equals("invalid otp")) {
                    otpViewHandler.clearFields();
                }
            }
        });

//        LiveData<Boolean> isLoaderData= otpViewModel.getIsLoading();
//        isLoaderData.observe(this,isLoader-> {
//
//            if (isLoader) {
//                progressDialog.show();
//                Log.d("Loader", "stated new loader");
//            } else {
//                progressDialog.dismiss();
//                Log.d("Loader", "stopped a loader");
//            }
//
//        });


        LiveData<String> getOtp = otpViewHandler.otpLiveData;
        getOtp.observe(this, otp -> {
            OtpRequestModel params = new OtpRequestModel("Login", otp, "");
            loginViewModel.validateOtp(params);
        });


        LiveData<Boolean> updateDeviceTokenLiveData = loginViewModel.getIsDeviceTokenUpdated();
        updateDeviceTokenLiveData.observe(this, isDeviceTokenUpdated -> {
            if (isDeviceTokenUpdated) {
//                setUserLanguage(langaugeUsed);
                if (appData.hasDefaultLanguage()) {
                    JSONObject params = new JSONObject();
                    try {
                        params.put("language", appData.getDeviceLanguage());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    loginViewModel.updateLanguage(params);
                } else {
                    if (Universal.getInstance().getLoginResponsedata().getIsfirsttimeloginuser().equals("1")) {
                        Intent intent1 = new Intent(this, ChangePassword.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent1);
                    } else {
                        Intent intent = new Intent(this, DashBoard.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    }
                }

            }
        });


        LiveData<Boolean> forgotPasswordResponse = loginViewModel.getForgotPasswordLiveData();
        forgotPasswordResponse.observe(this, isPasswordResetSuccessful -> {
            if (isPasswordResetSuccessful) {
                alerts.successDialog("Password reset was successful.We have sent the password to your registered mobile number");
            } else {
                alerts.errorDialog("Password reset failed.Retry later");
            }
        });

        LiveData<Boolean> resendOtp = loginViewModel.getResendOtpLiveData();
        resendOtp.observe(this, isOtpResend -> {
            if (isOtpResend) {
                Toast.makeText(this, "Otp Send", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Otp resend failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateLanguageStatus() {
        Toast.makeText(this, "Language updated", Toast.LENGTH_SHORT).show();
        mainBinding.invalidateAll();
    }
    private void init() {

        checkInternet();
        DaggerApiComponents.create().inject(this);
        appData=new AppData(this);
        ClickListners clickListners = new ClickListners();
        requestModel = new LoginRequestModel();
        mainBinding.setClickers(clickListners);
        mainBinding.setFields(requestModel);
        mainBinding.setLanguage(dictionary);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        otpViewModel = new ViewModelProvider(this).get(OtpViewModel.class);
        otpViewHandler = new OtpViewHandler(mainBinding, this);
        ipAddress = IPCapture.getIPAddress(true);
        Loader loader = new Loader(this);
        progressDialog = loader.progress();
        alerts = new NotificationAlerts(this);

        Intent intent = getIntent();
        loginMode = intent.getStringExtra("mode");
        if (loginMode != null) {
            if (loginMode.equals("BIOMETRIC")) {
                mainBinding.bioLogin.animationview.useHardwareAcceleration(true);
                mainBinding.bioLogin.animationview.enableMergePathsForKitKatAndAbove(true);
                mainBinding.bioLogin.animationview.playAnimation();
                isSensorActive = true;
                fingerprintSensor = new FingerIdentification(this);
                fingerprintSensor.InitFinger();
                mainBinding.credLogin.loginView.setVisibility(View.GONE);
                mainBinding.bioLogin.view.setVisibility(View.VISIBLE);
            } else {
                mainBinding.credLogin.loginView.setVisibility(View.VISIBLE);
                mainBinding.bioLogin.view.setVisibility(View.GONE);
            }
        }
    }

    private void setRegisteredLanguage(String lang) {
        SharedPreferences.Editor editor = getSharedPreferences("Language", MODE_PRIVATE).edit();
        editor.putString("currentLanguage", lang);
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

//    public boolean isVpnConnected() {
//        String iface = "";
//        try {
//            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
//                if (networkInterface.isUp())
//                    iface = networkInterface.getName();
//                Log.d("DEBUG", "IFACE NAME: " + iface);
//                if (iface.contains("tun") || iface.contains("ppp") || iface.contains("pptp")) {
//                    return true;
//                }
//            }
//        } catch (SocketException e1) {
//            e1.printStackTrace();
//        }
//
//        return false;
//    }

    @Override
    public void onSuccessfulFingerAuthentication(String androidKey) {
        fingerprintSensor.stopBiometricSensor();
        JSONObject object = new JSONObject();
        @SuppressLint("HardwareIds")
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        try {
            object.put("platform", "ANDROID");
            object.put("biometrictoken", android_id);
            object.put("ipaddress", ipAddress);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        loginViewModel.biometricLogin(appData,object);
    }

    @Override
    public void onDeviceFailedToAuthenticate(String message) {
        Toast.makeText(this, "fingerprint not recognized", Toast.LENGTH_SHORT).show();
    }

    private void doLogin() {
        if (requestModel.getUsername() != null && requestModel.getPassword() != null) {
            requestModel.setPlatform("ANDROID");
            requestModel.setIpaddress(ipAddress);
            loginViewModel.doLogin(appData,requestModel);
        } else {
            Toast.makeText(this, "Invalid Username/Password", Toast.LENGTH_SHORT).show();
        }
    }

    public void changeTransaction() {
        if (loginMode != null && loginMode.equals("BIOMETRIC")) {
            if (isChangePressed) {
                doAnimateTransaction(mainBinding.credLogin.loginView, mainBinding.otp.otpView);
            } else {
                doAnimateTransaction(mainBinding.bioLogin.view, mainBinding.otp.otpView);
            }
        } else if (loginMode != null && loginMode.equals("CREDENTIAL")) {
            doAnimateTransaction(mainBinding.credLogin.loginView, mainBinding.otp.otpView);
        }

    }

    public void doAnimateTransaction(View view1, View view2) {
        Animation slidein = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rtl_animation);
        Animation alpha = AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_out);
        view1.setAnimation(alpha);
        view2.setAnimation(slidein);
        view1.setVisibility(View.GONE);
        view2.setVisibility(View.VISIBLE);
    }

    public void showForgotPasswordDialog() {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        ForgotPasswordBinding forgotBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.forgot_password, null, false);
        mBottomSheetDialog.setContentView(forgotBinding.getRoot());
        mBottomSheetDialog.setCancelable(true);
        forgotBinding.submit.setOnClickListener(view -> {
            if (!forgotBinding.username.getText().toString().trim().isEmpty()) {
                loginViewModel.forgotPassword(forgotBinding.username.getText().toString());
                mBottomSheetDialog.dismiss();
            } else {
                Toast.makeText(this, "Enter a valid username to proceed", Toast.LENGTH_SHORT).show();
            }

        });
        forgotBinding.btn.setOnClickListener(v -> mBottomSheetDialog.dismiss());
        mBottomSheetDialog.show();


    }

    @Override
    protected void onDestroy() {
        if (isSensorActive) {
            fingerprintSensor.stopBiometricSensor();
        }
        loginViewModel.clear();
        super.onDestroy();

    }

    @Override
    protected void onStop() {
        if (isSensorActive) {
            fingerprintSensor.stopBiometricSensor();
        }
        loginViewModel.clear();
        Log.d("LifeCycle", "onStop viewModel Cleared");
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();
    }

    public class ClickListners {

        public ClickListners() {

        }

        public void onchangeClicked(View view) {
            if (isSensorActive) {
                fingerprintSensor.stopBiometricSensor();
            }
            isChangePressed = true;
            doAnimateTransaction(mainBinding.bioLogin.view, mainBinding.credLogin.loginView);
        }



        public void onLoginClicked(View view) {
            doLogin();
        }

        public void onResendOtpClicked(View view) {
            OtpRequestModel model = new OtpRequestModel("Login", "", "");
            loginViewModel.resendOtp(model);
        }

        public void onRegisterClicked(View view) {
            Intent intent = new Intent(MainActivity.this, CheckUser.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }

        public void onForgotPassword(View view) {
            showForgotPasswordDialog();
        }

        public void onLocatedBranchClicked(View view){
            Intent intent = new Intent(MainActivity.this, BranchPager.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }

        public void onCallUsClicked(View view){
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "+968-23-211258", null));
            startActivity(intent);
        }

        public void onCheckRatesClicked(View view){
            Intent intent = new Intent(MainActivity.this, RateChecker.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }


    }
}
