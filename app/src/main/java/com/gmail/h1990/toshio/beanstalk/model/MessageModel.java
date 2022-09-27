package com.gmail.h1990.toshio.beanstalk.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class MessageModel {
    private String message;
    private String messageFrom;
    private String messageId;
    private long messageTime;
    private String messageType;
    private int reactionStatus;

    public MessageModel() {
    }

    public MessageModel(String message, String messageFrom, String messageId, long messageTime, String messageType, int reactionStatus) {
        this.message = message;
        this.messageFrom = messageFrom;
        this.messageId = messageId;
        this.messageTime = messageTime;
        this.messageType = messageType;
        this.reactionStatus = reactionStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public String getMessageId() {
        return messageId;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public String getMessageType() {
        return messageType;
    }

    public int getReactionStatus() {
        return reactionStatus;
    }
}

