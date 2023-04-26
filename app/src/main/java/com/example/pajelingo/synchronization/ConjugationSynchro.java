package com.example.pajelingo.synchronization;

import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Conjugation;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

public class ConjugationSynchro extends ResourcesSynchro<Conjugation> {
    public ConjugationSynchro(Context context, NotificationCompat.Builder builder) {
        super(context, builder, 25, AppDatabase.getInstance(context).getConjugationDao(),
                () -> LanguageSchoolAPIHelper.getApiObject().getConjugations(), new LanguageSynchro(context, builder));
    }
}
