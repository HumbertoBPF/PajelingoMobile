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
    protected abstract List<Conjugation> getConjugationsFromVerb(long wordId);

    public void getConjugationsFromVerb(long wordId, OnResultListener<List<Conjugation>> onResultListener){
        BackgroundTask<List<Conjugation>> backgroundTask = new BackgroundTask<>(() -> getConjugationsFromVerb(wordId), onResultListener);
        backgroundTask.execute();
    }
}
