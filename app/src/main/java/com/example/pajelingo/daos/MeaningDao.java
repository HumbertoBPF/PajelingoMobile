package com.example.pajelingo.daos;

import androidx.room.Dao;

import com.example.pajelingo.models.Meaning;

@Dao
public abstract class MeaningDao extends BaseDao<Meaning>{
    public MeaningDao() {
        super("Meaning");
    }
}
