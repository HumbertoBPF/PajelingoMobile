package com.example.pajelingo.synchronization;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Article;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import java.util.List;

import retrofit2.Call;

public class ArticleSynchro extends ResourcesSynchro<Article>{
    public ArticleSynchro(Context context, AlertDialog downloadDialog) {
        super("article", AppDatabase.getInstance(context).getArticleDao(), new ResourcesInterface<Article>() {
            @Override
            public Call<List<Article>> getCallForResources() {
                return LanguageSchoolAPIHelper.getApiObject().getArticles();
            }
        }, new CategorySynchro(context, downloadDialog), downloadDialog);
    }
}
