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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getBackgroundPhoto() {
        return backgroundPhoto;
    }

    public void setBackgroundPhoto(String backgroundPhoto) {
        this.backgroundPhoto = backgroundPhoto;
    }
}
