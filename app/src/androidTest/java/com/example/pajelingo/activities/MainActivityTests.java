package com.example.pajelingo.activities;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.CustomMatchers.isGameNameAtPosition;
import static com.example.pajelingo.utils.CustomViewActions.waitForView;
import static com.example.pajelingo.utils.Tools.saveEntitiesFromAPI;

import androidx.test.core.app.ActivityScenario;

import com.example.pajelingo.R;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelperTest;
import com.example.pajelingo.retrofit.LanguageSchoolAPITest;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class MainActivityTests extends UITests{
    private final LanguageSchoolAPITest languageSchoolAPITest = (LanguageSchoolAPITest) LanguageSchoolAPIHelperTest.getApiObject();

    @Before
    public void setUp() throws IOException {
        saveEntitiesFromAPI(languageSchoolAPITest.getGames(), AppDatabase.getInstance(context).getGameDao());
    }

    @Test
    public void testRenderingMainActivity(){
        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.search_button)).check(matches(isDisplayed()));
        onView(withId(R.id.action_synchro)).check(matches(isDisplayed()));
        onView(withId(R.id.action_online)).check(matches(isDisplayed()));
        onView(withId(R.id.games_recycler_view)).check(matches(isGameNameAtPosition(0, "Vocabulary Game")));
        onView(withId(R.id.games_recycler_view)).check(matches(isGameNameAtPosition(1, "Article Game")));
        onView(withId(R.id.games_recycler_view)).check(matches(isGameNameAtPosition(2, "Conjugation Game")));
    }

    @Test
    public void testSync(){
        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_synchro)).perform(click());
        onView(isRoot()).perform(waitForView(withText(context.getString(R.string.dialog_download_resources_title)), 5000, true));
        onView(withText(R.string.dialog_download_resources_confirm)).perform(click());
        onView(isRoot()).perform(waitForView(withText(context.getString(R.string.progress_download_title)), 5000, true));
        onView(isRoot()).perform(waitForView(withText(context.getString(R.string.progress_download_title)), 30000, false));
    }

    @Test
    public void testDeclineSyncConfirmationDialog(){
        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_synchro)).perform(click());
        onView(isRoot()).perform(waitForView(withText(context.getString(R.string.dialog_download_resources_title)), 5000, true));
        onView(withText(R.string.dialog_download_resources_decline)).perform(click());
        onView(withText(context.getString(R.string.progress_download_title))).check(doesNotExist());
        testRenderingMainActivity();
    }
}
