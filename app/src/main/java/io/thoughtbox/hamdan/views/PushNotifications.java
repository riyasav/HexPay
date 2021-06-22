package io.thoughtbox.hamdan.views;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

import io.thoughtbox.hamdan.Adapters.NotificationAdapter;
import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.utls.ConnectionLiveData;
import io.thoughtbox.hamdan.utls.Loader;
import io.thoughtbox.hamdan.alerts.NotificationAlerts;
import io.thoughtbox.hamdan.databinding.ActivityPushNotificationsBinding;
import io.thoughtbox.hamdan.global.Dictionary;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import io.thoughtbox.hamdan.model.notificationModel.NotificationResponseData;
import io.thoughtbox.hamdan.viewModel.NotificationViewModel;

public class PushNotifications extends AppCompatActivity {
    ActivityPushNotificationsBinding binding;
    NotificationViewModel viewModel;
    RecyclerView recyclerView;
    NotificationAdapter adapter;
    @Inject
    Dictionary dictionary;
    private Dialog progressDialog;
    private NotificationAlerts alerts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_push_notifications);
        binding.setLifecycleOwner(this);
        init();
        LiveData<Boolean> isSessionValid = viewModel.getIsSessionValid();
        isSessionValid.observe(this, isTokenValid -> {
            if (!isTokenValid) {
                alerts.timeOut();
            }
        });
        binding.back.setOnClickListener(view -> onBackPressed());

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

        LiveData<ArrayList<NotificationResponseData>> getAllNotification = viewModel.getNotificationLiveData();
        getAllNotification.observe(this, notifications -> {
            if (notifications != null && notifications.size() > 0) {
                adapter = new NotificationAdapter(notifications);
                recyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(this, "No notification available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        checkInternet();
        alerts = new NotificationAlerts(this);
        DaggerApiComponents.create().inject(this);
        Loader loader = new Loader(this);
        progressDialog = loader.progress();
        binding.textView27.setText(dictionary.get("notification"));
        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        recyclerView = binding.rv;
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.hasFixedSize();

        JSONObject params = new JSONObject();
        try {
            params.put("platform", "ANDROID");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        viewModel.getNotification(params);

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
    public void onBackPressed() {
        Intent intent = new Intent(this, SettingsGrid.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        super.onBackPressed();
    }
}
