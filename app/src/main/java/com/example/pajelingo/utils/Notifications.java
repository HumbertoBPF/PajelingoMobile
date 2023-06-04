package com.example.pajelingo.utils;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.pajelingo.R;
import com.example.pajelingo.models.Badge;

import java.util.List;

public class Notifications {
    public static final String CHANNEL_ID = "notification channel id";
    public static final int PROGRESS_MAX = 100;
    public static final int SYNC_NOTIFICATION_ID = 1;
    public static final int BADGE_NOTIFICATION_ID = 2;

    public static void updateProgressBarNotification(Context context, NotificationCompat.Builder builder, int percentage) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            builder.setProgress(PROGRESS_MAX, percentage,false);
            showNotification(context, builder, SYNC_NOTIFICATION_ID);
        }
    }

    public static void showSyncFeedbackNotification(Context context, NotificationCompat.Builder builder, boolean isSuccessful) {
        String titleNotification;
        String textNotification;
        int iconResource;

        if (isSuccessful) {
            titleNotification = context.getString(R.string.sync_notification_concluded_title);
            textNotification = context.getString(R.string.sync_notification_concluded_text);
            iconResource = R.drawable.ic_check;
        }else {
            titleNotification = context.getString(R.string.sync_notification_failed_title);
            textNotification = context.getString(R.string.sync_notification_failed_text);
            iconResource = R.drawable.ic_warning;
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            builder.setContentTitle(titleNotification)
                    .setContentText(textNotification)
                    .setSmallIcon(iconResource)
                    .setProgress(0, 0,false)
                    .setOngoing(false);
            showNotification(context, builder, SYNC_NOTIFICATION_ID);
        }
    }


    public static void showBadgeNotification(Context context, List<Badge> badges) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            String titleNotification = context.getString(R.string.badges_notification_title);
            String textNotification = context.getString(R.string.badges_notification_text, badges.toString());

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle(titleNotification)
                    .setContentText(textNotification)
                    .setSmallIcon(R.drawable.ic_badge);
            showNotification(context, builder, BADGE_NOTIFICATION_ID);
        }
    }


    public static void showBadgeNotification(Context context, Badge badge) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            String titleNotification = context.getString(R.string.badge_notification_title, badge.getName());
            String textNotification = badge.getDescription();

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle(titleNotification)
                    .setContentText(textNotification)
                    .setSmallIcon(R.drawable.ic_badge);
            showNotification(context, builder, BADGE_NOTIFICATION_ID);
        }
    }


    public static void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.notification_channel_name);
            String description = context.getString(R.string.notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void showNotification(Context context, NotificationCompat.Builder builder, int notificationId) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(notificationId, builder.build());
        }
    }
}
