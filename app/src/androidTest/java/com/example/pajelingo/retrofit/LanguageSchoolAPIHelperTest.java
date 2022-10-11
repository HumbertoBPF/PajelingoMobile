package com.example.pajelingo.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LanguageSchoolAPIHelperTest extends LanguageSchoolAPIHelper{
    public static LanguageSchoolAPI getApiObject() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(LanguageSchoolAPITest.class);
    }
}
