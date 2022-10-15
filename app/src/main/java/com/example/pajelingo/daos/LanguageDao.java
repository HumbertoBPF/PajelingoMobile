package com.example.pajelingo.daos;

import android.os.AsyncTask;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pajelingo.interfaces.OnResultListener;
import com.example.pajelingo.models.Language;

@Dao
public abstract class LanguageDao extends BaseDao<Language>{
    public LanguageDao() {
        super("Language");
    }

    @Query("SELECT * FROM Language WHERE languageName = :languageName")
    public abstract Language getLanguageByName(String languageName);

    public AsyncTask<Void, Void, Language> getLanguageByNameAsyncTask(String languageName,
                                                                      OnResultListener<Language> onResultListener){
        return new AsyncTask<Void, Void, Language>() {
            @Override
            protected Language doInBackground(Void... voids) {
                return getLanguageByName(languageName);
            }

            @Override
            protected void onPostExecute(Language language) {
                super.onPostExecute(language);
                onResultListener.onResult(language);
            }
        };
    }

}
