package com.example.pajelingo.AsyncTasks;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.retrofit.ServiceAPIHelper;

import java.util.List;

import retrofit2.Call;

public class LanguageTask extends ResourcesTask<Language>{
    public LanguageTask(Context context, AlertDialog downloadDialog) {
        super("language", AppDatabase.getInstance(context).getLanguageDao(), new ResourcesInterface<Language>() {
            @Override
            public Call<List<Language>> getCallForResources() {
                return ServiceAPIHelper.getApiObject().getLanguages();
            }
        }, new MeaningTask(context, downloadDialog), downloadDialog);
    }
}
