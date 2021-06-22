package io.thoughtbox.hamdan.views;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import javax.inject.Inject;

import io.thoughtbox.hamdan.Adapters.RateAdapter;
import io.thoughtbox.hamdan.DashBoard;
import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.utls.ConnectionLiveData;
import io.thoughtbox.hamdan.utls.Loader;
import io.thoughtbox.hamdan.alerts.NotificationAlerts;
import io.thoughtbox.hamdan.databinding.ActivityRatesBinding;
import io.thoughtbox.hamdan.global.Dictionary;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import io.thoughtbox.hamdan.model.rateModels.RateResponseData;
import io.thoughtbox.hamdan.viewModel.HomeViewModel;

public class Rates extends AppCompatActivity {
    HomeViewModel viewModel;
    ActivityRatesBinding binding;
    Loader loader;
    Dialog progressDialog;
    RateAdapter adapter;
    RecyclerView recyclerView;
    @Inject
    Dictionary dictionary;
    private NotificationAlerts alerts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rates);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_rates);
        binding.setLifecycleOwner(this);
        init();
        LiveData<Boolean> isSessionValid = viewModel.getSessionStatus();
        isSessionValid.observe(this, isTokenValid -> {
            if (!isTokenValid) {
                alerts.timeOut();
            }
        });
        LiveData<ArrayList<RateResponseData>> rateData = viewModel.getRateData();
        rateData.observe(this, rateResponseData -> {
            adapter = new RateAdapter(rateResponseData);
            recyclerView.setAdapter(adapter);

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
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(this, "" + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        DaggerApiComponents.create().inject(this);
        checkInternet();
        alerts = new NotificationAlerts(this);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        Uiinteraction uiinteraction = new Uiinteraction(this);
        binding.setClickers(uiinteraction);
        binding.setLangauges(dictionary);
        loader = new Loader(this);
        progressDialog = loader.progress();
        recyclerView = binding.rv;
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.hasFixedSize();
        viewModel.getUserRate();
    }

    @Override
    protected void onDestroy() {
        viewModel.clear();
        super.onDestroy();
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

    public class Uiinteraction {
        Context context;

        public Uiinteraction(Context context) {
            this.context = context;
        }

        public void onBackClicked(View view) {
            onBackPressed();
        }
    }
}
