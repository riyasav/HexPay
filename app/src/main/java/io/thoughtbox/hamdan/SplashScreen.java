package io.thoughtbox.hamdan;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.thoughtbox.hamdan.alerts.NotificationAlerts;
import io.thoughtbox.hamdan.databinding.ActivitySplashScreenBinding;
import io.thoughtbox.hamdan.global.Dictionary;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import io.thoughtbox.hamdan.model.BannerResponse;
import io.thoughtbox.hamdan.model.dictionaryModel.DictionaryResponseData;
import io.thoughtbox.hamdan.utls.AppData;
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
    AppData appData;


    private AppUpdateManager appUpdateManager;
    private static final int IMMEDIATE_APP_UPDATE_REQ_CODE = 124;

    private FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen);
        binding.setLifecycleOwner(this);
        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        binding.textView46.setEnabled(false);
        checkAutoUpdate();
        init();

        binding.textView46.setOnClickListener(view -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=io.thoughtbox.hamdan")));
        });
//        checkExistingUser();
//        checkUserLanguage();
        LiveData<BannerResponse> getBannerLiveData = splashViewModel.getBannerLiveData();
        getBannerLiveData.observe(this, bannerData -> {
            String hasMessage = bannerData.getHasmessage();
            String message = bannerData.getMessage();
            if (hasMessage.equals("1")) {
                binding.card.setVisibility(View.VISIBLE);
                binding.textView.setText(message);
            } else {
                binding.card.setVisibility(View.GONE);
                binding.textView.setText(message);

            }
        });
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
        appData = new AppData(this);

        logNewToken();
        checkInternet();
        DaggerApiComponents.create().inject(this);
        splashViewModel = new ViewModelProvider(this).get(SplashViewModel.class);
        splashViewModel.getBannerData();
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
                .addOnCompleteListener(task -> {
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
                                int playStoreVersionCode = Integer.parseInt(version);
                                if (playStoreVersionCode > getCurrentVersionCode()) {

                                    if (verifyInstallerId(this)) {
//                                        showPlayStoreDialog();
                                    binding.textView46.setEnabled(true);
                                    SpannableString string = new SpannableString("Latest version is available on play store. Click here to update.");
                                    string.setSpan(new UnderlineSpan(), 43, 53, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    binding.textView46.setText(string);
                                    checkUpdate();
                                    } else {
                                        Toast.makeText(this, "This App version is not installed via play store. kindly uninstall this version and install from play store", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    checkExistingUser();
                                    getLanguages(appData.getDeviceLanguage());
                                }
                            } else {
                                checkExistingUser();
                                getLanguages(appData.getDeviceLanguage());
                            }
                        }

                    }
                });
    }

    private void checkUpdate() {
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                startUpdateFlow(appUpdateInfo);
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                startUpdateFlow(appUpdateInfo);
            }
        });
    }


    private void startUpdateFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    SplashScreen.IMMEDIATE_APP_UPDATE_REQ_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMMEDIATE_APP_UPDATE_REQ_CODE) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Update canceled", Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Updated successfully", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Update failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void checkIsRooted() {
        startTimerThread();
    }

    boolean verifyInstallerId(Context context) {
        // A list with valid installers package name
        List<String> validInstallers = new ArrayList<>(Arrays.asList("com.android.vending", "com.google.android.feedback"));

        // The package name of the app that has installed your app
        final String installer = context.getPackageManager().getInstallerPackageName(context.getPackageName());

        // true if your app has been downloaded from Play Store
        return installer != null && validInstallers.contains(installer);
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
