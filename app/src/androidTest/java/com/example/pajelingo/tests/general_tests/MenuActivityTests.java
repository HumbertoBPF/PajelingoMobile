package com.example.pajelingo.tests.general_tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.CustomMatchers.atPosition;
import static com.example.pajelingo.utils.TestTools.authenticateUser;
import static org.hamcrest.Matchers.allOf;

import android.view.View;

import androidx.test.core.app.ActivityScenario;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MainActivity;
import com.example.pajelingo.tests.abstract_tests.UITests;

import org.hamcrest.Matcher;
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

        onView(withId(R.id.action_menu))
                .perform(click());

        int expectedNumberOfItems = isAuthenticated?4:3;

        Matcher<View> profileMenuItemMatcher = allOf(hasDescendant(withText(R.string.profile_menu_item_title)), isDisplayed());
        Matcher<View> searchAccountMenuItemMatcher = allOf(hasDescendant(withText(R.string.search_account_menu_item_title)), isDisplayed());
        Matcher<View> rankingMenuItemMatcher = allOf(hasDescendant(withText(R.string.rankings_menu_item_title)), isDisplayed());
        Matcher<View> faqMenuItemMatcher = allOf(hasDescendant(withText(R.string.faq_menu_item_title)), isDisplayed());

        if (isAuthenticated){
            onView(withId(R.id.menu_recycler_view))
                    .check(matches(atPosition(profileMenuItemMatcher, 0)));
        }

        onView(withId(R.id.menu_recycler_view))
                .check(matches(atPosition(searchAccountMenuItemMatcher, expectedNumberOfItems-3)));
        onView(withId(R.id.menu_recycler_view))
                .check(matches(atPosition(rankingMenuItemMatcher, expectedNumberOfItems-2)));
        onView(withId(R.id.menu_recycler_view))
                .check(matches(atPosition(faqMenuItemMatcher, expectedNumberOfItems-1)));
    }
}
