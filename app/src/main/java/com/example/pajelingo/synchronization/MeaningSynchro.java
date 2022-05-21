package com.example.pajelingo.synchronization;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Meaning;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import java.util.List;

import retrofit2.Call;

public class MeaningSynchro extends ResourcesSynchro<Meaning> {
    public MeaningSynchro(Context context, AlertDialog downloadDialog) {
        super("meaning", AppDatabase.getInstance(context).getMeaningDao(), new ResourcesInterface<Meaning>() {
            @Override
            public Call<List<Meaning>> getCallForResources() {
                return LanguageSchoolAPIHelper.getApiObject().getMeanings();
            }
        }, new WordSynchro(context, downloadDialog), downloadDialog);
    }
}
