package com.example.pajelingo.synchronization;

import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.interfaces.OnTaskListener;
import com.example.pajelingo.models.Word;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

public class WordSynchro extends ResourcesSynchro<Word> {
    public WordSynchro(Context context, NotificationCompat.Builder builder, OnTaskListener onTaskListener) {
        super(context, builder, 63, AppDatabase.getInstance(context).getWordDao(),
                () -> LanguageSchoolAPIHelper.getApiObject().getWords(),
                new GameSynchro(context, builder, onTaskListener), onTaskListener);
    }
}
