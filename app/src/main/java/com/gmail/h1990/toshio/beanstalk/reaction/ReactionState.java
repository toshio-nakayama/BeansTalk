package com.gmail.h1990.toshio.beanstalk.reaction;

public enum ReactionState {
    STATE_CELEBRATE(0b00001),
    STATE_CRYING(0b00010),
    STATE_FURIOUS(0b00100),
    STATE_PLEADING(0b01000),
    STATE_WINK(0b10000);

    private int flag;

    private ReactionState(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    public boolean containsFlag(int value) {
        if ((value & flag) != 0) {
            return true;
        }
        return false;
    }
}
