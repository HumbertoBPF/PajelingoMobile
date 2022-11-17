package com.example.pajelingo.tests.general_tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.CustomViewActions.waitForView;
import static com.example.pajelingo.utils.TestTools.saveEntitiesFromAPI;

import androidx.test.core.app.ActivityScenario;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MainActivity;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.tests.abstract_tests.UITests;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class MainActivityTests extends UITests {

    @Before
    public void setUp() throws IOException {
        saveEntitiesFromAPI(languageSchoolAPI.getGames(), AppDatabase.getInstance(context).getGameDao());
    }

    @Test
    public void testRenderingMainActivity(){
        activityScenario = ActivityScenario.launch(MainActivity.class);
        assertIsMenuActivity(true);
    }

    @Test
    public void testSync(){
        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_synchro)).perform(click());
        onView(isRoot()).perform(waitForView(withText(R.string.dialog_download_resources_title), 5000, true));
        onView(withText(R.string.dialog_download_resources_confirm)).perform(click());
        onView(isRoot()).perform(waitForView(withText(R.string.progress_download_title), 5000, true));
        onView(isRoot()).perform(waitForView(withText(R.string.progress_download_title), 30000, false));
    }

    @Test
    public void testDeclineSyncConfirmationDialog(){
        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_synchro)).perform(click());
        onView(isRoot()).perform(waitForView(withText(R.string.dialog_download_resources_title), 5000, true));
        onView(withText(R.string.dialog_download_resources_decline)).perform(click());
        onView(withText(R.string.progress_download_title)).check(doesNotExist());
        assertIsMenuActivity(true);
    }

    @Test
    public void testMainActivityWithNoData(){
        AppDatabase.getInstance(context).clearAllTables();
        activityScenario = ActivityScenario.launch(MainActivity.class);
        assertIsMenuActivity(false);
    }
}
