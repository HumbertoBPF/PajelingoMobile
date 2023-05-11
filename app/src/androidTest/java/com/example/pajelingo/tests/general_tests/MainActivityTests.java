package com.example.pajelingo.tests.general_tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingPolicies;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MainActivity;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.tests.abstract_tests.UITests;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class MainActivityTests extends UITests {

    @Test
    public void testRenderingMainActivity(){
        activityScenario = ActivityScenario.launch(MainActivity.class);
        assertIsMainActivity(true);
    }

    @Test
    public void testSync() {
        activityScenario = ActivityScenario.launch(MainActivity.class);
        IdlingPolicies.setIdlingResourceTimeout(3, TimeUnit.MINUTES);

        onView(withId(R.id.action_synchro))
                .perform(click());

        onView(withText(R.string.dialog_confirm_resources_sync_title))
                .check(matches(isDisplayed()));
        onView(withText(R.string.dialog_confirm_resources_sync_message))
                .check(matches(isDisplayed()));
        onView(withText(R.string.dialog_confirm_resources_sync_confirm))
                .check(matches(isDisplayed()));
        onView(withText(R.string.dialog_confirm_resources_sync_decline))
                .check(matches(isDisplayed()));

        onView(withText(R.string.dialog_confirm_resources_sync_confirm))
                .perform(click());

        onView(withText(R.string.dialog_confirm_resources_sync_title))
                .check(doesNotExist());
    }

    @Test
    public void testDeclineSyncConfirmationDialog(){
        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_synchro))
                .perform(click());

        onView(withText(R.string.dialog_confirm_resources_sync_title))
                .check(matches(isDisplayed()));
        onView(withText(R.string.dialog_confirm_resources_sync_message))
                .check(matches(isDisplayed()));
        onView(withText(R.string.dialog_confirm_resources_sync_confirm))
                .check(matches(isDisplayed()));
        onView(withText(R.string.dialog_confirm_resources_sync_decline))
                .check(matches(isDisplayed()));

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
