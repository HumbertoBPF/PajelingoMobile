package com.example.pajelingo.synchronization;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Word;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

public class WordSynchro extends ResourcesSynchro<Word> {
    public WordSynchro(Context context, AlertDialog downloadDialog) {
        super("word", AppDatabase.getInstance(context).getWordDao(),
                () -> LanguageSchoolAPIHelper.getApiObject().getWords(), new GameSynchro(context, downloadDialog), downloadDialog);
    }
}
