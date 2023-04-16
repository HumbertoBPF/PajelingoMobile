package com.example.pajelingo.daos;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pajelingo.interfaces.OnResultListener;
import com.example.pajelingo.models.Meaning;
import com.example.pajelingo.utils.BackgroundTask;

import java.util.List;

@Dao
public abstract class MeaningDao extends BaseDao<Meaning>{
    public MeaningDao() {
        super("Meaning");
    }

    @Query("SELECT * FROM meaning WHERE idWord=:idWord")
    public abstract List<Meaning> getMeaningsOfWord(long idWord);

    public void getMeaningOfWord(long idWord, OnResultListener<List<Meaning>> onResultListener) {
        BackgroundTask<List<Meaning>> backgroundTask = new BackgroundTask<>(() -> getMeaningsOfWord(idWord), onResultListener);
        backgroundTask.execute();
    }
}
