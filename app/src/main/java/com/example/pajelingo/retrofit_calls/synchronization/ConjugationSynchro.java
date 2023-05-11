package com.example.pajelingo.retrofit_calls.synchronization;

import android.content.Context;

import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Conjugation;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

public class ConjugationSynchro extends ResourcesSynchro<Conjugation> {
    public ConjugationSynchro(Context context) {
        super(AppDatabase.getInstance(context).getConjugationDao(),
                () -> LanguageSchoolAPIHelper.getApiObject().getConjugations(), new LanguageSynchro(context));
    }
}
