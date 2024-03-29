package com.example.pajelingo.retrofit;

import com.example.pajelingo.models.Article;
import com.example.pajelingo.models.ArticleGameAnswer;
import com.example.pajelingo.models.Category;
import com.example.pajelingo.models.Conjugation;
import com.example.pajelingo.models.ConjugationGameAnswer;
import com.example.pajelingo.models.FavoriteWordPayload;
import com.example.pajelingo.models.Game;
import com.example.pajelingo.models.GameAnswerFeedback;
import com.example.pajelingo.models.GameRoundWord;
import com.example.pajelingo.models.Image;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Meaning;
import com.example.pajelingo.models.Page;
import com.example.pajelingo.models.ResetEmail;
import com.example.pajelingo.models.Score;
import com.example.pajelingo.models.Token;
import com.example.pajelingo.models.User;
import com.example.pajelingo.models.VocabularyGameAnswer;
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

    @GET("words")
    Call<List<Word>> getWords(@Header("Authorization") String authString);

    @PUT("words/{pk}/favorite-word")
    Call<Word> favoriteWord(@Header("Authorization") String authString,
                            @Body FavoriteWordPayload isFavorite,
                            @Path("pk") long customerId);

    @GET("scores")
    Call<List<Score>> getScores();

    @GET("public-images/")
    Call<Image> getPublicImage(@Query("url") String url);

    @POST("user-token")
    Call<Token> getToken(@Body User user);

    @GET("user/")
    Call<User> login(@Header("Authorization") String authString);

    @POST("user/")
    Call<User> signup(@Body User user);

    @PUT("user/")
    Call<User> updateAccount(@Header("Authorization") String authString, @Body User user);

    @DELETE("user/")
    Call<Void> deleteAccount(@Header("Authorization") String authString);

    @GET("article-game")
    Call<GameRoundWord> getWordForArticleGame(@Header("Authorization") String authString,
                                              @Query("language") String language);

    @GET("conjugation-game")
    Call<GameRoundWord> getWordForConjugationGame(@Header("Authorization") String authString,
                                                  @Query("language") String language);

    @GET("vocabulary-game")
    Call<GameRoundWord> getWordForVocabularyGame(@Header("Authorization") String authString,
                                                 @Query("base_language") String baseLanguage,
                                                 @Query("target_language") String targetLanguage);

    @POST("article-game")
    Call<GameAnswerFeedback> submitArticleGameAnswer(@Header("Authorization") String authString,
                                                     @Body ArticleGameAnswer articleGameAnswer);

    @POST("vocabulary-game")
    Call<GameAnswerFeedback> submitVocabularyGameAnswer(@Header("Authorization") String authString,
                                                        @Body VocabularyGameAnswer vocabularyGameAnswer);

    @POST("conjugation-game")
    Call<GameAnswerFeedback> submitConjugationGameAnswer(@Header("Authorization") String authString,
                                                         @Body ConjugationGameAnswer conjugationGameAnswer);

    @GET("score")
    Call<List<Score>> getScore(@Header("Authorization") String authString,
                               @Query("language") String languageName,
                               @Query("game") Long gameId);

    @POST("request-reset-account/")
    Call<Void> resetAccount(@Body ResetEmail resetEmail);

    @GET("accounts")
    Call<Page<User>> getAccounts(@Query("q") String q, @Query("page") int page);

    @GET("accounts/{username}")
    Call<User> getAccount(@Path("username") String username);
}
