package com.example.pajelingo.AsyncTasks;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Meaning;
import com.example.pajelingo.retrofit.ServiceAPIHelper;

import java.util.List;

import retrofit2.Call;

public class MeaningTask extends ResourcesTask<Meaning>{
    public MeaningTask(Context context, AlertDialog downloadDialog) {
        super("meaning", AppDatabase.getInstance(context).getMeaningDao(), new ResourcesInterface<Meaning>() {
            @Override
            public Call<List<Meaning>> getCallForResources() {
                return ServiceAPIHelper.getApiObject().getMeanings();
            }
        }, new WordTask(context, downloadDialog), downloadDialog);
    }
}
