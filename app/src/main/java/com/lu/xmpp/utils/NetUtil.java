package com.lu.xmpp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

/**
 * Created by xuyu on 2015/11/11.
 */
public class NetUtil {

    public static boolean isNetWorkAvaliable(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = manager.getAllNetworks();

            for (Network network : networks) {
                NetworkInfo info = manager.getNetworkInfo(network);
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        } else {
            NetworkInfo[] infos = manager.getAllNetworkInfo();
            for (NetworkInfo info : infos) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }


        return false;
    }
}
