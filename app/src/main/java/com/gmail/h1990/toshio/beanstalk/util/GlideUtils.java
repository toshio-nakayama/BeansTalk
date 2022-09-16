package com.gmail.h1990.toshio.beanstalk.util;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gmail.h1990.toshio.beanstalk.R;

public class GlideUtils {

    public static void setPhoto(Context context, Uri uri, ImageView imageView) {
        if (uri != null) {
            Glide.with(context)
                    .load(uri)
                    .placeholder(R.drawable.default_profile)
                    .error(R.drawable.default_profile)
                    .into(imageView);
        }
    }
}
