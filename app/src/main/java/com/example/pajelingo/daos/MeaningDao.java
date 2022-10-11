package com.example.pajelingo.daos;

import android.os.AsyncTask;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pajelingo.interfaces.OnResultListener;
import com.example.pajelingo.models.Meaning;

import java.util.List;

@Dao
public abstract class MeaningDao extends BaseDao<Meaning>{
    public MeaningDao() {
        super("Meaning");
    }

    @Query("SELECT * FROM meaning WHERE idWord=:idWord")
    public abstract List<Meaning> getMeaningsOfWord(long idWord);

    public AsyncTask<Void, Void, List<Meaning>> getMeaningOfWordTask(long idWord, OnResultListener<List<Meaning>> onResultListener) {
        return new AsyncTask<Void, Void, List<Meaning>>() {
            @Override
            protected List<Meaning> doInBackground(Void... voids) {
                return getMeaningsOfWord(idWord);
            }

            @Override
            protected void onPostExecute(List<Meaning> meanings) {
                super.onPostExecute(meanings);
                onResultListener.onResult(meanings);
            }
        };
    }
}
