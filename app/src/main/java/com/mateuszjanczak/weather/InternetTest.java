package com.mateuszjanczak.weather;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class InternetTest {

    private static InternetTest instance = new InternetTest();
    private static Context context;
    private ConnectivityManager connectivityManager;
    private NetworkInfo wifiInfo, mobileInfo;
    private boolean connected = false;

    public static InternetTest getInstance(Context ctx) {
        context = ctx.getApplicationContext();
        return instance;
    }

    public boolean isOnline() {
        try {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
            return connected;

        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }
        return connected;
    }
}