package io.thoughtbox.hamdan;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import io.thoughtbox.hamdan.alerts.NotificationAlerts;
import io.thoughtbox.hamdan.databinding.ActivitySplashScreenBinding;
import io.thoughtbox.hamdan.global.Dictionary;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import io.thoughtbox.hamdan.model.dictionaryModel.DictionaryResponseData;
import io.thoughtbox.hamdan.utls.AppUpdate;
import io.thoughtbox.hamdan.utls.ConnectionLiveData;
import io.thoughtbox.hamdan.utls.DeviceUtils;
import io.thoughtbox.hamdan.utls.Loader;
import io.thoughtbox.hamdan.viewModel.SplashViewModel;

public class SplashScreen extends AppCompatActivity {
    public Dialog progressDialog;
    ActivitySplashScreenBinding binding;
    AlertDialog alertDialog;
    boolean isExistingUser;
    String currentLanguage = null;
    int SPLASH_DISPLAY_LENGTH = 1000;
    Loader loader;
    String vName;
    int vNumber;
    NotificationAlerts alerts;
    @Inject
    Dictionary dictionary;
    private HashMap<String, String> dictMap = new HashMap<>();
    private SplashViewModel splashViewModel;

    private FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen);
        binding.setLifecycleOwner(this);

        checkAutoUpdate();
        init();


//        checkExistingUser();
//        checkUserLanguage();
        LiveData<ArrayList<DictionaryResponseData>> getDictionaryLiveData = splashViewModel.getDictionaryLiveData();
        getDictionaryLiveData.observe(this, dictionaryResponsesList -> {
            for (DictionaryResponseData dictionaryResponse : dictionaryResponsesList) {
                dictMap.put(dictionaryResponse.getItem(), dictionaryResponse.getValue());
                dictionary.setTempLangMap(dictMap);
            }
            binding.group2.setVisibility(View.GONE);
            checkIsRooted();
        });

        LiveData<Boolean> isLoadingData = splashViewModel.getIsLoading();
        isLoadingData.observe(this, isLoading -> {
            if (isLoading) {
                binding.group2.setVisibility(View.VISIBLE);
            } else {
                binding.group2.setVisibility(View.GONE);
            }
        });


        splashViewModel.getLoadingError().observe(this, errorMsg -> {
            if (errorMsg != null) {
                if (errorMsg.toString().toUpperCase().equals("SUCCESS")) {
                    binding.group2.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.textView46.setText(errorMsg.toString());
                    Toast.makeText(this, "Language failed to load", Toast.LENGTH_SHORT).show();
                    String mode;
                    if (isExistingUser) {
                        mode = "BIOMETRIC";
                    } else {
                        mode = "CREDENTIAL";
                    }
                    Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                    mainIntent.putExtra("mode", mode);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                binding.group2.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
                binding.textView46.setText(errorMsg.toString());
            }
        });

    }

    private void init() {
        alerts = new NotificationAlerts(this);
        logNewToken();
        checkInternet();
        DaggerApiComponents.create().inject(this);
        splashViewModel = new ViewModelProvider(this).get(SplashViewModel.class);
//        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
//            if (!task.isSuccessful()) {
//                Log.w("TAG", "getInstanceId failed", task.getException());
//                return;
//            }
//            // Get new Instance ID token
//            String token = Objects.requireNonNull(task.getResult()).getToken();
//            Log.d("TAG", token);
//        });
        loader = new Loader(this);
        progressDialog = loader.progress();
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            vName = pInfo.versionName;
            vNumber = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void logNewToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("TAG", msg);
//                        Toast.makeText(SplashScreen.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private int getCurrentVersionCode() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            vName = pInfo.versionName;
            vNumber = pInfo.versionCode;
            return vNumber;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return vNumber;
    }

    private void getLanguages(String currentLanguage) {
        splashViewModel.getDictionry(currentLanguage);
    }

    private void checkAutoUpdate() {

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        mFirebaseRemoteConfig.activate();
                        String version = FirebaseRemoteConfig.getInstance().getString("latest_app_version");
                        if (!version.equals("null")) {
                            if (!Build.MANUFACTURER.toUpperCase().trim().equals("HUAWEI")) {
//                                int playStoreVersionCode = Integer.parseInt(version);
//                                if (playStoreVersionCode>getCurrentVersionCode()){
//                                    showPlayStoreDialog();
                                checkExistingUser();
                                checkUserLanguage();
                            }
//                            else {
//                                checkExistingUser();
//                                checkUserLanguage();
//                            }
                        }

                    }
                });
    }

    private void checkIsRooted() {
        startTimerThread();
    }

    private void showPlayStoreDialog() {
        Intent launchIntent = new Intent(this, AppUpdate.class);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(launchIntent);

    }

    private void startTimerThread() {
        new Handler().postDelayed(() -> {
            if (new DeviceUtils().isDeviceRooted(getApplicationContext())) {
                showAlertDialogAndExitApp("This device is rooted. You can't use this app on this device.");
            } else {
                String mode;
                if (isExistingUser) {
                    mode = "BIOMETRIC";
                } else {
                    mode = "CREDENTIAL";
                }
                Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                mainIntent.putExtra("mode", mode);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void checkExistingUser() {
        SharedPreferences sharedPref = getSharedPreferences("Bio-Status", MODE_PRIVATE);
        isExistingUser = sharedPref.getBoolean("isExistingUser", false);
    }

    private void checkUserLanguage() {
        SharedPreferences sharedPref = getSharedPreferences("Language", MODE_PRIVATE);
        currentLanguage = sharedPref.getString("currentLanguage", "ENGLISH");
        getLanguages(currentLanguage);
    }

    public void showAlertDialogAndExitApp(String message) {
        alertDialog = new AlertDialog.Builder(SplashScreen.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                (dialog, which) -> {
                    dialog.dismiss();
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                });

        alertDialog.show();
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
                alerts.dismissDialog();
            }
        });
    }

    @Override
    protected void onDestroy() {
        splashViewModel.clear();
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        super.onDestroy();
    }


}
