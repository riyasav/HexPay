package io.thoughtbox.hamdan.transfers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import io.thoughtbox.hamdan.DashBoard;
import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.utls.ConnectionLiveData;
import io.thoughtbox.hamdan.utls.Constants;
import io.thoughtbox.hamdan.utls.Loader;
import io.thoughtbox.hamdan.alerts.NotificationAlerts;
import io.thoughtbox.hamdan.global.Universal;
import io.thoughtbox.hamdan.receipts.PaymentReceipt;

public class PaymentGateWay extends AppCompatActivity {
    private WebView webView;
    private String transfer_type, refNum;
    private Dialog progressDialog;

    private String url_to_check = "https://www.thoughtbox.io/transferconfirmation.html";
    private NotificationAlerts alerts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_gate_way);
        Init();

        Secured_connection_Condition();
        webView.setWebViewClient(new MyWebViewClient());

//        LiveData<Boolean> isLoadingData = viewModelnew.getIsLoading();
//        isLoadingData.observe(this, isLoading -> {
//            if (isLoading) {
//                progressDialog.show();
//                Log.d("Loader", "stated new loader");
//            } else {
//                progressDialog.dismiss();
//                Log.d("Loader", "stopped a loader");
//            }
//        });
//
//        viewModelnew.getLoadingError().observe(this, errorMsg -> {
//            if (errorMsg != null) {
//                if (progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
//                Toast.makeText(this, "" + errorMsg, Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        LiveData<String> htmlData = viewModelnew.htmlLiveData();
//        htmlData.observe(this, html -> webView.loadData(html, "text/html", "UTF-8"));

    }

    private void Init() {
        checkInternet();
        alerts = new NotificationAlerts(this);
        Loader loader = new Loader(this);
        progressDialog = loader.progress();
        progressDialog.show();
        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        Intent intent = getIntent();
        transfer_type = intent.getStringExtra("transfer_type");
        refNum = intent.getStringExtra("refnum");
        String token = Universal.getInstance().getLoginResponsedata().getToken();

        createWebPage(Constants.PaymentGateWayURL, transfer_type, refNum, token);
//        viewModelnew = new ViewModelProvider(this).get(PaymentViewModel.class);
//        viewModelnew.getPaymentGateway(transfer_type, refNum);


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

    private void createWebPage(String url, String txnType, String refNo, String token) {
        progressDialog.dismiss();
        String html = "<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'><title>Title</title></head><body><form id='transferpg' name='trandferpg' method='post' enctype='application/x-www-form-urlencoded' action='" + url + "'><input type='hidden' id='TransferType' name='TransferType' value='" + txnType + "'><input type='hidden' id='ReferenceNo' name='ReferenceNo' value='" + refNo + "'><input type='hidden' id='Token' name='Token' value='Bearer " + token + "'></form><script type='text/javascript'>document.forms[0].submit();</script></body></html>";
        Log.d("html", html);
        webView.loadData(html, "text/html", "UTF-8");
    }

    public void Secured_connection_Condition() {

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(final WebView view, final SslErrorHandler handler, SslError error) {
                Log.d("CHECK", "onReceivedSslError");
                AlertDialog.Builder builder = new AlertDialog.Builder(PaymentGateWay.this);
                AlertDialog alertDialog = builder.create();
                String message = "Certificate error.";
                switch (error.getPrimaryError()) {
                    case SslError.SSL_UNTRUSTED:
                        message = "The certificate authority is not trusted.";
                        break;
                    case SslError.SSL_EXPIRED:
                        message = "The certificate has expired.";
                        break;
                    case SslError.SSL_IDMISMATCH:
                        message = "The certificate Hostname mismatch.";
                        break;
                    case SslError.SSL_NOTYETVALID:
                        message = "The certificate is not yet valid.";
                        break;
                }

                message += " Do you want to continue anyway?";
                alertDialog.setTitle("SSL Certificate Error");
                alertDialog.setMessage(message);
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("CHECK", "Button ok pressed");
                        // Ignore SSL certificate errors
                        handler.proceed();
                    }
                });

                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("CHECK", "Button cancel pressed");
                        handler.cancel();
                    }
                });
                alertDialog.show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent1 = new Intent(PaymentGateWay.this, DashBoard.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        viewModelnew.clear();
    }

    private class MyWebViewClient extends WebViewClient {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("Starting url", url);
            if (Objects.equals(url.toUpperCase().trim(), url_to_check.toUpperCase().trim())) {
                Intent intent = new Intent(PaymentGateWay.this, PaymentReceipt.class);
                intent.putExtra("transfer_type", transfer_type);
                intent.putExtra("refnum", refNum);
                intent.putExtra("sourcePage", "GateWay");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d("starting url", url);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onPageFinished(WebView view, String url) {
//            if (!isFinishing()) {
//                progressDialog.show();
//            }
//            Log.d("finished url", url);
//            progressDialog.dismiss();
//            if (url.toUpperCase().trim().contains(url_to_check.toUpperCase().trim())) {
//                Intent intent = new Intent(PaymentGateWay.this, PaymentReceipt.class);
//                intent.putExtra("transfer_type", transfer_type);
//                intent.putExtra("refnum", refNum);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//            }

        }

    }
}