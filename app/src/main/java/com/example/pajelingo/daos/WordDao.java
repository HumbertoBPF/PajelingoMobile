package com.example.pajelingo.daos;

import android.os.AsyncTask;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pajelingo.interfaces.OnResultListener;
import com.example.pajelingo.models.Word;

import java.util.List;

@Dao
public abstract class WordDao extends BaseDao<Word>{
    public WordDao() {
        super("Word");
    }

    @Query("SELECT * FROM Word WHERE language = :language;")
    public abstract List<Word> getWordsByLanguage(String language);

    @Query("SELECT * FROM Word WHERE language = :language AND idArticle IS NOT NULL;")
    public abstract List<Word> getNounsByLanguage(String language);

    @Query("SELECT * FROM Word WHERE category = :category AND language = :language;")
    public abstract List<Word> getWordsByCategoryAndByLanguage(String category, String language);

    @Query("SELECT * FROM Word WHERE wordName LIKE :pattern ORDER BY LOWER(wordName)")
    public abstract List<Word> searchWords(String pattern);

    @Query("SELECT * FROM Word WHERE (wordName LIKE :pattern) AND (language = :language) ORDER BY LOWER(wordName)")
    public abstract List<Word> searchWords(String pattern, String language);

    @Query("SELECT * FROM Word WHERE (wordName LIKE :pattern) AND (isFavorite) ORDER BY LOWER(wordName)")
    public abstract List<Word> searchFavoriteWords(String pattern);

    @Query("SELECT * FROM Word WHERE (wordName LIKE :pattern) AND (language = :language) AND (isFavorite) ORDER BY LOWER(wordName)")
    public abstract List<Word> searchFavoriteWords(String pattern, String language);

    public AsyncTask<Void, Void, List<Word>> getWordsByLanguageAsyncTask(String language,
                                                                         OnResultListener<List<Word>> onResultListener){
        return new AsyncTask<Void, Void, List<Word>>() {
            @Override
            protected List<Word> doInBackground(Void... voids) {
                return getWordsByLanguage(language);
            }

            @Override
            protected void onPostExecute(List<Word> words) {
                super.onPostExecute(words);
                onResultListener.onResult(words);
            }
        };
    }

    public AsyncTask<Void, Void, List<Word>> getNounsByLanguageAsyncTask(String language,
                                                                         OnResultListener<List<Word>> onResultListener){
        return new AsyncTask<Void, Void, List<Word>>() {
            @Override
            protected List<Word> doInBackground(Void... voids) {
                return getNounsByLanguage(language);
            }

            @Override
            protected void onPostExecute(List<Word> words) {
                super.onPostExecute(words);
                onResultListener.onResult(words);
            }
        };
    }

    public AsyncTask<Void, Void, List<Word>> getWordsByCategoryAndByLanguageTask(String category, String language,
                                                                                 OnResultListener<List<Word>> onResultListener){
        return new AsyncTask<Void, Void, List<Word>>() {
            @Override
            protected List<Word> doInBackground(Void... voids) {
                return getWordsByCategoryAndByLanguage(category, language);
            }

            @Override
            protected void onPostExecute(List<Word> words) {
                super.onPostExecute(words);
                onResultListener.onResult(words);
            }
        };
    }

    public AsyncTask<Void, Void, List<Word>> searchWordsTask(String pattern, OnResultListener<List<Word>> onResultListener){
        return new AsyncTask<Void, Void, List<Word>>() {
            @Override
            protected List<Word> doInBackground(Void... voids) {
                return searchWords(pattern);
            }

            @Override
            protected void onPostExecute(List<Word> words) {
                super.onPostExecute(words);
                onResultListener.onResult(words);
            }
        };
    }

    public AsyncTask<Void, Void, List<Word>> searchWordsTask(String pattern, String language, OnResultListener<List<Word>> onResultListener){
        return new AsyncTask<Void, Void, List<Word>>() {
            @Override
            protected List<Word> doInBackground(Void... voids) {
                return searchWords(pattern, language);
            }

            @Override
            protected void onPostExecute(List<Word> words) {
                super.onPostExecute(words);
                onResultListener.onResult(words);
            }
        };
    }

    public AsyncTask<Void, Void, List<Word>> searchFavoriteWordsTask(String pattern, OnResultListener<List<Word>> onResultListener){
        return new AsyncTask<Void, Void, List<Word>>() {
            @Override
            protected List<Word> doInBackground(Void... voids) {
                return searchFavoriteWords(pattern);
            }

            @Override
            protected void onPostExecute(List<Word> words) {
                super.onPostExecute(words);
                onResultListener.onResult(words);
            }
        };
    }

    public AsyncTask<Void, Void, List<Word>> searchFavoriteWordsTask(String pattern, String language, OnResultListener<List<Word>> onResultListener){
        return new AsyncTask<Void, Void, List<Word>>() {
            @Override
            protected List<Word> doInBackground(Void... voids) {
                return searchFavoriteWords(pattern, language);
            }

            @Override
            protected void onPostExecute(List<Word> words) {
                super.onPostExecute(words);
                onResultListener.onResult(words);
            }
        };
    }

}
