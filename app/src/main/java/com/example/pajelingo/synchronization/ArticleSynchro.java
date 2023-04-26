package com.example.pajelingo.synchronization;

import android.content.Context;

import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Article;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

public class ArticleSynchro extends ResourcesSynchro<Article>{
    public ArticleSynchro(Context context) {
        super(AppDatabase.getInstance(context).getArticleDao(),
                () -> LanguageSchoolAPIHelper.getApiObject().getArticles(), new CategorySynchro(context));
    }
}
