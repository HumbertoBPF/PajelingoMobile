package com.example.pajelingo.synchronization;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Game;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import java.util.List;

import retrofit2.Call;

public class GameSynchro extends ResourcesSynchro<Game>{
    public GameSynchro(Context context, AlertDialog downloadDialog) {
        super("game", AppDatabase.getInstance(context).getGameDao(), new ResourcesInterface<Game>() {
            @Override
            public Call<List<Game>> getCallForResources() {
                return LanguageSchoolAPIHelper.getApiObject().getGames();
            }
        }, null, downloadDialog);
    }
}
