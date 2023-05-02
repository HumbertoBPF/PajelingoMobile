package com.example.pajelingo.tests.general_tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.CustomViewActions.waitUntil;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

import android.view.View;

import androidx.test.core.app.ActivityScenario;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MainActivity;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.tests.abstract_tests.UITests;

import org.hamcrest.Matcher;
import org.junit.Test;

public class MainActivityTests extends UITests {
    @Test
    public void testRenderingMainActivity(){
        activityScenario = ActivityScenario.launch(MainActivity.class);
        assertIsMainActivity(true);
    }

    @Test
    public void testSync() throws InterruptedException {
        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_synchro)).perform(click());

        Matcher<View> dialogConfirmSyncMatcher = allOf(withText(R.string.dialog_confirm_resources_sync_title), isDisplayed());
        onView(isRoot())
                .perform(waitUntil(dialogConfirmSyncMatcher, 5000));

        onView(withText(R.string.dialog_confirm_resources_sync_confirm))
                .perform(click());

        Matcher<View> dialogSyncMatcher = allOf(withText(R.string.dialog_confirm_resources_sync_title), isDisplayed());
        onView(isRoot())
                .perform(waitUntil(dialogSyncMatcher, 5000));

        Thread.sleep(60000);
        onView(withText(R.string.dialog_confirm_resources_sync_title)).check(doesNotExist());
    }

    @Test
    public void testDeclineSyncConfirmationDialog(){
        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_synchro))
                .perform(click());

        Matcher<View> dialogConfirmSyncMatcher = allOf(withText(R.string.dialog_confirm_resources_sync_title), isDisplayed());
        onView(isRoot())
                .perform(waitUntil(dialogConfirmSyncMatcher, 5000));

        onView(withText(R.string.dialog_confirm_resources_sync_decline))
                .perform(click());
        onView(withText(R.string.dialog_confirm_resources_sync_title))
                .check(doesNotExist());
        assertIsMainActivity(true);
    }

    @Test
    public void testMainActivityWithNoData(){
        AppDatabase.getInstance(context).clearAllTables();
        activityScenario = ActivityScenario.launch(MainActivity.class);
        assertIsMainActivity(false);
    }
}
