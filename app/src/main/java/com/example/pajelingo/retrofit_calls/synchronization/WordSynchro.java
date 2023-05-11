package com.example.pajelingo.retrofit_calls.synchronization;

import android.content.Context;

import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Word;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

public class WordSynchro extends ResourcesSynchro<Word> {
    public WordSynchro(Context context) {
        super(AppDatabase.getInstance(context).getWordDao(),
                () -> LanguageSchoolAPIHelper.getApiObject().getWords(), new GameSynchro(context));
    }
}
