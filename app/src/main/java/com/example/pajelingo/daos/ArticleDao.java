package com.example.pajelingo.daos;

import androidx.room.Dao;

import com.example.pajelingo.models.Article;

@Dao
public abstract class ArticleDao extends BaseDao<Article>{
    public ArticleDao() {
        super("Article");
    }
}
