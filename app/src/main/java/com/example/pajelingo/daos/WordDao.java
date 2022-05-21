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

    @Query("SELECT * FROM Word WHERE idLanguage = :idLanguage")
    protected abstract List<Word> findWordsByLanguage(long idLanguage);

    @Query("SELECT * FROM Word WHERE id = :id")
    public abstract Word findWordsById(long id);

    public AsyncTask<Void, Void, List<Word>> getWordsByLanguageAsyncTask(long idLanguage,
                                                                         OnResultListener<List<Word>> onResultListener){
        return new AsyncTask<Void, Void, List<Word>>() {
            @Override
            protected List<Word> doInBackground(Void... voids) {
                return findWordsByLanguage(idLanguage);
            }

            @Override
            protected void onPostExecute(List<Word> words) {
                super.onPostExecute(words);
                onResultListener.onResult(words);
            }
        };
    }

    public AsyncTask<Void, Void, Word> getWordsByIdTask(long id, OnResultListener<Word> onResultListener){
        return new AsyncTask<Void, Void, Word>() {
            @Override
            protected Word doInBackground(Void... voids) {
                return findWordsById(id);
            }

            @Override
            protected void onPostExecute(Word word) {
                super.onPostExecute(word);
                onResultListener.onResult(word);
            }
        };
    }

}
