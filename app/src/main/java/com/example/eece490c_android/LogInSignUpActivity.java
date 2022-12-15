package com.example.eece490c_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eece490c_android.serializers.LogInSerializer;
import com.example.eece490c_android.serializers.SignUpSerializer;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);

        intentToGallery = new Intent(this, MainActivity.class);

        logInButton = findViewById(R.id.LogInButton);
        signUpButton = findViewById(R.id.SignUpButton);
        usernameInput = findViewById(R.id.UsernameInput);
        passwordInput = findViewById(R.id.PasswordInput);

        requestServerURL = "https://3d76-2001-2d8-642d-b3f2-54a-e5ff-713d-a249.ngrok.io/";
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
                    intentToGallery.putExtra("token", token);

                    startActivity(intentToGallery);
                } else {

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
                    Toast.makeText(LogInSignUpActivity.this, "Your account has been successfully registered!", Toast.LENGTH_LONG);
                } else {
                    Toast.makeText(LogInSignUpActivity.this, "Sign Up Error!", Toast.LENGTH_SHORT);
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
