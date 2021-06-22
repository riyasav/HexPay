package io.thoughtbox.hamdan.utls;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NetworkChangeReceiver extends BroadcastReceiver {
    public static ConnectivityReceiverListener connectivityReceiverListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm != null) {
                    if (connectivityReceiverListener != null) {
                        connectivityReceiverListener.onNetworkConnectionChanged(cm.isDefaultNetworkActive());
                    }
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }


    public boolean isVPNConnected(){
        List<String> networkList = new ArrayList<>();
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp())
                    networkList.add(networkInterface.getName());
            }
        } catch (Exception ex) {
            Log.d("VPN","isVpnUsing Network List didn't received");
        }

        return networkList.contains("tun0");
    }
}
