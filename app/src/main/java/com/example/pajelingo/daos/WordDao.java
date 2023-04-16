package com.example.pajelingo.daos;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pajelingo.interfaces.OnResultListener;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Word;
import com.example.pajelingo.utils.BackgroundTask;

import java.util.ArrayList;
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

    public void getWordsByLanguage(String language, OnResultListener<List<Word>> onResultListener){
        BackgroundTask<List<Word>> backgroundTask = new BackgroundTask<>(() -> getWordsByLanguage(language), onResultListener);
        backgroundTask.execute();
    }

    public void getNounsByLanguage(String language, OnResultListener<List<Word>> onResultListener){
        BackgroundTask<List<Word>> backgroundTask = new BackgroundTask<>(() -> getNounsByLanguage(language), onResultListener);
        backgroundTask.execute();
    }

    public void getWordsByCategoryAndByLanguage(String category, String language, OnResultListener<List<Word>> onResultListener){
        BackgroundTask<List<Word>> backgroundTask = new BackgroundTask<>(() -> getWordsByCategoryAndByLanguage(category, language), onResultListener);
        backgroundTask.execute();
    }

    public void searchWords(String pattern, OnResultListener<List<Word>> onResultListener){
        BackgroundTask<List<Word>> backgroundTask = new BackgroundTask<>(() -> searchWords(pattern), onResultListener);
        backgroundTask.execute();
    }

    public void searchWords(String pattern, String language, OnResultListener<List<Word>> onResultListener){
        BackgroundTask<List<Word>> backgroundTask = new BackgroundTask<>(() -> searchWords(pattern, language), onResultListener);
        backgroundTask.execute();
    }

    public void searchFavoriteWords(String pattern, OnResultListener<List<Word>> onResultListener){
        BackgroundTask<List<Word>> backgroundTask = new BackgroundTask<>(() -> searchFavoriteWords(pattern), onResultListener);
        backgroundTask.execute();
    }

    public void searchFavoriteWords(String pattern, String language, OnResultListener<List<Word>> onResultListener){
        BackgroundTask<List<Word>> backgroundTask = new BackgroundTask<>(() -> searchFavoriteWords(pattern, language), onResultListener);
        backgroundTask.execute();
    }

    public void getSynonyms(Word word, Language language, OnResultListener<List<String>> onResultListener) {
        BackgroundTask<List<String>> backgroundTask = new BackgroundTask<>(() -> {
            List<String> synonyms = new ArrayList<>();

            for (Long synonymId : word.getIdsSynonyms()){
                Word synonym = getRecordById(synonymId);
                // Return only the synonyms whose language matches with the specified one
                if (synonym.getLanguage().equals(language.getLanguageName())){
                    synonyms.add(synonym.getWordName());
                }
            }

            return synonyms;
        }, onResultListener);
        backgroundTask.execute();
    }
}
