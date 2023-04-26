package com.example.pajelingo.synchronization;

import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Category;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

public class CategorySynchro extends ResourcesSynchro<Category> {
    public CategorySynchro(Context context, NotificationCompat.Builder builder) {
        super(context, builder, 13, AppDatabase.getInstance(context).getCategoryDao(),
                () -> LanguageSchoolAPIHelper.getApiObject().getCategories(), new ConjugationSynchro(context, builder));
    }
}
