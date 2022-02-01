package io.thoughtbox.hamdan.views;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;

import javax.inject.Inject;

import io.thoughtbox.hamdan.DashBoard;
import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.alerts.NotificationAlerts;
import io.thoughtbox.hamdan.databinding.ActivitySettingsGridBinding;
import io.thoughtbox.hamdan.databinding.BlockAccountBinding;
import io.thoughtbox.hamdan.databinding.ChangePinBinding;
import io.thoughtbox.hamdan.databinding.PasswordChangeBinding;
import io.thoughtbox.hamdan.global.Dictionary;
import io.thoughtbox.hamdan.global.Universal;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import io.thoughtbox.hamdan.model.loginModel.OtpResponsedata;
import io.thoughtbox.hamdan.utls.ConnectionLiveData;
import io.thoughtbox.hamdan.utls.Constants;
import io.thoughtbox.hamdan.utls.Loader;
import io.thoughtbox.hamdan.viewModel.SettingsViewModel;

public class SettingsGrid extends AppCompatActivity {
    ActivitySettingsGridBinding binding;
    SettingsViewModel viewModel;
    BottomSheetDialog mBottomSheetDialog;
    PasswordChangeBinding changeBinding;
    Loader loader;
    Dialog progressDialog;
    NotificationAlerts alerts;
    UiInteraction uiInteraction;
    boolean isFixedPinChecked, isOtpChecked, isBiometricChecked = false;
    TextInputEditText fixedPin;
    @Inject
    Dictionary dictionary;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings_grid);
        binding.setLifecycleOwner(this);
        init();
        LiveData<Boolean> isSessionValid = viewModel.getSessionStatus();
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
        LiveData<String> error = viewModel.getLoadingError();
        error.observe(this, errorMsg -> {
            if (errorMsg != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                Log.d("invoice", errorMsg);
            }
        });

        LiveData<OtpResponsedata> changePassword = viewModel.getChangePassWordLiveData();
        changePassword.observe(this, passwordChangeResponse -> {
            if (passwordChangeResponse.getResult().toUpperCase().equals("TRUE")) {
                mBottomSheetDialog.dismiss();
                alerts.successDialog("Your login password changed successfully");
            } else {
                mBottomSheetDialog.dismiss();
                alerts.errorDialog("Your attempt to change login password failed");
            }
        });

        LiveData<OtpResponsedata> changePin = viewModel.getChangePinLiveData();
        changePin.observe(this, pinChangeResponse -> {
            if (pinChangeResponse.getResult().toUpperCase().equals("TRUE")) {
                if (isOtpChecked) {
                    Universal.getInstance().getLoginResponsedata().setPinmode("OTP");
                } else if (isBiometricChecked) {
                    Universal.getInstance().getLoginResponsedata().setPinmode("Biometric");
                } else {
                    Universal.getInstance().getLoginResponsedata().setPinmode("Fixed");
                }
                mBottomSheetDialog.dismiss();
                alerts.successDialog("Your transaction pin changed successfully");
            } else {
                mBottomSheetDialog.dismiss();
                alerts.errorDialog("Your attempt to change transaction pin failed");
            }
        });

        LiveData<OtpResponsedata> blockAccount = viewModel.getBlockPinLiveData();
        blockAccount.observe(this, blockedResponse -> {
            if (blockedResponse.getResult().toUpperCase().equals("TRUE")) {
                mBottomSheetDialog.dismiss();
//                alerts.logOut();
            } else {
                mBottomSheetDialog.dismiss();
                alerts.errorDialog("Your attempt to block account failed");
            }
        });
    }

    private void init() {
        checkInternet();
        DaggerApiComponents.create().inject(this);
        loader = new Loader(this);
        progressDialog = loader.progress();
        alerts = new NotificationAlerts(this);
        getVersionData();
        uiInteraction = new UiInteraction();
        binding.setClickers(uiInteraction);
        binding.setLanguage(dictionary);
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, DashBoard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        super.onBackPressed();
    }

    private void updateTrxPin(String pinNum, String isPin, String isBiometric) {
        JSONObject params = new JSONObject();
        try {
            params.put("staticpin", pinNum);
            params.put("isstaticpin", isPin);
            params.put("isbiometric", isBiometric);
            params.put("platform", "ANDROID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        viewModel.onPinChange(params);
    }

    public void getVersionData() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String vers = pInfo.versionName;
            int versCode = pInfo.versionCode;
            binding.version.setText(MessageFormat.format("{0} {1} {2}","Application version :", vers, versCode));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void changePassword() {
        mBottomSheetDialog = new BottomSheetDialog(this, R.style.Theme_Design_BottomSheetDialog);
        changeBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.password_change, null, false);
        changeBinding.setLanguage(dictionary);
        mBottomSheetDialog.setContentView(changeBinding.getRoot());
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.show();
        changeBinding.setClickers(uiInteraction);
    }

    private void changeTrxPinMode() {
        mBottomSheetDialog = new BottomSheetDialog(this, R.style.Theme_Design_BottomSheetDialog);
        ChangePinBinding pinBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.change_pin, null, false);
        mBottomSheetDialog.setContentView(pinBinding.getRoot());
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.show();
        pinBinding.setClickers(uiInteraction);
        pinBinding.setLanguage(dictionary);
        String mode = Universal.getInstance().getLoginResponsedata().getPinmode();
        TextInputLayout pinlayout = pinBinding.pinLayout;
        fixedPin = pinBinding.fixedpin;
        TextView status = pinBinding.status;
        RadioButton otp = pinBinding.otpbtn;
        RadioButton pin = pinBinding.pinbtn;
        RadioButton biometric = pinBinding.biometric;
        if (Universal.getInstance().getLoginResponsedata().getIsandroidbiometricregistered().equals("1")) {
            biometric.setVisibility(View.VISIBLE);
        } else {
            biometric.setVisibility(View.GONE);
        }
        pin.setOnClickListener(view -> onRadioButtonClicked(view, pinlayout));
        otp.setOnClickListener(view -> onRadioButtonClicked(view, pinlayout));
        biometric.setOnClickListener(view -> onRadioButtonClicked(view, pinlayout));

        status.setText(MessageFormat.format("your current transaction mode is: {0}", mode));

    }

    private void onRadioButtonClicked(View view, TextInputLayout pinlayout) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.otpbtn:
                if (checked) {
                    pinlayout.setVisibility(View.GONE);
                    isFixedPinChecked = false;
                    isOtpChecked = true;
                    isBiometricChecked = false;
                }
                break;
            case R.id.pinbtn:
                if (checked) {
                    pinlayout.setVisibility(View.VISIBLE);
                    isFixedPinChecked = true;
                    isOtpChecked = false;
                    isBiometricChecked = false;
                }
                break;
            case R.id.biometric:
                if (checked) {
                    pinlayout.setVisibility(View.GONE);
                    isFixedPinChecked = false;
                    isOtpChecked = false;
                    isBiometricChecked = true;
                }
                break;
        }
    }

    private void blockAccount() {
        mBottomSheetDialog = new BottomSheetDialog(this, R.style.Theme_Design_BottomSheetDialog);
        BlockAccountBinding blockAccountBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.block_account, null, false);
        blockAccountBinding.setLanguage(dictionary);
        mBottomSheetDialog.setContentView(blockAccountBinding.getRoot());
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.show();
        blockAccountBinding.setClickers(uiInteraction);
    }

    public class UiInteraction {

        public UiInteraction() {
        }

        public void onLanguageClicked(View view) {
            Intent intent = new Intent(getApplicationContext(), Language.class);
            startActivity(intent);
        }

        public void onPasswordClicked(View view) {
//            changePassword();
            Intent intent = new Intent(getApplicationContext(), ChangePassword.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }

        public void onPinClicked(View view) {
            changeTrxPinMode();
        }

        public void onBiometricClicked(View view) {
            Intent intent = new Intent(getApplicationContext(), ManageBiometric.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }

        public void onBlockAccountClicked(View view) {
            blockAccount();
        }

        public void onNotificationClicked(View view) {
            Intent intent = new Intent(getApplicationContext(), PushNotifications.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }

        public void onTermsClicked(View view){
            Intent intent = new Intent(getApplicationContext(), Policy.class);
            intent.putExtra("url", Constants.Terms);
            intent.putExtra("title", dictionary.get("termsCondition"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }

        public void onPrivacyClicked(View view) {
            Intent intent = new Intent(getApplicationContext(), Policy.class);
            intent.putExtra("url", Constants.Privacy);
            intent.putExtra("title", dictionary.get("privacyPolicy"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }

        public void onHelpClicked(View view) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/+96895770895")));
        }

        public void onFaqClicked(View view) {
            Intent intent = new Intent(getApplicationContext(), Policy.class);
            intent.putExtra("url", Constants.Faq);
            intent.putExtra("title", dictionary.get("faq"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }

        public void onCustomerSupportClicked(View view) {
            makePhoneCall("+968-95-770895");
        }

        private void makePhoneCall(String customerCareNumber) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", customerCareNumber, null));
            startActivity(intent);
        }

        public void onFeedbackClicked(View view) {
            Intent intent = new Intent(getApplicationContext(), Feedback.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }

        public void onBackClicked(View view) {
            onBackPressed();
        }

        public void onCloseDialogClicked(View view) {
            mBottomSheetDialog.dismiss();
        }

        public void onPasswordChange(View view) {
            String old = changeBinding.currentPassword.getText().toString();
            String newPwd = changeBinding.newPassword.getText().toString();
            String confirmPwd = changeBinding.confirmPassword.getText().toString();

            if (newPwd.trim().equals(confirmPwd)) {
                mBottomSheetDialog.dismiss();
                JSONObject params = new JSONObject();
                try {
                    params.put("oldpass", old);
                    params.put("newpass", newPwd);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                viewModel.onPasswordChange(params);
            } else {
                Toast.makeText(getApplicationContext(), "Password Mismatch", Toast.LENGTH_SHORT).show();
            }
        }

        public void onPinChanged(View view) {
            String pinNum;
            mBottomSheetDialog.dismiss();
            if (isFixedPinChecked) {
                pinNum = fixedPin.getText().toString();
                if (pinNum.length() == 4) {
                    updateTrxPin(pinNum, "true", "false");
                } else {
                    Toast.makeText(getApplicationContext(), "Fixed PIN Must be a four digit Number", Toast.LENGTH_SHORT).show();
                }

            } else if (isOtpChecked) {
                pinNum = "";
                updateTrxPin(pinNum, "false", "false");
            } else if (isBiometricChecked) {
                pinNum = "";
                updateTrxPin(pinNum, "false", "true");
            }
        }

        public void onBlockAccount(View view) {
            viewModel.block();
        }

    }
}