package com.example.pajelingo.AsyncTasks;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Word;
import com.example.pajelingo.retrofit.ServiceAPIHelper;

import java.util.List;

import retrofit2.Call;

public class WordTask extends ResourcesTask<Word>{
    public WordTask(Context context, AlertDialog downloadDialog) {
        super("word", AppDatabase.getInstance(context).getWordDao(), new ResourcesInterface<Word>() {
            @Override
            public Call<List<Word>> getCallForResources() {
                return ServiceAPIHelper.getApiObject().getWords();
            }
        }, null, downloadDialog);
    }
}
