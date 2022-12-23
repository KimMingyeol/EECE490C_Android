package com.example.eece490c_android;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eece490c_android.serializers.LogInSerializer;
import com.example.eece490c_android.serializers.SignUpSerializer;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LogInSignUpActivity extends AppCompatActivity {
    private Button logInButton;
    private Button signUpButton;
    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    public static ServerAPI serverAPI;
    public static String requestServerURL;

    private Intent intentToGallery;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);

        initialPermissionRequest();

        intentToGallery = new Intent(this, MainActivity.class);

        logInButton = findViewById(R.id.LogInButton);
        signUpButton = findViewById(R.id.SignUpButton);
        usernameInput = findViewById(R.id.UsernameInput);
        passwordInput = findViewById(R.id.PasswordInput);

        requestServerURL = "http://35.247.113.204:8000/";
        Retrofit retrofit = new Retrofit.Builder().baseUrl(requestServerURL).addConverterFactory(GsonConverterFactory.create()).build();
        serverAPI = retrofit.create(ServerAPI.class);

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logIn();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initialPermissionRequest() {
        int permissionCamera = checkSelfPermission(Manifest.permission.CAMERA);
        int permissionReadExternalStorage = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCamera != PackageManager.PERMISSION_GRANTED || permissionReadExternalStorage != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean permissionGrantedState = requestCode == 0 && permissions.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED;
        if (!permissionGrantedState) {
            new AlertDialog.Builder(this).setTitle("Virtual Gallery").setMessage("Permission required for running this application :(").setPositiveButton("QUIT", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            }).show();
        }
    }

    private void logIn() {
        String username = checkAndExtractUsernameInputField();
        String password = checkAndExtractPasswordInputField();

        LogInSerializer logInSerializer = new LogInSerializer(username, password);
        Call<LogInSerializer> logInCall = serverAPI.logInPost(logInSerializer);
        logInCall.enqueue(new Callback<LogInSerializer>() {
            @Override
            public void onResponse(Call<LogInSerializer> call, Response<LogInSerializer> response) {
                if (response.isSuccessful()) {
                    LogInSerializer logInResponse = response.body();
                    String username = logInResponse.getUsername();
                    String token = logInResponse.getToken();

                    intentToGallery.putExtra("username", username);
                    intentToGallery.putExtra("token", "Bearer " + token);

                    startActivity(intentToGallery);
                } else {
                    Toast.makeText(LogInSignUpActivity.this, "Log In Failed!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LogInSerializer> call, Throwable t) {

            }
        });
    }

    private void signUp() {
        String username = checkAndExtractUsernameInputField();
        String password = checkAndExtractPasswordInputField();

        SignUpSerializer signUpSerializer = new SignUpSerializer(username, password);
        Call<SignUpSerializer> signUpCall = serverAPI.signUpPost(signUpSerializer);
        signUpCall.enqueue(new Callback<SignUpSerializer>() {
            @Override
            public void onResponse(Call<SignUpSerializer> call, Response<SignUpSerializer> response) {
                if (response.isSuccessful()) {
                    usernameInput.setText("");
                    passwordInput.setText("");
                    Toast.makeText(LogInSignUpActivity.this, "Account Successfully Registered!", Toast.LENGTH_LONG).show();
                } else {
                    if (response.code() == 409) {
                        Toast.makeText(LogInSignUpActivity.this, "Account Already Exists!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LogInSignUpActivity.this, "Invalid Sign Up Form!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<SignUpSerializer> call, Throwable t) {

            }
        });
    }

    private String checkAndExtractUsernameInputField() {
        String usernameInputString = usernameInput.getText().toString();
        if (usernameInputString.length() > 0) {
            return usernameInputString;
        }
        return null;
    }

    private String checkAndExtractPasswordInputField() {
        String passwordInputString = passwordInput.getText().toString();
        if (passwordInputString.length() > 0) {
            return passwordInputString;
        }
        return null;
    }
}
