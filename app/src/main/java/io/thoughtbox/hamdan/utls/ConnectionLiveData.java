package io.thoughtbox.hamdan.utls;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;

import io.thoughtbox.hamdan.model.ConnectionModel;


public class ConnectionLiveData extends LiveData<ConnectionModel> {

    private Context context;

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @SuppressWarnings("deprecation")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null) {
                NetworkInfo activeNetwork = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                if (isConnected) {
                    switch (activeNetwork.getType()) {
                        case ConnectivityManager.TYPE_VPN:
                            postValue(new ConnectionModel(Constants.WifiData, true, true));
                            break;
                        case ConnectivityManager.TYPE_WIFI:
                            if (isVpnConnected()) {
                                postValue(new ConnectionModel(Constants.WifiData, true, true));
                            } else {
                                postValue(new ConnectionModel(Constants.WifiData, true, false));
                            }
                            break;
                        case ConnectivityManager.TYPE_MOBILE:
                            if (isVpnConnected()) {
                                postValue(new ConnectionModel(Constants.MobileData, true, true));
                            } else {
                                postValue(new ConnectionModel(Constants.MobileData, true, false));
                            }
                            break;
//                        case ConnectivityManager.TYPE_VPN:
//                            postValue(new ConnectionModel(Constants.VPN, true,true));
//                            break;
                    }
                } else {
                    postValue(new ConnectionModel(0, false, false));
                }
//                if (isVpnConnected()) {
//                    postValue(new ConnectionModel(Constants.VPN, true, true));
//                }
            }
        }
    };

    public ConnectionLiveData(Context context) {
        this.context = context;
    }

    @Override
    protected void onActive() {
        super.onActive();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(networkReceiver, filter);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        context.unregisterReceiver(networkReceiver);
    }

    public boolean isVpnConnected() {
        String iface = "";
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp())
                    iface = networkInterface.getName();
                Log.d("DEBUG", "IFACE NAME: " + iface);
                if (iface.contains("tun") || iface.contains("ppp") || iface.contains("pptp")) {
                    return true;
                }
            }
        } catch (SocketException e1) {
            e1.printStackTrace();
        }

        return false;
    }
}