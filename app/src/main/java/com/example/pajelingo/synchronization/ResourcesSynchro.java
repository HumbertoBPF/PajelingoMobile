package com.example.pajelingo.synchronization;

import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pajelingo.daos.BaseDao;

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

    private int currentStep;
    private final BaseDao<E> dao;
    private final ResourcesInterface<E> resourcesInterface;
    private final ResourcesSynchro<?> nextTask;
    private OnSyncListener onSyncListener;
    private final Handler handler =  new Handler();

    public ResourcesSynchro(BaseDao<E> dao, ResourcesInterface<E> resourcesInterface, ResourcesSynchro<?> nextTask){
        this.dao = dao;
        this.resourcesInterface = resourcesInterface;
        this.nextTask = nextTask;
    }

    public void execute(int currentStep, OnSyncListener onSyncListener) {
        this.currentStep = currentStep;
        this.onSyncListener = onSyncListener;

        Call<List<E>> callObject = this.resourcesInterface.getCallForResources();

        callObject.enqueue(new Callback<List<E>>() {
            @Override
            public void onResponse(@NonNull Call<List<E>> call, @NonNull Response<List<E>> response) {
                if (response.isSuccessful()) {
                    saveEntities(response.body());
                }else{
                    Log.e("ResourcesSynchro", "doInBackground:onResponse not successful");
                    onSyncListener.onSync(false, currentStep);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<E>> call, @NonNull Throwable t) {
                Log.e("ResourcesSynchro", "doInBackground:onFailure");
                onSyncListener.onSync(false, currentStep);
            }
        });
    }

    protected void saveEntities(List<E> entities) {
        if (entities != null){
            Log.i("ResourcesSynchro","Number of elements of table: " + entities.size());
        }
        dao.save(entities, result -> nextStep());
    }

    /**
     * Calls the object responsible for the next synchronization step.
     */
    protected void nextStep() {
        handler.postDelayed(() -> {
            onSyncListener.onSync(true, currentStep);

            if (nextTask != null) {
                nextTask.execute(currentStep + 1, this.onSyncListener);
            }
        },2000);
    }

    public interface ResourcesInterface<E> {
        Call<List<E>> getCallForResources();
    }

    public interface OnSyncListener {
        void onSync(boolean isSuccessful, int currentStep);
    }
}
