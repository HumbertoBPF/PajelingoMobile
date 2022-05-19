package com.example.pajelingo.daos;

import static androidx.room.OnConflictStrategy.REPLACE;

import android.os.AsyncTask;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.RawQuery;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

@Dao
public abstract class BaseDao<E> {

    protected String tableName;

    /**
     * @param tableName name of the table corresponding to the entity <b>E</b>.
     */
    public BaseDao(String tableName) {
        this.tableName = tableName;
    }

    @RawQuery
    protected abstract List<E> getAllRecords(SupportSQLiteQuery sqLiteQuery);

    /**
     * Executes the query to get the list of all the records of the entity <b>E</b>.
     * @return
     */
    public List<E> getAllRecords(){
        return getAllRecords(new SimpleSQLiteQuery("SELECT * FROM "+tableName));
    }

    @Insert(onConflict = REPLACE)
    public abstract void save(List<E> entity);

    public AsyncTask getSaveAsyncTask(List<E> entities){
        return new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                save(entities);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
            }
        };
    }



}
