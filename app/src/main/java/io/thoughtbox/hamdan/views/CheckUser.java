package io.thoughtbox.hamdan.views;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import io.thoughtbox.hamdan.MainActivity;
import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.utls.ConnectionLiveData;
import io.thoughtbox.hamdan.utls.Loader;
import io.thoughtbox.hamdan.alerts.NotificationAlerts;
import io.thoughtbox.hamdan.databinding.ActivityCheckUserBinding;
import io.thoughtbox.hamdan.databinding.StatusViewBinding;
import io.thoughtbox.hamdan.global.Universal;
import io.thoughtbox.hamdan.viewModel.CheckUserViewModel;
import io.thoughtbox.hamdan.viewModel.LoginViewModel;

public class CheckUser extends AppCompatActivity {
    private ActivityCheckUserBinding binding;
    private CheckUserViewModel viewModel;
    private LoginViewModel loginViewModel;
    private Dialog progressDialog;
    private NotificationAlerts alerts;
    private BottomSheetDialog mBottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_user);
        binding.setLifecycleOwner(this);
        init();

        LiveData<Boolean> idStatus = viewModel.getIdStatus();
        idStatus.observe(this, isIdExist -> {
            if (!isIdExist) {
                Intent intent = new Intent(this, Signup.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            } else {
                showStatusView();
//                Toast.makeText(this, "You are already registered.try login or use forgot password option to reset your password.", Toast.LENGTH_LONG).show();
            }
        });


        LiveData<Boolean> isLoadingData = viewModel.getIsLoading();
        isLoadingData.observe(this, isLoading -> {
            if (isLoading) {
                progressDialog.show();
                Log.d("Loader", "stated new loader");
            } else {
                progressDialog.dismiss();
                Log.d("Loader", "stopped a loader");
            }
        });

        viewModel.getLoadingError().observe(this, errorMsg -> {
            if (errorMsg != null) {
                progressDialog.dismiss();

            }
        });


        LiveData<Boolean> isForgotPwdLoadingData = loginViewModel.getIsLoading();
        isForgotPwdLoadingData.observe(this, isLoading -> {
            if (isLoading) {
                progressDialog.show();
            } else {
                progressDialog.dismiss();
            }
        });


        loginViewModel.getLoadingError().observe(this, errorMsg -> {
            if (errorMsg != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(this, "" + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        LiveData<Boolean> forgotPasswordResponse = loginViewModel.getForgotPasswordLiveData();

        forgotPasswordResponse.observe(this, isPasswordResetSuccessful -> {
            if (isPasswordResetSuccessful) {
                alerts.successDialog("Your new Login credentials has been sent to your registered mobile number");
            } else {
                alerts.errorDialog("Password Reset failed.Retry later");
            }
        });

    }

    private void showStatusView() {
        mBottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        StatusViewBinding statuBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.status_view, null, false);
        mBottomSheetDialog.setContentView(statuBinding.getRoot());
        statuBinding.setClickers(CheckUser.this);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.show();
    }

    private void init() {
        checkInternet();
        viewModel = new ViewModelProvider(this).get(CheckUserViewModel.class);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        Loader loader = new Loader(this);
        progressDialog = loader.progress();
        alerts = new NotificationAlerts(this);
        binding.setClickers(CheckUser.this);

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

    public void onVerifyClicked(View view) {
        if (!binding.idNumber.getText().toString().isEmpty()) {
            Universal.getInstance().setIdNumber(binding.idNumber.getText().toString());
            viewModel.checkUserId(binding.idNumber.getText().toString());
        } else {
            Toast.makeText(this, "ID number should be not be empty", Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackClicked(View view) {
        onBackPressed();
    }

    public void onLoginClicked(View view) {
        mBottomSheetDialog.dismiss();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("mode", "CREDENTIAL");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void onResetPasswordClicked(View view) {
        loginViewModel.forgotPassword(binding.idNumber.getText().toString());
        mBottomSheetDialog.dismiss();
    }

    public void onCloseClick(View view) {
        mBottomSheetDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("mode", "CREDENTIAL");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        viewModel.clear();
        super.onDestroy();
    }
}
