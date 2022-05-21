package com.example.pajelingo.retrofit;

import com.example.pajelingo.models.Category;
import com.example.pajelingo.models.Conjugation;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Meaning;
import com.example.pajelingo.models.Word;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface LanguageSchoolAPI {

    @GET("categories")
    Call<List<Category>> getCategories();

    @GET("conjugations")
    Call<List<Conjugation>> getConjugations();

    @GET("languages")
    Call<List<Language>> getLanguages();

    @GET("meanings")
    Call<List<Meaning>> getMeanings();

    @GET("words")
    Call<List<Word>> getWords();

}
