package com.example.pajelingo.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.example.pajelingo.daos.BaseDao;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class ResourcesTask<E> extends AsyncTask {

    private final String resourceName;
    private final BaseDao<E> dao;
    private final ResourcesInterface<E> resourcesInterface;
    private final ResourcesTask nextTask;
    private AlertDialog downloadDialog;
    private Handler handler =  new Handler();

    public ResourcesTask(String resourceName, BaseDao<E> dao, ResourcesInterface<E> resourcesInterface,
                         ResourcesTask nextTask, AlertDialog downloadDialog){
        this.resourceName = resourceName;
        this.dao = dao;
        this.resourcesInterface = resourcesInterface;
        this.nextTask = nextTask;
        this.downloadDialog = downloadDialog;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        Call<List<E>> callObject = this.resourcesInterface.getCallForResources();
        callObject.enqueue(new Callback<List<E>>() {
            @Override
            public void onResponse(Call<List<E>> call, Response<List<E>> response) {
                if (response.isSuccessful()) {
                    List<E> entities = response.body();
                    downloadDialog.setMessage("Downloading "+resourceName+" table");
                    if (entities != null){
                        Log.i("ResourcesTask","Number of elements of "+resourceName+" table: " + entities.size());
                    }
                    dao.getSaveAsyncTask(entities);
                }else{
                    Log.e("ResourcesTask", "doInBackground:onResponse not successful");
                }
            }

            @Override
            public void onFailure(Call<List<E>> call, Throwable t) {
                Log.e("ResourcesTask", "doInBackground:onFailure");
            }
        });

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (nextTask != null) {
                    nextTask.execute();
                }else {
                    downloadDialog.dismiss();
                }
            }
        },2000);
    }


    public interface ResourcesInterface<E> {
        Call<List<E>> getCallForResources();
    }

}
