package com.example.pajelingo.tests.general_tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.CustomViewActions.waitUntil;

import androidx.test.core.app.ActivityScenario;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MainActivity;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.tests.abstract_tests.UITests;

import org.junit.Test;

public class MainActivityTests extends UITests {
    @Test
    public void testRenderingMainActivity(){
        activityScenario = ActivityScenario.launch(MainActivity.class);
        assertIsMainActivity(true);
    }

    @Test
    public void testSync(){
        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_synchro)).perform(click());
        onView(isRoot()).perform(waitUntil(withText(R.string.dialog_confirm_resources_sync_title), 5000, true));
        onView(withText(R.string.dialog_confirm_resources_sync_confirm)).perform(click());
        onView(isRoot()).perform(waitUntil(withText(R.string.dialog_confirm_resources_sync_title), 5000, true));
        onView(isRoot()).perform(waitUntil(withText(R.string.dialog_confirm_resources_sync_title), 150000, false));
    }

    @Test
    public void testDeclineSyncConfirmationDialog(){
        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_synchro)).perform(click());
        onView(isRoot()).perform(waitUntil(withText(R.string.dialog_confirm_resources_sync_title), 5000, true));
        onView(withText(R.string.dialog_confirm_resources_sync_decline)).perform(click());
        onView(withText(R.string.dialog_confirm_resources_sync_title)).check(doesNotExist());
        assertIsMainActivity(true);
    }

    @Test
    public void testMainActivityWithNoData(){
        AppDatabase.getInstance(context).clearAllTables();
        activityScenario = ActivityScenario.launch(MainActivity.class);
        assertIsMainActivity(false);
    }
}
