package com.gmail.h1990.toshio.beanstalk.model;

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

    public String getUserName() {
        return userName;
    }

    public String getPhotoName() {
        return photoName;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}

