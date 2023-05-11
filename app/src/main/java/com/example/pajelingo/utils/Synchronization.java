package com.example.pajelingo.utils;

import static com.example.pajelingo.database.settings.AppDatabase.NAME_DB;
import static com.example.pajelingo.utils.Files.clearFolder;
import static com.example.pajelingo.utils.Notifications.CHANNEL_ID;
import static com.example.pajelingo.utils.Notifications.PROGRESS_MAX;
import static com.example.pajelingo.utils.Notifications.SYNC_NOTIFICATION_ID;
import static com.example.pajelingo.utils.Notifications.showNotification;
import static com.example.pajelingo.utils.Notifications.showSyncFeedbackNotification;
import static com.example.pajelingo.utils.Notifications.updateProgressBarNotification;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;

import com.example.pajelingo.R;
import com.example.pajelingo.interfaces.OnTaskListener;
import com.example.pajelingo.retrofit_calls.synchronization.ArticleSynchro;

public class Synchronization {
    /**
     * Launches the synchronization of resources, which is implemented through a chain of
     * responsibility design pattern.
     * @param context application's context.
     * @param inflater layout inflated.
     * @param onTaskListener code to be implemented at the end of the sync, either when it is
     *                       successful or when it fails.
     */
    public static void launchResourcesSync(Context context, LayoutInflater inflater, OnTaskListener onTaskListener){
        final int nbSteps = 8;
        // Sync progress notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_synchro)
                .setContentTitle(context.getString(R.string.sync_notification_title))
                .setContentText(context.getString(R.string.sync_notification_in_progress))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setProgress(PROGRESS_MAX, 0, false)
                .setOngoing(true);
        showNotification(context, notificationBuilder, SYNC_NOTIFICATION_ID);
        // Loading dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context)
                .setTitle(R.string.dialog_confirm_resources_sync_title)
                .setView(inflater.inflate(R.layout.loading_dialog_layout, null))
                .setCancelable(false);
        AlertDialog loadingDialog = dialogBuilder.create();
        loadingDialog.show();
        // Delete database and internal storage files before synchronization
        context.deleteDatabase(NAME_DB);
        clearFolder(context.getFilesDir());

        new ArticleSynchro(context).execute((isSuccessful, currentStep) -> {
            int percentage = Math.min(Math.round(currentStep*100f/nbSteps), 100);

            updateProgressBarNotification(context, notificationBuilder, percentage);

            if ((!isSuccessful) || (currentStep == nbSteps)) {
                loadingDialog.dismiss();
                showSyncFeedbackNotification(context, notificationBuilder, isSuccessful);
                showSyncFeedbackToast(context, isSuccessful);
                onTaskListener.onTask();
            }
        });
    }

    private static void showSyncFeedbackToast(Context context, boolean isSuccessful) {
        String toastMessage;

        if (isSuccessful) {
            toastMessage = context.getString(R.string.sync_toast_concluded);
        }else {
            toastMessage = context.getString(R.string.sync_toast_failed);
        }

        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
    }
}
