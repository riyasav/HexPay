package io.thoughtbox.hamdan.views;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import io.thoughtbox.hamdan.Adapters.LanguageAdapter;
import io.thoughtbox.hamdan.Adapters.LanguageClickListner;
import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.utls.AppData;
import io.thoughtbox.hamdan.utls.ConnectionLiveData;
import io.thoughtbox.hamdan.utls.Loader;
import io.thoughtbox.hamdan.alerts.NotificationAlerts;
import io.thoughtbox.hamdan.databinding.ActivityLanguageBinding;
import io.thoughtbox.hamdan.global.Dictionary;
import io.thoughtbox.hamdan.global.Universal;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import io.thoughtbox.hamdan.model.dictionaryModel.DictionaryResponseData;
import io.thoughtbox.hamdan.model.languageModel.LanguageResponseData;
import io.thoughtbox.hamdan.viewModel.LoginViewModel;

public class Language extends AppCompatActivity implements LanguageClickListner {
    LoginViewModel viewModel;
    ActivityLanguageBinding binding;
    RecyclerView langList;
    GridLayoutManager layoutManager;
    LanguageAdapter adapter;
    @Inject
    Dictionary dictionary;
    NotificationAlerts alerts;
    String currentLang = "ENGLISH";
    private Dialog progressDialog;
    private TextView emptymsg;
    private String langaugeUsed;
    AppData appData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_language);
        init();
        LiveData<Boolean> isSessionValid = viewModel.getIsSessionValid();
        isSessionValid.observe(this, isTokenValid -> {
            if (!isTokenValid) {
                alerts.timeOut();
            }
        });
        binding.back.setOnClickListener(view -> {
            onBackPressed();
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

        LiveData<ArrayList<LanguageResponseData>> getAllLang = viewModel.getLanguageListLiveData();
        getAllLang.observe(this, allLanguages -> {
            if (allLanguages != null && allLanguages.size() > 0) {
                adapter = new LanguageAdapter(allLanguages, Language.this, langaugeUsed);
                langList.setAdapter(adapter);
            } else {
                emptymsg.setVisibility(View.VISIBLE);
            }
        });

        LiveData<Boolean> updateLanguage = viewModel.getLanguageUpdateLiveData();
        updateLanguage.observe(this, status -> {
            if (status) {
                Universal.getInstance().getLoginResponsedata().setLang(langaugeUsed);
                viewModel.getDictionry(langaugeUsed);
                appData.setDeviceLanguage(currentLang);

//                setRegisteredLanguage(currentLang);
            } else {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        });

        LiveData<ArrayList<DictionaryResponseData>> liveDictObj = viewModel.getDictionaryLiveData();
        liveDictObj.observe(this, dictionaryResponsesList -> {
            HashMap<String, String> dictMap = new HashMap<>();
            for (DictionaryResponseData dictionaryResponse : dictionaryResponsesList) {
                dictMap.put(dictionaryResponse.getItem(), dictionaryResponse.getValue());
                Dictionary.getInstance().setLangMap(dictMap);
            }
            Toast.makeText(this, "Language Updated", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(getIntent());
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void init() {
        checkInternet();
        DaggerApiComponents.create().inject(this);
        alerts = new NotificationAlerts(this);
        appData = new AppData(this);
        Loader loader = new Loader(this);
        progressDialog = loader.progress();
        langaugeUsed = Universal.getInstance().getLoginResponsedata().getLang();
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding.setLanguage(dictionary);
        viewModel.getAllLanguages();
        emptymsg = binding.emptymsg;
        langList = binding.rv;
        layoutManager = new GridLayoutManager(this, 3);
        langList.setLayoutManager(layoutManager);

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
    public void onLangClicked(LanguageResponseData lang) {
        currentLang = lang.getName();
        updateLanguage(lang.getName());
    }

    private void updateLanguage(String name) {
        JSONObject params = new JSONObject();
        try {
            params.put("language", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        langaugeUsed = name;
        viewModel.updateLanguage(params);
    }

    @Override
    public void onBackPressed() {
        Intent mainIntent = new Intent(this, SettingsGrid.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        super.onBackPressed();
    }

    private void setRegisteredLanguage(String lang) {
        SharedPreferences.Editor editor = getSharedPreferences("Language", MODE_PRIVATE).edit();
        editor.putString("currentLanguage", lang);
        editor.apply();
    }
}
