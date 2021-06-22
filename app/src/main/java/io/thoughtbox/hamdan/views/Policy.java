package io.thoughtbox.hamdan.views;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.utls.ConnectionLiveData;
import io.thoughtbox.hamdan.alerts.NotificationAlerts;
import io.thoughtbox.hamdan.databinding.ActivityPolicyBinding;

public class Policy extends AppCompatActivity {
    ActivityPolicyBinding binding;
    private NotificationAlerts alerts;
//    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_policy);
        init();
        binding.back.setOnClickListener(view -> onBackPressed());
    }

    private void init() {
        checkInternet();
        alerts = new NotificationAlerts(this);
        WebView webView = binding.webview;
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String titleLbl = intent.getStringExtra("title");
        binding.title.setText(titleLbl);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl(url);
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
        super.onBackPressed();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String request) {
            view.loadUrl(request);
            return true;
        }
    }
}
