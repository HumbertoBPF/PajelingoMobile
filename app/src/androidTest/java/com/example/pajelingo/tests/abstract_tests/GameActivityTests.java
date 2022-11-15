package com.example.pajelingo.tests.abstract_tests;

import static com.example.pajelingo.utils.TestTools.saveEntitiesFromAPI;
import static com.example.pajelingo.utils.Tools.getAuthToken;

import com.example.pajelingo.R;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Game;
import com.example.pajelingo.models.User;
import com.example.pajelingo.retrofit.LanguageSchoolAPI;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import org.junit.After;
import org.junit.Before;

import java.io.IOException;

public abstract class GameActivityTests extends UITests {
    protected final User testUser = new User("test.android@test.com", "TestAndroid", "test-android");
    protected Game game;

    @Before
    public void setUp() throws IOException {
        context.deleteSharedPreferences(context.getString(R.string.sp_file_name));
        languageSchoolAPI.deleteAccount(getAuthToken(testUser.getUsername(), testUser.getPassword())).execute();
        languageSchoolAPI.signup(testUser).execute();

        saveEntitiesFromAPI(languageSchoolAPI.getGames(), AppDatabase.getInstance(context).getGameDao());
        saveEntitiesFromAPI(languageSchoolAPI.getLanguages(), AppDatabase.getInstance(context).getLanguageDao());
    }

    @After
    public void tearDown() throws IOException {
        super.tearDown();
        languageSchoolAPI.deleteAccount(getAuthToken(testUser.getUsername(), testUser.getPassword())).execute();
        context.deleteSharedPreferences(context.getString(R.string.sp_file_name));
    }
}
