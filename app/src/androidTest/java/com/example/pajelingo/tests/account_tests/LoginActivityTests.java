package com.example.pajelingo.tests.account_tests;

import static android.content.Context.MODE_PRIVATE;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasTextColor;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.CustomMatchers.getLabeledEditTextMatcher;
import static com.example.pajelingo.utils.CustomViewActions.fillLabeledEditText;
import static com.example.pajelingo.utils.CustomViewActions.waitUntil;
import static com.example.pajelingo.utils.RandomTools.getRandomAlphabeticalString;
import static com.example.pajelingo.utils.RandomTools.getRandomInteger;
import static com.example.pajelingo.utils.SharedPreferences.isUserAuthenticated;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.SharedPreferences;
import android.view.View;

import androidx.test.core.app.ActivityScenario;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MainActivity;
import com.example.pajelingo.tests.abstract_tests.UITests;

import org.hamcrest.Matcher;
import org.junit.Test;

public class LoginActivityTests extends UITests {
    @Test
    public void testRenderingLoginActivity(){
        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_login_logout)).perform(click());

        Matcher<View> usernameInputMatcher = getLabeledEditTextMatcher(R.string.username_label, "");
        Matcher<View> passwordInputMatcher = getLabeledEditTextMatcher(R.string.password_label, "");
        Matcher<View> loginButtonMatcher = hasDescendant(allOf(withText(R.string.login_button_text), isDisplayed()));

        onView(withId(R.id.username_input))
                .check(matches(usernameInputMatcher));
        onView(withId(R.id.password_input))
                .check(matches(passwordInputMatcher));
        onView(withId(R.id.login_button))
                .check(matches(loginButtonMatcher));

        Matcher<View> resetPasswordLinkMatcher = allOf(withText(R.string.reset_password_link), hasTextColor(R.color.raw_blue), isDisplayed());
        Matcher<View> orTextMatcher = allOf(withText(R.string.or), isDisplayed());
        Matcher<View> signupLinkMatcher = allOf(withText(R.string.signup_link), hasTextColor(R.color.raw_blue), isDisplayed());

        onView(withId(R.id.reset_password_link_text_view))
                .check(matches(resetPasswordLinkMatcher));
        onView(withId(R.id.or))
                .check(matches(orTextMatcher));
        onView(withId(R.id.signup_link_text_view))
                .check(matches(signupLinkMatcher));
    }

    @Test
    public void testLoginFailed() {
        String username = getRandomAlphabeticalString(getRandomInteger(1, 18));
        String password = getRandomAlphabeticalString(getRandomInteger(8, 30));

        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_login_logout)).perform(click());

        onView(withId(R.id.username_input))
                .perform(fillLabeledEditText(username), closeSoftKeyboard());
        onView(withId(R.id.password_input))
                .perform(fillLabeledEditText(password), closeSoftKeyboard());

        Matcher<View> loadingButtonMatcher = hasDescendant(allOf(withText(R.string.loading_button_text), isDisplayed()));
        onView(withId(R.id.login_button))
                .perform(click())
                .check(matches(loadingButtonMatcher));

        Matcher<View> loginButtonMatcher = allOf(withId(R.id.login_button), hasDescendant(withText(R.string.login_button_text)), isDisplayed());
        onView(isRoot())
                .perform(waitUntil(loginButtonMatcher, 5000));

        assertFalse(isUserAuthenticated(context));
    }

    @Test
    public void testLoginSuccessful(){
        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_login_logout)).perform(click());

        onView(withId(R.id.username_input))
                .perform(fillLabeledEditText(testUser.getUsername()), closeSoftKeyboard());
        onView(withId(R.id.password_input))
                .perform(fillLabeledEditText(testUser.getPassword()), closeSoftKeyboard());

        Matcher<View> loadingButtonMatcher = hasDescendant(allOf(withText(R.string.loading_button_text), isDisplayed()));
        onView(withId(R.id.login_button))
                .perform(click())
                .check(matches(loadingButtonMatcher));

        Matcher<View> loginButtonMatcher = allOf(withId(R.id.login_button), not(isDisplayed()));
        onView(isRoot())
                .perform(waitUntil(loginButtonMatcher, 10000));

        assertTrue(isUserAuthenticated(context));

        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.sp_file_name),MODE_PRIVATE);
        String username = sp.getString(context.getString(R.string.username_sp),null);
        String email = sp.getString(context.getString(R.string.email_sp),null);

        assertEquals(testUser.getUsername(), username);
        assertEquals(testUser.getEmail(), email);
        // Verify menu icons on app bar (ranking must be rendered after login)
        onView(withId(R.id.action_synchro))
                .check(matches(isDisplayed()));
        onView(withId(R.id.action_login_logout))
                .check(matches(isDisplayed()));
        onView(withId(R.id.action_menu))
                .check(matches(isDisplayed()));

        String greetingText = context.getString(R.string.greeting_text, testUser.getUsername());
        Matcher<View> greetingMatcher = allOf(withText(greetingText), isDisplayed());
        onView(withId(R.id.greeting_text_view))
                .check(matches(greetingMatcher));
    }
    
}
