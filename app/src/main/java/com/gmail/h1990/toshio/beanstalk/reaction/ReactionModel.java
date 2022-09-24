package com.gmail.h1990.toshio.beanstalk.reaction;

import android.graphics.drawable.Drawable;

public class ReactionModel {
    private Drawable graphics;
    private int reactionStatus;

    public ReactionModel(Drawable graphics, int reactionStatus) {
        this.graphics = graphics;
        this.reactionStatus = reactionStatus;
    }

    public Drawable getGraphics() {
        return graphics;
    }

    public void setGraphics(Drawable graphics) {
        this.graphics = graphics;
    }

    public int getReactionStatus() {
        return reactionStatus;
    }

    public void setReactionStatus(int reactionStatus) {
        this.reactionStatus = reactionStatus;
    }
}
