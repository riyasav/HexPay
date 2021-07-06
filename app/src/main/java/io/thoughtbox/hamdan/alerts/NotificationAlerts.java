package io.thoughtbox.hamdan.alerts;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

import javax.inject.Inject;

import io.thoughtbox.hamdan.MainActivity;
import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.SplashScreen;
import io.thoughtbox.hamdan.databinding.ErrorDialogBinding;
import io.thoughtbox.hamdan.databinding.SuccessDialogBinding;
import io.thoughtbox.hamdan.databinding.TimeoutBinding;
import io.thoughtbox.hamdan.databinding.VpnAlertBinding;
import io.thoughtbox.hamdan.global.Dictionary;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;

public class NotificationAlerts {
    @Inject
    Dictionary dictionary;
    private Context context;
    private BottomSheetDialog mBottomSheetDialog;
    private ClicksBinds clicksBind;
    BottomSheetDialog vpnBottomSheetDialog;
    public NotificationAlerts(Context context) {
        this.context = context;
        clicksBind = new ClicksBinds(context);
        DaggerApiComponents.create().inject(this);
    }

    public void errorDialog(String msg) {
        mBottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        ErrorDialogBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.error_dialog, null, false);
        dialogBinding.setClickers(clicksBind);
        mBottomSheetDialog.setContentView(dialogBinding.getRoot());
        mBottomSheetDialog.setCancelable(true);
        dialogBinding.desc.setText(msg);
        mBottomSheetDialog.show();


    }

    public void successDialog(String msg) {
        mBottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        SuccessDialogBinding successDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.success_dialog, null, false);
        successDialogBinding.setClickers(clicksBind);
        mBottomSheetDialog.setContentView(successDialogBinding.getRoot());
        mBottomSheetDialog.setCancelable(true);
        successDialogBinding.desc.setText(msg);
        mBottomSheetDialog.show();


    }

    public void noInternet() {
        mBottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        View sheetView = LayoutInflater.from(context).inflate(R.layout.no_network_view, null);
        mBottomSheetDialog.setContentView(sheetView);
        mBottomSheetDialog.setCancelable(false);
        if (!mBottomSheetDialog.isShowing()) {
            mBottomSheetDialog.show();
        } else {
            mBottomSheetDialog.dismiss();
        }

        Button done = mBottomSheetDialog.findViewById(R.id.done);
        TextView desc = mBottomSheetDialog.findViewById(R.id.desc);
        if (done != null && desc != null) {

            done.setOnClickListener(view -> {
                if (isConnected(context)) {
                    mBottomSheetDialog.dismiss();
                }else {
                        Toast.makeText(context, "Network still unavailable", Toast.LENGTH_SHORT).show();
                    }
//                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//                if (cm != null) {
//                    boolean isConnected = cm.isDefaultNetworkActive();
//                    if (isConnected) {
//                        mBottomSheetDialog.dismiss();
//                    } else {
//                        Toast.makeText(context, "Network still unavailable", Toast.LENGTH_SHORT).show();
//                    }
//                }


            });
        }
    }

    public  boolean isConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())) {
            return true;
        }else{
            return false;
        }
    }

    public void dismissDialog() {
        if (mBottomSheetDialog != null) {
            mBottomSheetDialog.dismiss();
        }

    }

    public void timeOut() {
        mBottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        TimeoutBinding timeoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.timeout, null, false);
        timeoutBinding.setClickers(clicksBind);
        timeoutBinding.setLanguage(dictionary);
        mBottomSheetDialog.setContentView(timeoutBinding.getRoot());
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.show();

    }

    public void vpnAlert() {
        vpnBottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        VpnAlertBinding vpnAlertBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.vpn_alert, null, false);
        vpnAlertBinding.setClickers(clicksBind);
//        vpnAlertBinding.setLanguage(dictionary);
        vpnBottomSheetDialog.setContentView(vpnAlertBinding.getRoot());
        vpnBottomSheetDialog.setCancelable(false);
        if (!vpnBottomSheetDialog.isShowing()){
            vpnBottomSheetDialog.show();
        }

    }

    public void dismissVpnDialog() {
        if (vpnBottomSheetDialog != null) {
            vpnBottomSheetDialog.dismiss();
        }

    }

    public void disableCopyPaste(TextInputEditText textField) {
        textField.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {return false;}

            public void onDestroyActionMode(ActionMode actionMode) {}
        });

        textField.setLongClickable(false);
        textField.setTextIsSelectable(false);
    }

    public class ClicksBinds {
        Context context;

        public ClicksBinds(Context context) {
            this.context = context;
        }

        public void onCloseClick(View view) {
            mBottomSheetDialog.dismiss();
        }

        public void onLogout(View view) {
            mBottomSheetDialog.dismiss();
            Intent intent = new Intent(context, SplashScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            context.startActivity(intent);
        }

        public void onLoginClick(View view) {
            mBottomSheetDialog.dismiss();
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            context.startActivity(intent);
        }
    }
}
