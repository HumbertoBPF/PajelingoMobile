package com.example.pajelingo.synchronization;

import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.interfaces.OnTaskListener;
import com.example.pajelingo.models.Meaning;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

public class MeaningSynchro extends ResourcesSynchro<Meaning> {
    public MeaningSynchro(Context context, NotificationCompat.Builder builder, OnTaskListener onTaskListener) {
        super(context, builder, 50, AppDatabase.getInstance(context).getMeaningDao(),
                () -> LanguageSchoolAPIHelper.getApiObject().getMeanings(),
                new WordSynchro(context, builder, onTaskListener), onTaskListener);
    }
}
