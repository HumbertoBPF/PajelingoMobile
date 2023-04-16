package com.example.pajelingo.daos;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.RawQuery;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.example.pajelingo.interfaces.OnResultListener;
import com.example.pajelingo.utils.BackgroundTask;

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

    public void getAllRecords(OnResultListener<List<E>> onResultListener){
        BackgroundTask<List<E>> backgroundTask = new BackgroundTask<>(this::getAllRecords, onResultListener);
        backgroundTask.execute();
    }

    @Insert(onConflict = REPLACE)
    public abstract void save(List<E> entity);

    public void save(List<E> entities, OnResultListener<List<E>> onResultListener){
        BackgroundTask<List<E>> backgroundTask = new BackgroundTask<>(() -> {
            save(entities);
            return entities;
        }, onResultListener);
        backgroundTask.execute();
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

    public void getRecordById(long id, OnResultListener<E> onResultListener){
        BackgroundTask<E> backgroundTask = new BackgroundTask<>(() -> getRecordById(id), onResultListener);
        backgroundTask.execute();
    }

}
