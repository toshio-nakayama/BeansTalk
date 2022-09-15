package com.gmail.h1990.toshio.beanstalk.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class FriendListModel {
    private String userId;
    private String userName;
    private String statusMessage;
    private String photoName;

    public FriendListModel(String userId, String userName, String statusMessage, String photoName) {
        this.userId = userId;
        this.userName = userName;
        this.statusMessage = statusMessage;
        this.photoName = photoName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }
}

