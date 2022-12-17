package com.example.eece490c_android.serializers;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class UploadPostSerializer {
    private String username;
    private String artist;
    private String photo;
    private int captured_year;
    private int captured_month;
    private int captured_day;
    private int captured_hour;
    private int captured_minute;
    private int captured_second;
    private String caption;

    public UploadPostSerializer(String username, String artist, Bitmap photo, int capturedYear, int capturedMonth, int capturedDay, int capturedHour, int capturedMinute, int capturedSecond, String caption) {
        this.username = username;
        this.artist = artist;
        this.captured_year = capturedYear;
        this.captured_month = capturedMonth;
        this.captured_day = capturedDay;
        this.captured_hour = capturedHour;
        this.captured_minute = capturedMinute;
        this.captured_second = capturedSecond;
        this.caption = caption;

        ByteArrayOutputStream photoByteStream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 50, photoByteStream);
        this.photo = Base64.encodeToString(photoByteStream.toByteArray(), Base64.NO_WRAP);
    }
}