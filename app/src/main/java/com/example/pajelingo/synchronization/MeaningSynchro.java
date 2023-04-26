package com.example.pajelingo.synchronization;

import android.content.Context;

import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Meaning;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

public class MeaningSynchro extends ResourcesSynchro<Meaning> {
    public MeaningSynchro(Context context) {
        super(AppDatabase.getInstance(context).getMeaningDao(),
                () -> LanguageSchoolAPIHelper.getApiObject().getMeanings(), new WordSynchro(context));
    }
}
