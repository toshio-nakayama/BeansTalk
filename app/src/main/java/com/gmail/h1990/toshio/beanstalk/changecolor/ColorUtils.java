package com.gmail.h1990.toshio.beanstalk.changecolor;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.gmail.h1990.toshio.beanstalk.R;
import com.gmail.h1990.toshio.beanstalk.common.Extras;

public class ColorUtils {

    public static void setTheme(Activity activity) {
        SharedPreferences sharedPref =
                activity.getSharedPreferences(Extras.THEME, Context.MODE_PRIVATE);
        int styleRes = sharedPref.getInt(Extras.STYLE_RESOURCE, R.style.Theme_BeansTalk);
        activity.setTheme(styleRes);
    }

}
