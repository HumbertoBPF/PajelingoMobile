package com.example.pajelingo.tests.general_tests;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.example.pajelingo.utils.Tools.getAuthToken;
import static com.example.pajelingo.utils.Tools.saveStateAndUserCredentials;
import static com.example.pajelingo.utils.CustomMatchers.isScoreAtPosition;
import static com.example.pajelingo.utils.TestTools.getRandomInteger;
import static com.example.pajelingo.utils.TestTools.saveEntitiesFromAPI;
import static org.hamcrest.Matchers.is;

import androidx.test.core.app.ActivityScenario;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MainActivity;
import com.example.pajelingo.daos.LanguageDao;
import com.example.pajelingo.daos.ScoreDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Score;
import com.example.pajelingo.models.User;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelperTest;
import com.example.pajelingo.retrofit.LanguageSchoolAPITest;
import com.example.pajelingo.tests.abstract_tests.UITests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class RankingActivityTests extends UITests {
    private final LanguageSchoolAPITest languageSchoolAPITest = (LanguageSchoolAPITest) LanguageSchoolAPIHelperTest.getApiObject();
    private final User testUser = new User("test.android@test.com", "TestAndroid", "test-android");

    @Before
    public void setUp() throws IOException {
        context.deleteSharedPreferences(context.getString(R.string.sp_file_name));
        languageSchoolAPITest.deleteAccount(getAuthToken(testUser.getUsername(), testUser.getPassword())).execute();
        languageSchoolAPITest.signup(testUser).execute();

        saveEntitiesFromAPI(languageSchoolAPITest.getGames(), AppDatabase.getInstance(context).getGameDao());
        saveEntitiesFromAPI(languageSchoolAPITest.getLanguages(), AppDatabase.getInstance(context).getLanguageDao());
        saveEntitiesFromAPI(languageSchoolAPITest.getScores(getAuthToken(testUser.getUsername(), testUser.getPassword())),
                AppDatabase.getInstance(context).getScoreDao());

        saveStateAndUserCredentials(context, testUser.getUsername(), testUser.getPassword());
    }

    @Test
    public void testSelectGame(){
        LanguageDao languageDao = AppDatabase.getInstance(context).getLanguageDao();
        List<Language> languages = languageDao.getAllRecords();
        languages.add(new Language("General"));

        Language randomLanguage = languages.get(getRandomInteger(0, languages.size() - 1));

        ScoreDao scoreDao = AppDatabase.getInstance(context).getScoreDao();
        List<Score> scores;

        if (randomLanguage.getLanguageName().equals("General")){
            scores = scoreDao.getAllScoresSorted();
        }else{
            scores = scoreDao.getAllScoresSorted(randomLanguage.getLanguageName());
        }

        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_rankings)).perform(click());
        onView(withId(R.id.language_spinner)).perform(click());
        onData(is(randomLanguage)).inRoot(isPlatformPopup()).perform(click());

        for (int i=0;i<scores.size();i++){
            onView(withId(R.id.ranking_recycler_view)).check(matches(isScoreAtPosition(scores.get(i), i)));
        }
    }

    @After
    public void tearDown() throws IOException {
        context.deleteSharedPreferences(context.getString(R.string.sp_file_name));
        languageSchoolAPITest.deleteAccount(getAuthToken(testUser.getUsername(), testUser.getPassword())).execute();
    }
}
