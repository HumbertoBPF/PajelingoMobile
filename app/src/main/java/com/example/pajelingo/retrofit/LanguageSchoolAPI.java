package com.example.pajelingo.retrofit;

import com.example.pajelingo.models.Article;
import com.example.pajelingo.models.Category;
import com.example.pajelingo.models.Conjugation;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Meaning;
import com.example.pajelingo.models.Score;
import com.example.pajelingo.models.Word;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface LanguageSchoolAPI {

    @GET("articles")
    Call<List<Article>> getArticles();

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

    @GET("scores")
    Call<List<Score>> getScores(@Header("Authorization") String authString);

}
