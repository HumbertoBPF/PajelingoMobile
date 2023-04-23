package com.example.pajelingo.daos;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pajelingo.interfaces.OnResultListener;
import com.example.pajelingo.models.Conjugation;
import com.example.pajelingo.utils.BackgroundTask;

import java.util.List;

@Dao
public abstract class ConjugationDao extends BaseDao<Conjugation>{
    public ConjugationDao() {
        super("Conjugation");
    }

    @Query("SELECT * FROM Conjugation WHERE wordId = :wordId")
    protected abstract List<Conjugation> getConjugations(long wordId);

    @Query("SELECT * FROM Conjugation WHERE wordId = :wordId AND tense = :tense")
    protected abstract Conjugation getConjugation(long wordId, String tense);

    public void getConjugations(long wordId, OnResultListener<List<Conjugation>> onResultListener){
        BackgroundTask<List<Conjugation>> backgroundTask = new BackgroundTask<>(() -> getConjugations(wordId), onResultListener);
        backgroundTask.execute();
    }

    public void getConjugation(long wordId, String tense, OnResultListener<Conjugation> onResultListener) {
        BackgroundTask<Conjugation> backgroundTask = new BackgroundTask<>(() -> getConjugation(wordId, tense), onResultListener);
        backgroundTask.execute();
    }
}
