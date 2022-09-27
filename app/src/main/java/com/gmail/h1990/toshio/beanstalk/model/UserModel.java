package com.gmail.h1990.toshio.beanstalk.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserModel {
    private String name;
    private String email;
    private String statusMessage;
    private String photo;
    private String backgroundPhoto;

    private UserModel() {
    }

    public UserModel(String name, String email, String statusMessage, String photo, String backgroundPhoto) {
        this.name = name;
        this.email = email;
        this.statusMessage = statusMessage;
        this.photo = photo;
        this.backgroundPhoto = backgroundPhoto;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
