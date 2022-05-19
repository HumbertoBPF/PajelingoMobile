package com.example.pajelingo.daos;

import androidx.room.Dao;

import com.example.pajelingo.models.Category;

@Dao
public abstract class CategoryDao extends BaseDao<Category>{
    public CategoryDao() {
        super("Category");
    }
}
