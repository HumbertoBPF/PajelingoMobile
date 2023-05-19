package com.example.pajelingo.tests.account_tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.CustomMatchers.hasLength;
import static com.example.pajelingo.utils.RandomTools.getRandomAlphabeticalString;
import static com.example.pajelingo.utils.RetrofitTools.getAccountsPage;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsStringIgnoringCase;

import android.view.View;

import androidx.test.core.app.ActivityScenario;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MainActivity;
import com.example.pajelingo.models.Page;
import com.example.pajelingo.models.User;
import com.example.pajelingo.tests.abstract_tests.UITests;

import org.hamcrest.Matcher;
import org.junit.Test;

import java.io.IOException;

public class SearchAccountActivityTests extends UITests {

    @Test
    public void testSearchAccountActivityRendering() {
        browseToActivity();

        Matcher<View> searchAccountEditTextMatcher = allOf(
                withHint(R.string.hint_edit_text_account_search),
                isDisplayed()
        );
        Matcher<View> searchAccountButtonMatcher = allOf(
                allOf(hasDescendant(withText(R.string.search_account_button)), isDisplayed())
        );

        onView(withId(R.id.search_account_edit_text))
                .check(matches(searchAccountEditTextMatcher));
        onView(withId(R.id.search_account_button))
                .check(matches(searchAccountButtonMatcher));
        onView(withId(R.id.accounts_recycler_view))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testSearchAccountActivityAllAccounts() throws IOException {
        Page<User> accountsPage = getAccountsPage("", 1);
        int numberOfAccounts = accountsPage.getCount();

        browseToActivity();

        onView(withId(R.id.search_account_button))
                .perform(click());

        assertAccounts("", numberOfAccounts);
    }

    @Test
    public void testSearchAccountActivityWithFilter() throws IOException {
        String q = getRandomAlphabeticalString(1);
        Page<User> accountsPage = getAccountsPage(q, 1);
        int numberOfAccounts = accountsPage.getCount();

        browseToActivity();

        onView(withId(R.id.search_account_edit_text))
                .perform(typeText(q), closeSoftKeyboard());
        onView(withId(R.id.search_account_button))
                .perform(click());

        assertAccounts(q, numberOfAccounts);
    }

    public void assertAccounts(String q, int numberOfAccounts) {
        int numberOfPages = 0;

        if (numberOfAccounts > 0) {
            numberOfPages = (int) Math.ceil(((float) numberOfAccounts)/10f);
        }

        for (int i=1;i < numberOfPages;i++) {
            onView(withId(R.id.accounts_recycler_view))
                    .perform(scrollToPosition(10*i - 1));
        }

        onView(withId(R.id.accounts_recycler_view))
                .check(matches(hasLength(numberOfAccounts)));

        for (int i=0;i < numberOfAccounts;i++) {
            Matcher<View> matcherItem = hasDescendant(
                    allOf(
                            withId(R.id.account_text_view),
                            withText(containsStringIgnoringCase(q))
                    )
            );

            onView(withId(R.id.accounts_recycler_view))
                    .perform(scrollToPosition(i))
                    .check(matches(matcherItem));
        }
    }

    private void browseToActivity() {
        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_menu))
                .perform(click());
        onView(withId(R.id.menu_recycler_view))
                .perform(actionOnItemAtPosition(0, click()));
    }
}
