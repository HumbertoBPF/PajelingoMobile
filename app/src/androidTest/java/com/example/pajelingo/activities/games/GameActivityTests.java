package com.example.pajelingo.activities.games;

import static com.example.pajelingo.util.Tools.getAuthToken;
import static com.example.pajelingo.utils.Tools.saveEntitiesFromAPI;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.UITests;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.User;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelperTest;
import com.example.pajelingo.retrofit.LanguageSchoolAPITest;

import org.junit.After;
import org.junit.Before;

import java.io.IOException;

public class GameActivityTests extends UITests {
    protected final LanguageSchoolAPITest languageSchoolAPITest = (LanguageSchoolAPITest) LanguageSchoolAPIHelperTest.getApiObject();
    protected final User testUser = new User("test.android@test.com", "TestAndroid", "test-android");

    @Before
    public void setUp() throws IOException {
        context.deleteSharedPreferences(context.getString(R.string.sp_file_name));
        languageSchoolAPITest.deleteAccount(getAuthToken(testUser.getUsername(), testUser.getPassword())).execute();
        languageSchoolAPITest.signup(testUser).execute();

        saveEntitiesFromAPI(languageSchoolAPITest.getGames(), AppDatabase.getInstance(context).getGameDao());
        saveEntitiesFromAPI(languageSchoolAPITest.getLanguages(), AppDatabase.getInstance(context).getLanguageDao());
    }

    @After
    public void tearDown() throws IOException {
        languageSchoolAPITest.deleteAccount(getAuthToken(testUser.getUsername(), testUser.getPassword())).execute();
        context.deleteSharedPreferences(context.getString(R.string.sp_file_name));
    }
}
