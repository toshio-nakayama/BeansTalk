package com.gmail.h1990.toshio.beanstalk.util;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

import com.bumptech.glide.Glide;

public class GlideUtils {

    public static void setPhoto(Context context, Uri uri, @DrawableRes int placeholder,
                                ImageView into) {
        if (uri != null) {
            Glide.with(context)
                    .load(uri)
                    .placeholder(placeholder)
                    .error(placeholder)
                    .into(into);
        }
    }
}
