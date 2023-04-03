package com.example.pajelingo.activities.games;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pajelingo.R;
import com.example.pajelingo.daos.LanguageDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Game;
import com.example.pajelingo.retrofit.LanguageSchoolAPI;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

public abstract class GameActivity extends AppCompatActivity {

    protected LanguageSchoolAPI languageSchoolAPI;
    protected LanguageDao languageDao;
    protected Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        languageSchoolAPI = LanguageSchoolAPIHelper.getApiObject();
        languageDao = AppDatabase.getInstance(this).getLanguageDao();
        game = (Game) getIntent().getSerializableExtra("game");
        setTitle(game.getGameName());
        setup();
    }

    protected abstract void setup();

    protected abstract void startGame();

    protected abstract void verifyAnswer(Object answer);

    protected void finishActivityNotEnoughResources(){
        Toast.makeText(this, getString(R.string.warning_no_resources), Toast.LENGTH_LONG).show();
        finish();
    }

}