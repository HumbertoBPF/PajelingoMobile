package com.example.pajelingo.tests.general_tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.example.pajelingo.utils.TestTools.authenticateUser;
import static com.example.pajelingo.utils.Tools.saveStateAndUserCredentials;
import static org.hamcrest.Matchers.not;

import androidx.test.core.app.ActivityScenario;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MainActivity;
import com.example.pajelingo.tests.abstract_tests.UITests;

import org.junit.Test;

import java.io.IOException;

public class MenuActivityTests extends UITests {
    @Test
    public void testRenderingMenuForAuthenticatedUser() throws IOException {
        authenticateUser(context, testUser);
        assertMenu(true);
    }

    @Test
    public void testRenderingMenuForNonAuthenticatedUser() {
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
        }else{
            onView(withId(R.id.menu_item_profile)).check(matches(not(isDisplayed())));
        }
        onView(withId(R.id.menu_item_faq)).check(matches(isDisplayed()));
        onView(withId(R.id.menu_item_rankings)).check(matches(isDisplayed()));
    }
}
