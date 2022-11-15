package com.example.pajelingo.synchronization;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Category;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

public class CategorySynchro extends ResourcesSynchro<Category> {
    public CategorySynchro(Context context, AlertDialog downloadDialog) {
        super("category", AppDatabase.getInstance(context).getCategoryDao(),
                () -> LanguageSchoolAPIHelper.getApiObject().getCategories(), new ConjugationSynchro(context, downloadDialog), downloadDialog);
    }
}
