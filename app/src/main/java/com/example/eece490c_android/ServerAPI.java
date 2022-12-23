package com.example.eece490c_android;

import com.example.eece490c_android.serializers.FetchPostsSerializer;
import com.example.eece490c_android.serializers.LogInSerializer;
import com.example.eece490c_android.serializers.SignUpSerializer;
import com.example.eece490c_android.serializers.UploadPostSerializer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Header;
import retrofit2.http.Body;
import retrofit2.http.Query;

public interface ServerAPI {
    @POST("photo_server/login/")
    Call<LogInSerializer> logInPost(@Body LogInSerializer logInSerializer);

    @POST("photo_server/signup/")
    Call<SignUpSerializer> signUpPost(@Body SignUpSerializer signUpSerializer);

    @POST("photo_server/upload/")
    Call<UploadPostSerializer> uploadPost(@Header("Authorization") String token, @Body UploadPostSerializer uploadPostSerializer);

    @POST("photo_server/delete/")
    Call<Integer> deletePost(@Header("Authorization") String token, @Body Integer postId);

    @GET("photo_server/fetch/")
    Call<FetchPostsSerializer> fetchGet(@Header("Authorization") String token, @Query("username") String username, @Query("from_which") String from_which);
}
