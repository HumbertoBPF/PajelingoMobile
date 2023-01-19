package com.example.pajelingo.tests.account_tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.CustomViewActions.waitForView;
import static com.example.pajelingo.utils.RandomTools.getRandomEmail;
import static com.example.pajelingo.utils.RandomTools.getRandomInteger;
import static com.example.pajelingo.utils.RandomTools.getRandomString;
import static org.hamcrest.Matchers.allOf;

import androidx.test.core.app.ActivityScenario;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MainActivity;
import com.example.pajelingo.tests.abstract_tests.UITests;

import org.junit.Test;

public class RequestResetPasswordActivityTests extends UITests {

    @Test
    public void testRequestResetPasswordActivityRendering(){
        browseToRequestResetPasswordActivity();
        assertRequestResetPasswordActivityViews();
    }

    @Test
    public void testRequestResetPasswordWithoutEmailInput(){
        browseToRequestResetPasswordActivity();

        onView(withId(R.id.reset_password_button)).perform(click());
        assertRequestResetPasswordActivityViews();
    }

    @Test
    public void testRequestResetPasswordWithWrongTypeEmailInput(){
        browseToRequestResetPasswordActivity();

        String randomString = getRandomString(getRandomInteger(1, 10), true, true, false);
        onView(withId(R.id.email_edit_text)).perform(typeText(randomString), closeSoftKeyboard());
        onView(withId(R.id.reset_password_button)).perform(click());
        assertRequestResetPasswordActivityViews();
    }

    @Test
    public void testRequestResetPasswordWithValidEmailInput(){
        browseToRequestResetPasswordActivity();

        String randomEmail = getRandomEmail();
        onView(withId(R.id.email_edit_text)).perform(typeText(randomEmail), closeSoftKeyboard());
        onView(withId(R.id.reset_password_button)).perform(click());

        onView(isRoot()).perform(waitForView(withText(R.string.login_dialog_title), 5000, true));
        onView(isRoot()).perform(waitForView(withText(R.string.login_dialog_title), 30000, false));
        // Check that the feedback dialog is displayed and dismiss it
        onView(withText(R.string.reset_password_feedback_dialog_title)).check(matches(isDisplayed()));
        onView(withText(R.string.reset_password_feedback_dialog_message)).check(matches(isDisplayed()));
        onView(withText(R.string.reset_password_positive_button_text)).perform(click());
        // Check if user is back to MenuActivity
        assertIsMainActivity(true);
    }

    private void browseToRequestResetPasswordActivity() {
        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_login_logout)).perform(click());
        onView(withId(R.id.reset_password_link_text_view)).perform(click());
    }

    private void assertRequestResetPasswordActivityViews() {
        onView(allOf(withId(R.id.reset_password_instructions_text_view), withText(R.string.reset_password_instructions)))
                .check(matches(isDisplayed()));
        onView(withId(R.id.email_edit_text)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.reset_password_button), withText(R.string.reset_button_text))).check(matches(isDisplayed()));
    }

}
