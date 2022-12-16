package com.example.eece490c_android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class UserPost {
    private int id;
    private String username;
    private String artist;
    private String photoURL;
    private String uploadDateFormatted;
    private String uploadTimeFormatted;
    private String caption;
    private File localPhotoFile;

    public UserPost(int id, String username, String artist , String photoURL, String uploadDateFormatted, String uploadTimeFormatted, String caption) {
        this.id = id;
        this.username = username;
        this.artist = artist;
        this.photoURL = photoURL;
        this.uploadDateFormatted = uploadDateFormatted;
        this.uploadTimeFormatted = uploadTimeFormatted;
        this.caption = caption;
        this.localPhotoFile = null;
    }

    public void removePhotoFileIfNotNull() {
        if (this.localPhotoFile != null) {
            localPhotoFile.delete();
        }
    }

    public void loadPhotoOnLocal(String postDirectory, String serverURL) {
        removePhotoFileIfNotNull();

        localPhotoFile = new File(postDirectory, String.valueOf(this.id));
        String serverHost = serverURL.substring(0, serverURL.length()-1);

        new Thread(() -> {
            try {
                URL photoServerURL = new URL(serverHost + this.photoURL);
                Log.d("IMAGE REQUEST", serverHost + this.photoURL);
                Bitmap photoBitmap = BitmapFactory.decodeStream(photoServerURL.openConnection().getInputStream());
                FileOutputStream fileOutputStream = new FileOutputStream(localPhotoFile);
                photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();

                MainActivity.numOfFetched++;
                Log.d("STATE", "FINISHED");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getArtist() {
        return artist;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public String getUploadDateFormatted() {
        return uploadDateFormatted;
    }

    public String getUploadTimeFormatted() {
        return uploadTimeFormatted;
    }

    public String getCaption() {
        return caption;
    }

    public File getLocalPhotoFile() {
        return localPhotoFile;
    }
}
