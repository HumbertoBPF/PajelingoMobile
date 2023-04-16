package com.example.pajelingo.daos;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pajelingo.interfaces.OnResultListener;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.utils.BackgroundTask;

@Dao
public abstract class LanguageDao extends BaseDao<Language>{
    public LanguageDao() {
        super("Language");
    }

    @Query("SELECT * FROM Language WHERE languageName = :languageName")
    public abstract Language getLanguageByName(String languageName);

    public void getLanguageByName(String languageName, OnResultListener<Language> onResultListener){
        BackgroundTask<Language> backgroundTask = new BackgroundTask<>(() -> getLanguageByName(languageName), onResultListener);
        backgroundTask.execute();
    }

}
