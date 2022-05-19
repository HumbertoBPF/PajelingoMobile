package com.example.pajelingo.daos;

import androidx.room.Dao;

import com.example.pajelingo.models.Language;

@Dao
public abstract class LanguageDao extends BaseDao<Language>{
    public LanguageDao() {
        super("Language");
    }
}
