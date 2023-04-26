package com.example.pajelingo.synchronization;

import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Score;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

public class ScoresSynchro extends ResourcesSynchro<Score> {

    public ScoresSynchro(Context context, NotificationCompat.Builder builder) {
        super(context, builder, 88, AppDatabase.getInstance(context).getScoreDao(),
                () -> LanguageSchoolAPIHelper.getApiObject().getScores(), null);
    }

}
