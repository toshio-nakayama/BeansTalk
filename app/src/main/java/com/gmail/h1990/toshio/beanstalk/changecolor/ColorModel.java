package com.gmail.h1990.toshio.beanstalk.changecolor;

import androidx.annotation.ColorInt;
import androidx.annotation.StyleRes;

public class ColorModel {
    @ColorInt
    private int argb;

    @StyleRes
    private int styleRes;

    public ColorModel(int argb, int styleRes) {
        this.argb = argb;
        this.styleRes = styleRes;
    }

    public int getArgb() {
        return argb;
    }

    public int getStyleRes() {
        return styleRes;
    }
}

