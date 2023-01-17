package com.example.pajelingo.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LanguageSchoolAPIHelper {

    protected static final String BASE_URL = "http://192.168.15.3:8000/api/";

    public static LanguageSchoolAPI getApiObject() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(LanguageSchoolAPI.class);
    }
}