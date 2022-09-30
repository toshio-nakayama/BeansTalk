package com.gmail.h1990.toshio.beanstalk.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastGenerator {
    public static final int CENTER = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;

    private Context context;
    private CharSequence text;
    private int length;
    private Integer gravity;

    public static class Builder {
        private Context context;
        private CharSequence text;
        private int duration;
        private Integer gravity;

        public Builder(Context context) {
            this.context = context;
            this.text = "";
            this.duration = Toast.LENGTH_SHORT;
        }

        public Builder text(CharSequence message) {
            this.text = message;
            return this;
        }

        public Builder resId(int resId) {
            this.text = context.getText(resId);
            return this;
        }

        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder gravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public ToastGenerator create() {
            return new ToastGenerator(this);
        }

        public void build() {
            new ToastGenerator(this).show();
        }
    }

    private ToastGenerator(Builder builder) {
        this.context = builder.context;
        this.text = builder.text;
        this.length = builder.duration;
        this.gravity = builder.gravity;
    }

    public void show() {
        Toast toast = Toast.makeText(context, text, length);
        if (gravity != null) {
            toast.setGravity(gravity, 0, 0);
        } else {
            toast.setGravity(CENTER, 0, 0);
        }
        toast.show();
    }
}

