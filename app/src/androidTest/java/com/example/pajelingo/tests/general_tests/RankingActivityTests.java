package com.example.pajelingo.tests.general_tests;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
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
import com.example.pajelingo.tests.abstract_tests.UITests;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class RankingActivityTests extends UITests {

    @Before
    public void setUp() throws IOException {
        context.deleteSharedPreferences(context.getString(R.string.sp_file_name));
        saveEntitiesFromAPI(languageSchoolAPI.getGames(), AppDatabase.getInstance(context).getGameDao());
        saveEntitiesFromAPI(languageSchoolAPI.getLanguages(), AppDatabase.getInstance(context).getLanguageDao());
        saveEntitiesFromAPI(languageSchoolAPI.getScores(), AppDatabase.getInstance(context).getScoreDao());
    }

    @Test
    public void testSelectGame(){
        LanguageDao languageDao = AppDatabase.getInstance(context).getLanguageDao();
        List<Language> languages = languageDao.getAllRecords();
        languages.add(new Language(context.getString(R.string.general_ranking_spinner_text)));

        Language randomLanguage = languages.get(getRandomInteger(0, languages.size() - 1));

        ScoreDao scoreDao = AppDatabase.getInstance(context).getScoreDao();
        List<Score> scores;

        if (randomLanguage.getLanguageName().equals(context.getString(R.string.general_ranking_spinner_text))){
            scores = scoreDao.getAllScoresSorted();
        }else{
            scores = scoreDao.getAllScoresSorted(randomLanguage.getLanguageName());
        }

        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_menu)).perform(click());
        onView(withId(R.id.menu_items_recycler_view)).perform(actionOnItemAtPosition(0, click()));
        onView(withId(R.id.language_spinner)).perform(click());
        onData(is(randomLanguage)).inRoot(isPlatformPopup()).perform(click());

        for (int i=0;i<scores.size();i++){
            onView(withId(R.id.ranking_recycler_view)).check(matches(isScoreAtPosition(scores.get(i), i)));
        }
    }
}
