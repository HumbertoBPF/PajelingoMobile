package com.example.pajelingo.synchronization;

import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.example.pajelingo.daos.BaseDao;
import com.example.pajelingo.interfaces.OnResultListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Object responsible for the synchronization step(i.e. resources download) of the entity <b>E</b>.
 * A chain of responsibility design pattern is used to call the different steps in the correct order.
 * @param <E> Model entity representing the resource concerned.
 */
public abstract class ResourcesSynchro<E> {

    private final String resourceName;
    private final BaseDao<E> dao;
    private final ResourcesInterface<E> resourcesInterface;
    private final ResourcesSynchro nextTask;
    private AlertDialog downloadDialog;
    private Handler handler =  new Handler();

    public ResourcesSynchro(String resourceName, BaseDao<E> dao, ResourcesInterface<E> resourcesInterface,
                            ResourcesSynchro nextTask, AlertDialog downloadDialog){
        this.resourceName = resourceName;
        this.dao = dao;
        this.resourcesInterface = resourcesInterface;
        this.nextTask = nextTask;
        this.downloadDialog = downloadDialog;
    }

    public void execute() {
        Call<List<E>> callObject = this.resourcesInterface.getCallForResources();
        callObject.enqueue(new Callback<List<E>>() {
            @Override
            public void onResponse(Call<List<E>> call, Response<List<E>> response) {
                if (response.isSuccessful()) {
                    List<E> entities = response.body();
                    downloadDialog.setMessage("Downloading "+resourceName+" table");
                    if (entities != null){
                        Log.i("ResourcesSynchro","Number of elements of "+resourceName+" table: " + entities.size());
                    }
                    dao.getSaveAsyncTask(entities, new OnResultListener<List<E>>() {
                        @Override
                        public void onResult(List<E> result) {
                            nextStep();
                        }
                    }).execute();
                }else{
                    Log.e("ResourcesSynchro", "doInBackground:onResponse not successful");
                    downloadDialog.setMessage("Fail to download "+resourceName+" table");
                    nextStep();
                }
            }

            @Override
            public void onFailure(Call<List<E>> call, Throwable t) {
                Log.e("ResourcesSynchro", "doInBackground:onFailure");
                downloadDialog.setMessage("Fail to download "+resourceName+" table");
                nextStep();
            }
        });
    }

    /**
     * Calls the object responsible for the next synchronization step.
     */
    protected void nextStep() {
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
