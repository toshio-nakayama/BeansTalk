package com.gmail.h1990.toshio.beanstalk.model;

import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.EMAIL;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.NAME;
import static com.gmail.h1990.toshio.beanstalk.common.NodeNames.PHOTO;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class UserModel {
    public String name;
    public String email;
    public String photo;

    public UserModel() {
    }

    public UserModel(String name, String email, String photo) {
        this.name = name;
        this.email = email;
        this.photo = photo;
    }

    @Exclude
    public Map<String, String> toMap() {
        HashMap<String, String> result = new HashMap<>();
        result.put(NAME, name);
        result.put(EMAIL, email);
        result.put(PHOTO, photo);
        return result;
    }

}
