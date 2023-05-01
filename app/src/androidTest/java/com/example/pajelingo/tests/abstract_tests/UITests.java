package com.example.pajelingo.tests.abstract_tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.CustomMatchers.atPosition;
import static com.example.pajelingo.utils.RetrofitTools.saveEntitiesFromAPI;
import static org.hamcrest.Matchers.allOf;

import android.content.Context;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.example.pajelingo.R;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.User;
import com.example.pajelingo.retrofit.LanguageSchoolAPI;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;

public abstract class UITests {
    public ActivityScenario<?> activityScenario;
    protected final Context context = ApplicationProvider.getApplicationContext();
    protected final LanguageSchoolAPI languageSchoolAPI = LanguageSchoolAPIHelper.getApiObject();
    protected final User testUser = new User("test-android@test.com", "test-android", "str0ng-p4ssw0rd", null);

    @Before
    public void setUp() throws IOException {
        context.deleteSharedPreferences(context.getString(R.string.sp_file_name));
        AppDatabase appDatabase = AppDatabase.getInstance(context);
        appDatabase.clearAllTables();
        saveEntitiesFromAPI(languageSchoolAPI.getArticles(), appDatabase.getArticleDao());
        saveEntitiesFromAPI(languageSchoolAPI.getCategories(), appDatabase.getCategoryDao());
        saveEntitiesFromAPI(languageSchoolAPI.getConjugations(), appDatabase.getConjugationDao());
        saveEntitiesFromAPI(languageSchoolAPI.getGames(), appDatabase.getGameDao());
        saveEntitiesFromAPI(languageSchoolAPI.getLanguages(), appDatabase.getLanguageDao());
        saveEntitiesFromAPI(languageSchoolAPI.getMeanings(), appDatabase.getMeaningDao());
        saveEntitiesFromAPI(languageSchoolAPI.getScores(), appDatabase.getScoreDao());
        saveEntitiesFromAPI(languageSchoolAPI.getWords(), appDatabase.getWordDao());
    }

    /**
     * Asserts that the activity currently rendered is the MainActivity.
     * @param hasGames if the games are rendered (otherwise, a data warning is displayed).
     */
    protected void assertIsMainActivity(boolean hasGames) {
        onView(withId(R.id.search_button)).check(matches(isDisplayed()));
        onView(withId(R.id.action_synchro)).check(matches(isDisplayed()));
        onView(withId(R.id.action_login_logout)).check(matches(isDisplayed()));

        if (hasGames){
            onView(withId(R.id.games_recycler_view))
                    .check(matches(atPosition(hasDescendant(getGameMatcher("Vocabulary Training")), 0)));
            onView(withId(R.id.games_recycler_view))
                    .check(matches(atPosition(hasDescendant(getGameMatcher("Guess the Article")), 1)));
            onView(withId(R.id.games_recycler_view))
                    .check(matches(atPosition(hasDescendant(getGameMatcher("Conjugation Game")), 2)));
        }else{
            onView(withId(R.id.no_data_constraint_layout)).check(matches(isDisplayed()));
            onView(withId(R.id.no_data_image_view)).check(matches(isDisplayed()));
            onView(withId(R.id.no_data_text_view)).check(matches(isDisplayed()));
        }
    }

    private Matcher<View> getGameMatcher(String gameName) {
        return allOf(withId(R.id.game_name_text_view), withText(gameName));
    }

    @After
    public void tearDown() {
        if (activityScenario != null) {
            activityScenario.close();
        }
        context.deleteSharedPreferences(context.getString(R.string.sp_file_name));
    }
}
