package com.example.pajelingo.retrofit;

import com.example.pajelingo.models.Article;
import com.example.pajelingo.models.Category;
import com.example.pajelingo.models.Conjugation;
import com.example.pajelingo.models.Game;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Meaning;
import com.example.pajelingo.models.Score;
import com.example.pajelingo.models.User;
import com.example.pajelingo.models.Word;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LanguageSchoolAPI {

    @GET("games")
    Call<List<Game>> getGames();

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
    Call<List<Score>> getScores();

    @GET("score")
    Call<List<Score>> getScore(@Header("Authorization") String authString,
                                @Query("language") String languageName,
                                @Query("game") Long gameId);

    @POST("score/")
    Call<Score> createScore(@Header("Authorization") String authString, @Body Score score);

    @PUT("score/{score_id}")
    Call<Score> incrementScore(@Header("Authorization") String authString,
                                          @Path("score_id") Long scoreId);

    @GET("user/")
    Call<User> login(@Header("Authorization") String authString);

    @POST("user/")
    Call<User> signup(@Body User user);

    @DELETE("user/")
    Call<Void> deleteAccount(@Header("Authorization") String authString);
}
