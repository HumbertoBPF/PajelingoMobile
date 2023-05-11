package com.example.pajelingo.retrofit_calls.synchronization;

import android.content.Context;

import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Score;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

public class ScoresSynchro extends ResourcesSynchro<Score> {

    public ScoresSynchro(Context context) {
        super(AppDatabase.getInstance(context).getScoreDao(),
                () -> LanguageSchoolAPIHelper.getApiObject().getScores(), null);
    }

}
