package com.example.pajelingo.daos;

import android.os.AsyncTask;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pajelingo.interfaces.OnResultListener;
import com.example.pajelingo.models.Category;

@Dao
public abstract class CategoryDao extends BaseDao<Category>{
    public CategoryDao() {
        super("Category");
    }

    @Query("SELECT * FROM category WHERE categoryName=:categoryName")
    protected abstract Category getCategoryByName(String categoryName);

    public AsyncTask<Void, Void, Category> getCategoryByNameTask(String categoryName, OnResultListener<Category> onResultListener){
        return new AsyncTask<Void, Void, Category>(){

            @Override
            protected Category doInBackground(Void... voids) {
                return getCategoryByName(categoryName);
            }

            @Override
            protected void onPostExecute(Category category) {
                super.onPostExecute(category);
                onResultListener.onResult(category);
            }
        };
    }
}
