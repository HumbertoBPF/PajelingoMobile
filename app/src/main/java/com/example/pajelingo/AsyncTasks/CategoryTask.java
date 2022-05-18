package com.example.pajelingo.AsyncTasks;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Category;
import com.example.pajelingo.retrofit.ServiceAPIHelper;

import java.util.List;

import retrofit2.Call;

public class CategoryTask extends ResourcesTask<Category>{
    public CategoryTask(Context context, AlertDialog downloadDialog) {
        super("category", AppDatabase.getInstance(context).getCategoryDao(), new ResourcesInterface<Category>() {
            @Override
            public Call<List<Category>> getCallForResources() {
                return ServiceAPIHelper.getApiObject().getCategories();
            }
        }, new ConjugationTask(context, downloadDialog), downloadDialog);
    }
}
