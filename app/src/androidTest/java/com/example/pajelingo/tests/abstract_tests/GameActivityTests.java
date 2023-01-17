package com.example.pajelingo.tests.abstract_tests;

import static com.example.pajelingo.utils.RetrofitTools.saveEntitiesFromAPI;

import com.example.pajelingo.R;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Game;

import org.junit.After;
import org.junit.Before;

import java.io.IOException;

public abstract class GameActivityTests extends UITests {
    protected Game game;

    @Before
    public void setUp() throws IOException {
        context.deleteSharedPreferences(context.getString(R.string.sp_file_name));

        saveEntitiesFromAPI(languageSchoolAPI.getGames(), AppDatabase.getInstance(context).getGameDao());
        saveEntitiesFromAPI(languageSchoolAPI.getLanguages(), AppDatabase.getInstance(context).getLanguageDao());
    }

    @After
    public void tearDown() throws IOException {
        super.tearDown();
        context.deleteSharedPreferences(context.getString(R.string.sp_file_name));
    }
}
