package com.example.pajelingo.retrofit;

import com.example.pajelingo.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface LanguageSchoolAPITest extends LanguageSchoolAPI{
    @POST("user/")
    Call<User> signup(@Body User user);

    @DELETE("user/")
    Call<Void> deleteAccount(@Header("Authorization") String authString);
}
