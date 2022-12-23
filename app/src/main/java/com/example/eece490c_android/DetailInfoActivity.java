package com.example.eece490c_android;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailInfoActivity extends AppCompatActivity {
    private TextView artistText;
    private TextView dateTimeText;
    private TextView captionText;
    private ImageView photoView;
    private Button deleteButton;

    private int postId;
    private String username;
    private String token;

    private Intent intentToGalleryOnDeletion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_info);

        Intent intentFromGallery = getIntent();
        String artist = intentFromGallery.getExtras().getString("Artist");
        String dateTime = intentFromGallery.getExtras().getString("DateTime");
        String caption = intentFromGallery.getExtras().getString("Caption");
        String photoViewFilePath = intentFromGallery.getExtras().getString("PhotoFilePath");
        postId = intentFromGallery.getExtras().getInt("postId");
        username = intentFromGallery.getExtras().getString("username");
        token = intentFromGallery.getExtras().getString("token");

        artistText = findViewById(R.id.Artist);
        dateTimeText = findViewById(R.id.DateTime);
        captionText = findViewById(R.id.Caption);
        photoView = findViewById(R.id.PhotoView);

        if (artist.length() > 0) {
            artistText.setText(artist);
        } else {
            artistText.setText(username);
        }
        dateTimeText.setText(dateTime);
        captionText.setText(caption);
        photoView.setImageBitmap(BitmapFactory.decodeFile(photoViewFilePath));

        intentToGalleryOnDeletion = new Intent(this, MainActivity.class);

        deleteButton = findViewById(R.id.DeleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<Integer> deletePostCall = LogInSignUpActivity.serverAPI.deletePost(token, postId);
                deletePostCall.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        intentToGalleryOnDeletion.putExtra("username", username);
                        intentToGalleryOnDeletion.putExtra("token", token);

                        startActivity(intentToGalleryOnDeletion);
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                        Toast.makeText(DetailInfoActivity.this, "Deletion Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
