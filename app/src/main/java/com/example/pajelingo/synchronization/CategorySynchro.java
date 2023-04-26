package com.example.pajelingo.synchronization;

import android.content.Context;

import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Category;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

public class CategorySynchro extends ResourcesSynchro<Category> {
    public CategorySynchro(Context context) {
        super(AppDatabase.getInstance(context).getCategoryDao(),
                () -> LanguageSchoolAPIHelper.getApiObject().getCategories(), new ConjugationSynchro(context));
    }
}
