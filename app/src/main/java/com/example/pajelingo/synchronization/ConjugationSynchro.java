package com.example.pajelingo.synchronization;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Conjugation;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

public class ConjugationSynchro extends ResourcesSynchro<Conjugation> {
    public ConjugationSynchro(Context context, AlertDialog downloadDialog) {
        super("conjugation", AppDatabase.getInstance(context).getConjugationDao(),
                () -> LanguageSchoolAPIHelper.getApiObject().getConjugations(), new LanguageSynchro(context, downloadDialog), downloadDialog);
    }
}
