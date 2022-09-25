package com.gmail.h1990.toshio.beanstalk.reaction;

import android.graphics.drawable.Drawable;

public class ReactionModel {
    private Drawable graphics;
    private ReactionState reactionState;

    public ReactionModel(Drawable graphics, ReactionState reactionState) {
        this.graphics = graphics;
        this.reactionState = reactionState;
    }

    public Drawable getGraphics() {
        return graphics;
    }

    public void setGraphics(Drawable graphics) {
        this.graphics = graphics;
    }

    public ReactionState getReactionState() {
        return reactionState;
    }

    public void setReactionState(ReactionState reactionState) {
        this.reactionState = reactionState;
    }
}
