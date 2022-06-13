package com.example.pajelingo.daos;

import static androidx.room.OnConflictStrategy.REPLACE;

import android.os.AsyncTask;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.RawQuery;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.example.pajelingo.interfaces.OnResultListener;

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
     * @return All the records of the concerned table.
     */
    public List<E> getAllRecords(){
        return getAllRecords(new SimpleSQLiteQuery("SELECT * FROM "+tableName));
    }

    public AsyncTask<Void, Void, List<E>> getAllRecordsTask(OnResultListener<List<E>> onResultListener){
        return new AsyncTask<Void, Void, List<E>>() {
            @Override
            protected List<E> doInBackground(Void... voids) {
                return getAllRecords();
            }

            @Override
            protected void onPostExecute(List<E> result) {
                super.onPostExecute(result);
                onResultListener.onResult(result);
            }
        };
    }

    @Insert(onConflict = REPLACE)
    public abstract void save(List<E> entity);

    public AsyncTask<Void, Void, Void> getSaveAsyncTask(List<E> entities){
        return new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                save(entities);
                return null;
            }
        };
    }

    @RawQuery
    protected abstract E getRecordById(SupportSQLiteQuery sqLiteQuery);

    /**
     * Executes the query to get the element among all the records that match with the specified id.
     * @return The entity corresponded to the specified id.
     */
    public E getRecordById(long id) {
        return getRecordById(new SimpleSQLiteQuery("SELECT * FROM "+tableName+" WHERE id = "+id));
    }

    public AsyncTask<Void, Void, E> getRecordByIdTask(long id, OnResultListener<E> onResultListener){
        return new AsyncTask<Void, Void, E>(){

            @Override
            protected E doInBackground(Void... voids) {
                return getRecordById(id);
            }

            @Override
            protected void onPostExecute(E e) {
                super.onPostExecute(e);
                onResultListener.onResult(e);
            }
        };
    }

}
