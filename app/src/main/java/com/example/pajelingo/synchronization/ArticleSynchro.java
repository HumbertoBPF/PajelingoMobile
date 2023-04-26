package com.example.pajelingo.synchronization;

import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Article;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

public class ArticleSynchro extends ResourcesSynchro<Article>{
    public ArticleSynchro(Context context, NotificationCompat.Builder builder) {
        super(context, builder, 0, AppDatabase.getInstance(context).getArticleDao(),
                () -> LanguageSchoolAPIHelper.getApiObject().getArticles(), new CategorySynchro(context, builder));
    }
}
