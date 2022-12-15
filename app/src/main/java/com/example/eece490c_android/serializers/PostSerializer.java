package com.example.eece490c_android.serializers;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class PostSerializer {
    private int id;
    private ArrayList<UserSerializer> heart_users;
    private String photo;
    private int captured_year;
    private int captured_month;
    private int captured_day;
    private int captured_hour;
    private int captured_minute;
    private String caption;

    public int getId() {
        return id;
    }

    public String getDate() {
        return String.format("%d-%d-%d", captured_year, captured_month, captured_day);
    }

    public String getTime() {
        return String.format("%d:%d", captured_hour, captured_minute);
    }

    public String getCaption() {
        return caption;
    }

    public ArrayList<String> getNamesOfHeartUsers() {
        ArrayList<String> namesOfHeartUsers = new ArrayList<String>();
        for (UserSerializer userSerializer : heart_users) {
            namesOfHeartUsers.add(userSerializer.getUsername());
        }
        return namesOfHeartUsers;
    }

    public String getPhoto() {
        return photo;
    }
}
