package com.example.pajelingo.synchronization;

import static com.example.pajelingo.utils.Tools.PROGRESS_MAX;
import static com.example.pajelingo.utils.Tools.SYNC_NOTIFICATION_ID;
import static com.example.pajelingo.utils.Tools.showNotification;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.pajelingo.R;
import com.example.pajelingo.daos.BaseDao;
import com.example.pajelingo.interfaces.OnTaskListener;

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

    private final Context context;
    private final NotificationCompat.Builder builder;
    private final int percentage;
    private final BaseDao<E> dao;
    private final ResourcesInterface<E> resourcesInterface;
    private final ResourcesSynchro<?> nextTask;
    private OnTaskListener onTaskListener;
    private final Handler handler =  new Handler();

    public ResourcesSynchro(Context context, NotificationCompat.Builder builder, int percentage, BaseDao<E> dao,
                            ResourcesInterface<E> resourcesInterface, ResourcesSynchro<?> nextTask){
        this.context = context;
        this.builder = builder;
        this.percentage = percentage;
        this.dao = dao;
        this.resourcesInterface = resourcesInterface;
        this.nextTask = nextTask;
    }

    public void execute(OnTaskListener onTaskListener) {
        this.onTaskListener = onTaskListener;

        Call<List<E>> callObject = this.resourcesInterface.getCallForResources();

        callObject.enqueue(new Callback<List<E>>() {
            @Override
            public void onResponse(@NonNull Call<List<E>> call, @NonNull Response<List<E>> response) {
                if (response.isSuccessful()) {
                    saveEntities(response.body());
                }else{
                    Log.e("ResourcesSynchro", "doInBackground:onResponse not successful");
                    terminateSynchronization(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<E>> call, @NonNull Throwable t) {
                Log.e("ResourcesSynchro", "doInBackground:onFailure");
                terminateSynchronization(false);
            }
        });
    }

    protected void saveEntities(List<E> entities) {
        updateProgressBarNotification(percentage);
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
            if (nextTask != null) {
                nextTask.execute(this.onTaskListener);
            }else {
                updateProgressBarNotification(PROGRESS_MAX);
                handler.postDelayed(() -> terminateSynchronization(true), 2000);
            }
        },2000);
    }

    private void updateProgressBarNotification(int percentage) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            builder.setProgress(PROGRESS_MAX, percentage,false);
            showNotification(context, builder, SYNC_NOTIFICATION_ID);
        }
    }

    private void terminateSynchronization(boolean isSuccessful) {
        String titleNotification;
        String textNotification;
        int iconResource;
        String toastMessage;

        if (isSuccessful) {
            titleNotification = context.getString(R.string.sync_notification_concluded_title);
            textNotification = context.getString(R.string.sync_notification_concluded_text);
            iconResource = R.drawable.ic_check;
            toastMessage = context.getString(R.string.sync_toast_concluded);
        }else {
            titleNotification = context.getString(R.string.sync_notification_failed_title);
            textNotification = context.getString(R.string.sync_notification_failed_text);
            iconResource = R.drawable.ic_warning;
            toastMessage = context.getString(R.string.sync_toast_failed);
        }

        onTaskListener.onTask();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            builder.setContentTitle(titleNotification)
                    .setContentText(textNotification)
                    .setSmallIcon(iconResource)
                    .setProgress(0, 0,false)
                    .setOngoing(false);
            showNotification(context, builder, SYNC_NOTIFICATION_ID);
        }
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
    }

    public interface ResourcesInterface<E> {
        Call<List<E>> getCallForResources();
    }
}
