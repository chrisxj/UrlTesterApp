package com.example.chrisjohnson.myapplication;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by chrisjohnson on 11/12/14.
 */
public class NetworkStatusHelper {

    private final ConnectivityManager connMgr;
    private boolean networkIsUp = false;
    private boolean wifiIsConnected = false;
    private boolean radioIsConnected = false;
    private String wifiExtraInfo = "";
    private String radioExtraInfo = "";
    private boolean isRoaming = false;

    public NetworkStatusHelper(ConnectivityManager connMgr) {
        this.connMgr = connMgr;
        // store the network information object for later
        checkNetworkIsUp();
        checkInterfaces();
    }

    public void UpdateStatus() {
        // refresh stored values
        checkNetworkIsUp();
        checkInterfaces();
    }

    private void checkNetworkIsUp() {
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // network is up
            networkIsUp = true;
        }
    }

    private void checkInterfaces() {
        NetworkInfo wifiInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            // wifi is connected
            wifiIsConnected = true;
            // get extra wifi info, if available
            if (wifiInfo.getExtraInfo() != null) {
                wifiExtraInfo = wifiInfo.getExtraInfo();
            }
        }

        NetworkInfo radioInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (radioInfo != null && radioInfo.isConnected()) {
            // radio is connected
            radioIsConnected = true;
            // get radio extra info, if available
            if (radioInfo.getExtraInfo() != null) {
                radioExtraInfo = radioInfo.getExtraInfo();
            }
            // get roaming info
            isRoaming = radioInfo.isRoaming();
        }

    }

    public boolean IsNetworkUp() {
        return networkIsUp;
    }

    public boolean IsWifiConnected() { return wifiIsConnected; }

    public boolean IsRadioConnected() { return radioIsConnected; }

    public String GetRadioExtraInfo() { return radioExtraInfo; }

    public String GetWifiExtraInfo() { return wifiExtraInfo; }

    public boolean IsRoaming() { return isRoaming; }
}
