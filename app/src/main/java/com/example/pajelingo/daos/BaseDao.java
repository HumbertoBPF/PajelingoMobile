package com.example.pajelingo.daos;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;

import java.util.List;

@Dao
public interface BaseDao<E> {
    @Insert(onConflict = REPLACE)
    void save(List<E> entity);
}
