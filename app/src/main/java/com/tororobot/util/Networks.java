package com.tororobot.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.tororobot.TororobotAplications;

/**
 * Created by Roger Patiño on 15/06/2016.
 */
public class Networks {

    public static synchronized boolean isOnline(Context ctx) {
        Context context = (ctx == null) ? TororobotAplications.getContext() : ctx;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static synchronized boolean isConnectedWifi(Context ctx) {
        Context context = (ctx == null) ? TororobotAplications.getContext() : ctx;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    public static synchronized boolean isConnectedMobile(Context ctx) {
        Context context = (ctx == null) ? TororobotAplications.getContext() : ctx;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }


}