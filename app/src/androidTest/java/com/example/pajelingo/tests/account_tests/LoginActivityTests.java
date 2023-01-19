package com.example.pajelingo.tests.account_tests;

import static android.content.Context.MODE_PRIVATE;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasTextColor;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.CustomMatchers.hasLabel;
import static com.example.pajelingo.utils.CustomViewActions.fillLabeledEditText;
import static com.example.pajelingo.utils.CustomViewActions.waitForView;
import static com.example.pajelingo.utils.RandomTools.getRandomInteger;
import static com.example.pajelingo.utils.RandomTools.getRandomWord;
import static com.example.pajelingo.utils.Tools.isUserAuthenticated;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MainActivity;
import com.example.pajelingo.tests.abstract_tests.UITests;

import org.junit.Test;

public class LoginActivityTests extends UITests {
    @Test
    public void testRenderingLoginActivity(){
        String usernameLabel = context.getString(R.string.username_label);
        String passwordLabel = context.getString(R.string.password_label);
        String buttonLoginText = context.getString(R.string.login_button_text);

        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_login_logout)).perform(click());

        onView(withId(R.id.username_input)).check(matches(isDisplayed())).check(matches(hasLabel(usernameLabel)));
        onView(withId(R.id.password_input)).check(matches(isDisplayed())).check(matches(hasLabel(passwordLabel)));
        onView(allOf(withId(R.id.login_button), withText(buttonLoginText))).check(matches(isDisplayed()));

        onView(allOf(withId(R.id.reset_password_link_text_view), withText(context.getString(R.string.reset_password_link))))
                .check(matches(isDisplayed()))
                .check(matches(hasTextColor(R.color.raw_blue)));
        onView(allOf(withId(R.id.or), withText(context.getString(R.string.or))))
                .check(matches(isDisplayed()));
        onView(allOf(withId(R.id.signup_link_text_view), withText(context.getString(R.string.signup_link))))
                .check(matches(isDisplayed())).check(matches(hasTextColor(R.color.raw_blue)));;
    }

    @Test
    public void testLoginFailed(){
        String username = getRandomWord(getRandomInteger(1, 18));
        String password = getRandomWord(getRandomInteger(8, 30));

        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_login_logout)).perform(click());

        onView(withId(R.id.username_input)).perform(fillLabeledEditText(username), closeSoftKeyboard());
        onView(withId(R.id.password_input)).perform(fillLabeledEditText(password), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        onView(isRoot()).perform(waitForView(withText((R.string.login_dialog_title)), 5000, true));
        onView(isRoot()).perform(waitForView(withText(R.string.login_dialog_title), 30000, false));

        assertFalse(isUserAuthenticated(context));
    }

    @Test
    public void testLoginSuccessful(){
        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_login_logout)).perform(click());

        onView(withId(R.id.username_input)).perform(fillLabeledEditText(testUser.getUsername()), closeSoftKeyboard());
        onView(withId(R.id.password_input)).perform(fillLabeledEditText(testUser.getPassword()), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        onView(isRoot()).perform(waitForView(withText(R.string.login_dialog_title), 5000, true));
        onView(isRoot()).perform(waitForView(withText(R.string.login_dialog_title), 30000, false));

        assertTrue(isUserAuthenticated(context));

        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.sp_file_name),MODE_PRIVATE);
        String username = sp.getString(context.getString(R.string.username_sp),null);
        String email = sp.getString(context.getString(R.string.email_sp),null);

        assertEquals(testUser.getUsername(), username);
        assertEquals(testUser.getEmail(), email);
        // Verify menu icons on app bar (ranking must be rendered after login)
        onView(withId(R.id.action_synchro)).check(matches(isDisplayed()));
        onView(withId(R.id.action_login_logout)).check(matches(isDisplayed()));
        onView(withId(R.id.action_menu)).check(matches(isDisplayed()));
    }
    
}
