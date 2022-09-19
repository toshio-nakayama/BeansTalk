package com.gmail.h1990.toshio.beanstalk.reaction;

import android.graphics.drawable.Drawable;

public class ReactionModel {
    private Drawable graphics;

    public ReactionModel(Drawable graphics) {
        this.graphics = graphics;
    }

    public Drawable getGraphics() {
        return graphics;
    }

    public void setGraphics(Drawable graphics) {
        this.graphics = graphics;
    }
}
