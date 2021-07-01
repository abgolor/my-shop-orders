package com.example.myshop.InternetUtility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/*This class checks for internet connection. It uses the isConnectedToInternet method to be able
 *to chack if the user's device have internet connection or not.
 */
public class ConnectionChecker {
    public static boolean isConnectedToInternet (Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


        if(connectivityManager != null){
            return wifiConn != null && wifiConn.isConnected() || (mobileConn != null && mobileConn.isConnected());
        }
        return false;
    }
}
