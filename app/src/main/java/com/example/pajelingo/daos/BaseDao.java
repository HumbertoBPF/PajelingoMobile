package com.example.pajelingo.daos;

import static androidx.room.OnConflictStrategy.REPLACE;

import android.os.AsyncTask;

import androidx.room.Dao;
import androidx.room.Insert;

import java.util.List;

@Dao
public abstract class BaseDao<E> {

    @Insert(onConflict = REPLACE)
    public abstract void save(List<E> entity);

    public AsyncTask getSaveAsyncTask(List<E> entities){
        return new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                save(entities);
                return null;
            }
        };
    }

}
