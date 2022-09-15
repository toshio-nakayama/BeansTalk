package com.gmail.h1990.toshio.beanstalk.util;

import android.content.Context;
import android.net.ConnectivityManager;

public class ConnectivityCheck {
    public static boolean connectionAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null) {
            return connectivityManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

}
