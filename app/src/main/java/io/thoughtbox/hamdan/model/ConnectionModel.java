package io.thoughtbox.hamdan.model;

public class ConnectionModel {

    private int type;
    private boolean isConnected;
    private boolean isVpn;

    public ConnectionModel(int type, boolean isConnected,boolean isVpn) {
        this.type = type;
        this.isConnected = isConnected;
        this.isVpn = isVpn;
    }

    public int getType() {
        return type;
    }

    public boolean getIsConnected() {
        return isConnected;
    }

    public boolean getIsVpnConnected() {
        return isVpn;
    }
}