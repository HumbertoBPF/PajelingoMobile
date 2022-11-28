package com.example.pajelingo.tests.general_tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.example.pajelingo.utils.Tools.getAuthToken;
import static com.example.pajelingo.utils.Tools.saveStateAndUserCredentials;

import androidx.test.core.app.ActivityScenario;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MainActivity;
import com.example.pajelingo.tests.abstract_tests.UITests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class MenuActivityTests extends UITests {
    @Before
    public void setUp() throws IOException {
        languageSchoolAPI.deleteAccount(getAuthToken(testUser.getUsername(), testUser.getPassword())).execute();
        languageSchoolAPI.signup(testUser).execute();
        saveStateAndUserCredentials(context, testUser.getUsername(), testUser.getEmail(), testUser.getPassword());
    }

    @Test
    public void testRenderingMenuForAuthenticatedUser(){
        saveStateAndUserCredentials(context, testUser.getUsername(), testUser.getEmail(), testUser.getPassword());

        assertMenu(true);
    }

    @Test
    public void testRenderingMenuForNonAuthenticatedUser(){
        assertMenu(false);
    }

    /**
     * Asserts if the lateral menu is displayed correctly.
     * @param isAuthenticated boolean indicating if the user is authenticated.
     */
    private void assertMenu(boolean isAuthenticated) {
        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_menu)).perform(click());

        if (isAuthenticated){
            onView(withId(R.id.menu_item_profile)).check(matches(isDisplayed()));
        }
        onView(withId(R.id.menu_item_faq)).check(matches(isDisplayed()));
        onView(withId(R.id.menu_item_rankings)).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() throws IOException {
        languageSchoolAPI.deleteAccount(getAuthToken(testUser.getUsername(), testUser.getPassword())).execute();
    }
}
