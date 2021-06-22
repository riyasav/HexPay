package io.thoughtbox.hamdan.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import javax.inject.Inject;

import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.SplashScreen;
import io.thoughtbox.hamdan.alerts.NotificationAlerts;
import io.thoughtbox.hamdan.databinding.ActivityChangePasswordBinding;
import io.thoughtbox.hamdan.global.Dictionary;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import io.thoughtbox.hamdan.model.loginModel.OtpResponsedata;
import io.thoughtbox.hamdan.utls.Loader;
import io.thoughtbox.hamdan.viewModel.SettingsViewModel;

public class ChangePassword extends AppCompatActivity {
    ActivityChangePasswordBinding binding;
    private Dialog progressDialog;
    private SettingsViewModel viewModel;
    private TextInputEditText pass_word;
    boolean isLengthOk, isUpperCaseOk, isNumberOk, isCharOk = false;
    private NotificationAlerts alerts;
    @Inject
    Dictionary dictionary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password);
        binding.setLifecycleOwner(this);
        binding.setClickers(this);

        init();

        binding.newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (pass_word.getText().toString().length() > 0) {
                    if (hasLength(pass_word.getText().toString())) {
                        binding.charLength.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_green, 0, 0, 0);
                        isLengthOk = true;
                    } else {
                        binding.charLength.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_gray_check, 0, 0, 0);
                        isLengthOk = false;
                    }
                    if (hasSymbol(pass_word.getText().toString())) {
                        binding.splChar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_green, 0, 0, 0);
                        isCharOk = true;
                    } else {
                        binding.splChar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_gray_check, 0, 0, 0);
                        isCharOk = false;
                    }
                    if (hasUpperCase(pass_word.getText().toString())) {
                        binding.charUpper.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_green, 0, 0, 0);
                        isUpperCaseOk = true;
                    } else {
                        binding.charUpper.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_gray_check, 0, 0, 0);
                        isUpperCaseOk = false;
                    }
                    if (hasNumber(pass_word.getText().toString())) {
                        binding.charNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_green, 0, 0, 0);
                        isNumberOk = true;
                    } else {
                        binding.charNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_gray_check, 0, 0, 0);
                        isNumberOk = false;
                    }
                } else {
                    binding.charUpper.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_gray_check, 0, 0, 0);
                    isUpperCaseOk = false;
                    binding.splChar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_gray_check, 0, 0, 0);
                    isCharOk = false;
                    binding.charLength.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_gray_check, 0, 0, 0);
                    isLengthOk = false;
                    binding.charNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_gray_check, 0, 0, 0);
                    isNumberOk = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


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

        viewModel.getLoadingError().observe(this, errorMsg -> {
            if (errorMsg != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(this, "" + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
        LiveData<OtpResponsedata> changePassword = viewModel.getChangePassWordLiveData();
        changePassword.observe(this, passwordChangeResponse -> {
            if (passwordChangeResponse.getResult().toUpperCase().equals("TRUE")) {
                Toast.makeText(this, "Your login password changed successfully", Toast.LENGTH_SHORT).show();
//                alerts.successDialog("Your login password changed successfully");
                Intent mainIntent = new Intent(ChangePassword.this, SplashScreen.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(mainIntent);
            } else {
                alerts.errorDialog("Your attempt to change login password failed");
            }
        });
    }

    public void onBackClicked(View view) {
        finish();
    }

    private void init() {
        DaggerApiComponents.create().inject(this);
        binding.setClickers(this);
        binding.setLanguage(dictionary);
        Loader loader = new Loader(this);
        progressDialog = loader.progress();
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        pass_word = binding.newPassword;
        alerts = new NotificationAlerts(this);
    }

    private boolean hasLength(CharSequence data) {
        return String.valueOf(data).length() >= 8;
    }

    private boolean hasSymbol(CharSequence data) {
        String password = String.valueOf(data);
        return !password.matches("[A-Za-z0-9 ]*");
    }

    private boolean hasUpperCase(CharSequence data) {
        String password = String.valueOf(data);
        return !password.equals(password.toLowerCase());
    }

    private boolean hasNumber(CharSequence data) {
        String password = String.valueOf(data);
        return password.matches(".*\\d.*");
    }

    public void onSubmitClicked(View view) {
        if (validate()) {
            if (Objects.requireNonNull(binding.newPassword.getText()).toString().equals(Objects.requireNonNull(binding.confirmPassword.getText()).toString())) {
                if (isCharOk && isLengthOk && isNumberOk && isUpperCaseOk) {
                    JSONObject params = new JSONObject();
                    try {
                        params.put("newpass", binding.newPassword.getText().toString());
                        params.put("oldpass", binding.oldPass.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    viewModel.onPasswordChange(params);
                } else {
                    binding.newPassword.requestFocus();
                }
            } else {
                Toast.makeText(this, "Password mismatch", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private boolean hasText(TextInputEditText editText) {
        String text = Objects.requireNonNull(editText.getText()).toString().trim();
        editText.setError(null);
        if (text.length() == 0) {
            editText.setError("Field cannot be empty");
            return false;
        }
        return true;
    }

    private boolean validate() {
        boolean check = true;
        if (!hasText(binding.oldPass)) check = false;
        if (!hasText(binding.newPassword)) check = false;
        if (!hasText(binding.confirmPassword)) check = false;
        return check;
    }

}