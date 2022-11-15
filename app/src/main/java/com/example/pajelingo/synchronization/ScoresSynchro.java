package com.example.pajelingo.synchronization;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Score;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

public class ScoresSynchro extends ResourcesSynchro<Score> {

    public ScoresSynchro(Context context, AlertDialog downloadDialog) {
        super("meaning", AppDatabase.getInstance(context).getScoreDao(),
                () -> LanguageSchoolAPIHelper.getApiObject().getScores(), null, downloadDialog);
    }

}
