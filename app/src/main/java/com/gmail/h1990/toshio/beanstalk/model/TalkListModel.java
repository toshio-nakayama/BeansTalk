package com.gmail.h1990.toshio.beanstalk.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class TalkListModel {
    private String userId;
    private String userName;
    private String photoName;
    private String unreadCount;
    private String lastMessage;
    private String time;

    public TalkListModel(String userId, String userName, String photoName, String unreadCount, String lastMessage, String time) {
        this.userId = userId;
        this.userName = userName;
        this.photoName = photoName;
        this.unreadCount = unreadCount;
        this.lastMessage = lastMessage;
        this.time = time;
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

    public String getUnreadCount() {
        return unreadCount;
    }
}
