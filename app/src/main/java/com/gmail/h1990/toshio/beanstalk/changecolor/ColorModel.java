package com.gmail.h1990.toshio.beanstalk.changecolor;

import androidx.annotation.ColorInt;

public class ColorModel {
    @ColorInt
    private int argb;

    public ColorModel(@ColorInt int argb) {
        this.argb = argb;
    }

    public int getArgb() {
        return argb;
    }

    public void setArgb(@ColorInt int argb) {
        this.argb = argb;
    }
}

