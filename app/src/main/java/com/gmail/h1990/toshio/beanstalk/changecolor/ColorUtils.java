package com.gmail.h1990.toshio.beanstalk.changecolor;

import static com.gmail.h1990.toshio.beanstalk.common.Extras.STYLE_RESOURCE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.gmail.h1990.toshio.beanstalk.R;

public class ColorUtils {

    public static void setTheme(Activity activity) {
        SharedPreferences sharedPref =
                activity.getSharedPreferences("theme", Context.MODE_PRIVATE);
        int styleRes = sharedPref.getInt(STYLE_RESOURCE, R.style.Theme_BeansTalk);
        activity.setTheme(styleRes);
    }

}
