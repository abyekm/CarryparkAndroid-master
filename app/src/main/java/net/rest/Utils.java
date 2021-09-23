package net.rest;

import android.content.Context;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import net.CarryParkApplication;


/**
 * Created by Binin Regi on 12/10/16.
 */

public class Utils {
    public static boolean isNetworkConnectionAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) CarryParkApplication.getCurrentContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }
}
