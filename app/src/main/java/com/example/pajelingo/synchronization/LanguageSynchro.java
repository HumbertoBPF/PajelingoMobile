package com.example.pajelingo.synchronization;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

public class LanguageSynchro extends ResourcesSynchro<Language> {
    public LanguageSynchro(Context context, AlertDialog downloadDialog) {
        super("language", AppDatabase.getInstance(context).getLanguageDao(),
                () -> LanguageSchoolAPIHelper.getApiObject().getLanguages(), new MeaningSynchro(context, downloadDialog), downloadDialog);
    }
}
