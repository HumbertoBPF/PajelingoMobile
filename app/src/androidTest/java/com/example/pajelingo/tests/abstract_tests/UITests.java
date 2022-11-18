package com.example.pajelingo.tests.abstract_tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.example.pajelingo.utils.CustomMatchers.isGameNameAtPosition;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MainActivity;
import com.example.pajelingo.retrofit.LanguageSchoolAPI;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import org.junit.After;

import java.io.IOException;

public abstract class UITests {
    public ActivityScenario activityScenario;
    protected final Context context = ApplicationProvider.getApplicationContext();
    protected final LanguageSchoolAPI languageSchoolAPI = LanguageSchoolAPIHelper.getApiObject();

    /**
     * Asserts that the activity currently rendered is the MainActivity.
     * @param hasGames if the games are rendered (otherwise, a data warning is displayed).
     */
    protected void assertIsMenuActivity(boolean hasGames) {
        onView(withId(R.id.search_button)).check(matches(isDisplayed()));
        onView(withId(R.id.action_synchro)).check(matches(isDisplayed()));
        onView(withId(R.id.action_online)).check(matches(isDisplayed()));

        if (hasGames){
            onView(withId(R.id.games_recycler_view)).check(matches(isGameNameAtPosition("Vocabulary Training", 0)));
            onView(withId(R.id.games_recycler_view)).check(matches(isGameNameAtPosition("Guess the Article", 1)));
            onView(withId(R.id.games_recycler_view)).check(matches(isGameNameAtPosition("Conjugation Game", 2)));
        }else{
            onView(withId(R.id.no_data_constraint_layout)).check(matches(isDisplayed()));
            onView(withId(R.id.no_data_image_view)).check(matches(isDisplayed()));
            onView(withId(R.id.no_data_text_view)).check(matches(isDisplayed()));
        }
    }

    @After
    public void tearDown() throws IOException {
        if (activityScenario != null) {
            activityScenario.close();
        }
    }
}
