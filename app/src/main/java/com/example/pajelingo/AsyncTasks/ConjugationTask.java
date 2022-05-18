package com.example.pajelingo.AsyncTasks;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Conjugation;
import com.example.pajelingo.retrofit.ServiceAPIHelper;

import java.util.List;

import retrofit2.Call;

public class ConjugationTask extends ResourcesTask<Conjugation>{
    public ConjugationTask(Context context, AlertDialog downloadDialog) {
        super("conjugation", AppDatabase.getInstance(context).getConjugationDao(), new ResourcesInterface<Conjugation>() {
            @Override
            public Call<List<Conjugation>> getCallForResources() {
                return ServiceAPIHelper.getApiObject().getConjugations();
            }
        }, new LanguageTask(context, downloadDialog), downloadDialog);
    }
}
