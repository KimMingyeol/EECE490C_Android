package com.example.eece490c_android;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class DetailInfoActivity extends AppCompatActivity {
    private TextView uploaderText;
    private TextView dateTimeText;
    private TextView captionText;
    private ImageView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_info);

        Intent intentFromGallery = getIntent();
        String uploader = intentFromGallery.getExtras().getString("Uploader");
        String artist = intentFromGallery.getExtras().getString("Artist");
        String dateTime = intentFromGallery.getExtras().getString("DateTime");
        String caption = intentFromGallery.getExtras().getString("Caption");
        String photoViewFilePath = intentFromGallery.getExtras().getString("PhotoFilePath");

        uploaderText = findViewById(R.id.Uploader);
        dateTimeText = findViewById(R.id.DateTime);
        captionText = findViewById(R.id.Caption);
        photoView = findViewById(R.id.PhotoView);

        uploaderText.setText(uploader);
        dateTimeText.setText(dateTime);
        captionText.setText(caption);
        photoView.setImageBitmap(BitmapFactory.decodeFile(photoViewFilePath));
    }
}
