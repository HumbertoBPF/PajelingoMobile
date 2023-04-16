package com.example.pajelingo.utils;

import android.os.Handler;
import android.os.Looper;

import com.example.pajelingo.interfaces.BackgroundTaskInterface;
import com.example.pajelingo.interfaces.OnResultListener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackgroundTask<E> {
    private final ExecutorService executor;
    private final Handler handler;
    private final BackgroundTaskInterface<E> backgroundTaskInterface;
    private final OnResultListener<E> onResultListener;

    public BackgroundTask(BackgroundTaskInterface<E> backgroundTaskInterface, OnResultListener<E> onResultListener){
        this.executor = Executors.newSingleThreadExecutor();
        this.handler = new Handler(Looper.getMainLooper());
        this.backgroundTaskInterface = backgroundTaskInterface;
        this.onResultListener = onResultListener;
    }

    public void execute() {
        executor.execute(() -> {
            E entity = backgroundTaskInterface.doInBackground();

            handler.post(() -> {
                if (onResultListener != null){
                    onResultListener.onResult(entity);
                }
            });
        });
    }
}