package com.example.eece490c_android;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.example.eece490c_android.serializers.FetchPostsSerializer;
import com.example.eece490c_android.serializers.PostSerializer;
import com.example.eece490c_android.serializers.UploadPostSerializer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private GridView galleryView;
    private Button fromGalleryButton;
    private Button fromCameraButton;

    private String username;
    private String token;

    private File postDirectory;
    private String tmpCaptureName;

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher;
    private SwipeRefreshLayout galleryRefreshLayout;

    public static ArrayList<UserPost> userPosts;
    public static int numOfFetched;
    private int numToFetch;
    private boolean isFetching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intentFromLogIn = getIntent();
        username = intentFromLogIn.getExtras().getString("username");
        token = "Bearer " + intentFromLogIn.getExtras().getString("token");

        userPosts = new ArrayList<UserPost>();

        galleryView = findViewById(R.id.GalleryView);
        galleryView.setAdapter(new GalleryAdapter(this));
        galleryRefreshLayout = findViewById(R.id.GallerySwipeLayout);

        galleryView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intentToDetailInfo = new Intent(view.getContext(), DetailInfoActivity.class);
                intentToDetailInfo.putExtra("ID", i);
                startActivity(intentToDetailInfo);
            }
        });

        postDirectory = new File(getFilesDir().getAbsolutePath().concat("/posts/"));
        if (!postDirectory.exists()) {
            postDirectory.mkdirs();
        }
        tmpCaptureName = "tmpCapture";

        fromGalleryButton = findViewById(R.id.FromGalleryButton);
        fromCameraButton = findViewById(R.id.FromCameraButton);

        fromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        fromCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File tmpCaptureFile = new File(postDirectory, tmpCaptureName);
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(MainActivity.this, "com.example.eece490c_android.fileprovider", tmpCaptureFile));
                if (captureIntent.resolveActivity(getPackageManager()) != null) {
                    cameraActivityResultLauncher.launch(captureIntent);
                }
            }
        });

        cameraActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    File tmpCapturedFile = new File(postDirectory, tmpCaptureName);
                    if (!tmpCapturedFile.exists()) {
                        return;
                    }
                    Bitmap capturedBitmap = BitmapFactory.decodeFile(tmpCapturedFile.getAbsolutePath());

                    ExifInterface exifInterfaceToRotate = null;
                    try {
                        exifInterfaceToRotate = new ExifInterface(tmpCapturedFile.getAbsoluteFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    int exifInterfaceOrientation = exifInterfaceToRotate.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int exifInterfaceOrientationDegree = 0;
                    if (exifInterfaceOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
                        exifInterfaceOrientationDegree = 90;
                    } else if (exifInterfaceOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
                        exifInterfaceOrientationDegree = 180;
                    } else if (exifInterfaceOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
                        exifInterfaceOrientationDegree = 270;
                    }

                    if (exifInterfaceOrientationDegree != 0) {
                        Matrix bitmapRotationMatrix = new Matrix();
                        bitmapRotationMatrix.setRotate(exifInterfaceOrientationDegree, (float) capturedBitmap.getWidth()/2, (float) capturedBitmap.getHeight()/2);
                        Bitmap capturedBitmapOrigin = Bitmap.createBitmap(capturedBitmap, 0, 0, capturedBitmap.getWidth(), capturedBitmap.getHeight(), bitmapRotationMatrix, true);

                        capturedBitmap.recycle();
                        capturedBitmap = capturedBitmapOrigin;
                    }

                    tmpCapturedFile.delete();

                    // capturedBitmap
                    Calendar currentCalendar = Calendar.getInstance();
                    int capturedYear = currentCalendar.get(Calendar.YEAR);
                    int capturedMonth = currentCalendar.get(Calendar.MONTH) + 1;
                    int capturedDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
                    int capturedHour = currentCalendar.get(Calendar.HOUR_OF_DAY);
                    int capturedMinute = currentCalendar.get(Calendar.MINUTE);
                    String caption = "";

                    UploadPostSerializer uploadPostSerializer = new UploadPostSerializer(username, capturedBitmap, capturedYear, capturedMonth, capturedDay, capturedHour, capturedMinute, caption);
                    Call<UploadPostSerializer> uploadPostCall = LogInSignUpActivity.serverAPI.uploadPost(token, uploadPostSerializer); // using the same API as the previous activity
                    uploadPostCall.enqueue(new Callback<UploadPostSerializer>() {
                        @Override
                        public void onResponse(Call<UploadPostSerializer> call, Response<UploadPostSerializer> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Uploaded Successfully!", Toast.LENGTH_LONG);
                            } else {
                                Toast.makeText(MainActivity.this, "Failed to Upload!", Toast.LENGTH_SHORT);
                            }
                        }

                        @Override
                        public void onFailure(Call<UploadPostSerializer> call, Throwable t) {

                        }
                    });
                }
            }
        });

        galleryRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isFetching) {
                    Log.d("GALLERY REFRESH", "STILL FETCHING");
                    return;
                }
                galleryReload();
            }
        });

        galleryRefreshLayout.setRefreshing(true);
        galleryReload();
    }

    public boolean fetchPostsFromServer() {
        if (username == null || token == null) {
            return false;
        }
        Call<FetchPostsSerializer> fetchGetCall = LogInSignUpActivity.serverAPI.fetchGet(token, username);
        fetchGetCall.enqueue(new Callback<FetchPostsSerializer>() {
            @Override
            public void onResponse(Call<FetchPostsSerializer> call, Response<FetchPostsSerializer> response) {
                if (response.isSuccessful()) {
                    for (UserPost prevPost : userPosts) {
                        prevPost.removePhotoFileIfNotNull();
                    }
                    userPosts.clear();

                    FetchPostsSerializer fetchGetResponse = response.body();
                    ArrayList<PostSerializer> posts = fetchGetResponse.getPosts();
                    for (PostSerializer post : posts) {
                        int id = post.getId();
                        String photoURL = post.getPhoto();
                        String uploadDataFormatted = post.getDate();
                        String uploadTimeFormatted = post.getTime();
                        String caption = post.getCaption();
                        ArrayList<String> namesOfHeartUsers = post.getNamesOfHeartUsers();

                        UserPost userPost = new UserPost(username, id, photoURL, uploadDataFormatted, uploadTimeFormatted, namesOfHeartUsers);
                        userPosts.add(userPost);

                        Log.d("POST ID", String.valueOf(id));
                        Log.d("PHOTO URL TO FETCH", photoURL);
                        Log.d("POST DATETIME", uploadDataFormatted + " " + uploadTimeFormatted);
                        Log.d("POST CAPTION", caption);
                        for (String otherUsername : namesOfHeartUsers) {
                            Log.d("NAME OF HEART USER", otherUsername);
                        }

                        numToFetch++;
                    }

                    numOfFetched = 0;
                    for (UserPost currPost : userPosts) {
                        currPost.loadPhotoOnLocal(postDirectory.getAbsolutePath(), LogInSignUpActivity.requestServerURL);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Failed to fetch posts from server!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FetchPostsSerializer> call, Throwable t) {

            }
        });

        return true;
    }

    public void galleryReload() {
        if (galleryView != null) {
            numOfFetched = -1;
            numToFetch = 0;
            isFetching = true;
            fetchPostsFromServer();

            new Thread(() -> {
                Log.d("BEFORE WHILE LOOP", "START WAITING");
                while(numOfFetched < numToFetch) {
                    Log.d("WHILE LOOP", "WAITING TO FETCH " + String.valueOf(numToFetch));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("RELOAD GALLERY", "START");
                        galleryView.invalidateViews();
                        galleryView.setAdapter(new GalleryAdapter(galleryView.getContext()));

                        Log.d("RELOAD SUCCESS", "OK");
                        galleryRefreshLayout.setRefreshing(false);
                        isFetching = false;
                    }
                });
            }).start();
        }
    }
}