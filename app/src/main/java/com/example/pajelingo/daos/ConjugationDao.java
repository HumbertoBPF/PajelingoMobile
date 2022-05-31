package com.example.pajelingo.daos;

import android.os.AsyncTask;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pajelingo.interfaces.OnResultListener;
import com.example.pajelingo.models.Conjugation;

import java.util.List;

@Dao
public abstract class ConjugationDao extends BaseDao<Conjugation>{
    public ConjugationDao() {
        super("Conjugation");
    }

    @Query("SELECT * FROM Conjugation WHERE wordId = :wordId")
    protected abstract List<Conjugation> getConjugationsFromVerb(long wordId);

    public AsyncTask<Void, Void, List<Conjugation>> getConjugationsFromVerbTask(long wordId,
                                                                                OnResultListener<List<Conjugation>> onResultListener){
        return new AsyncTask<Void, Void, List<Conjugation>>() {
            @Override
            protected List<Conjugation> doInBackground(Void... voids) {
                return getConjugationsFromVerb(wordId);
            }

            @Override
            protected void onPostExecute(List<Conjugation> conjugations) {
                super.onPostExecute(conjugations);
                onResultListener.onResult(conjugations);
            }
        };
    }
}
