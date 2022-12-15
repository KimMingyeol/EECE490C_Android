package com.example.eece490c_android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetailInfoActivity extends AppCompatActivity {
    private TextView uploaderText;
    private TextView dateTimeText;
    private TextView captionText;
    private ImageView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_info);

        String uploader;
        String dateTime;
        String caption;

        Intent intentFromGallery = getIntent();

        uploaderText = findViewById(R.id.Uploader);
        dateTimeText = findViewById(R.id.DateTime);
        captionText = findViewById(R.id.Caption);
        photoView = findViewById(R.id.PhotoView);
    }
}
