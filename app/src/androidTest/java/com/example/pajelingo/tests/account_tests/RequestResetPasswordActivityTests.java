package com.example.pajelingo.tests.account_tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.RandomTools.getRandomEmail;
import static com.example.pajelingo.utils.RandomTools.getRandomInteger;
import static com.example.pajelingo.utils.RandomTools.getRandomString;
import static org.hamcrest.Matchers.allOf;

import android.view.View;

import androidx.test.core.app.ActivityScenario;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MainActivity;
import com.example.pajelingo.tests.abstract_tests.UITests;

import org.hamcrest.Matcher;
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

        onView(withId(R.id.reset_password_button))
                .perform(click());
        assertRequestResetPasswordActivityViews();
    }

    @Test
    public void testRequestResetPasswordWithWrongTypeEmailInput(){
        browseToRequestResetPasswordActivity();

        String randomString = getRandomString(getRandomInteger(1, 10), true, true, false);
        onView(withId(R.id.email_edit_text))
                .perform(typeText(randomString), closeSoftKeyboard());
        onView(withId(R.id.reset_password_button))
                .perform(click());
        assertRequestResetPasswordActivityViews();
    }

    @Test
    public void testRequestResetPasswordWithValidEmailInput() {
        browseToRequestResetPasswordActivity();

        String randomEmail = getRandomEmail();
        onView(withId(R.id.email_edit_text))
                .perform(typeText(randomEmail), closeSoftKeyboard());
        onView(withId(R.id.reset_password_button))
                .perform(click());
        // Check that the feedback dialog is displayed and dismiss it
        onView(withText(R.string.reset_password_feedback_dialog_title))
                .check(matches(isDisplayed()));
        onView(withText(R.string.reset_password_feedback_dialog_message))
                .check(matches(isDisplayed()));
        onView(withText(R.string.reset_password_positive_button_text))
                .perform(click());
        // Check if user is back to MenuActivity
        assertIsMainActivity(true);
    }

    private void browseToRequestResetPasswordActivity() {
        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_login_logout))
                .perform(click());
        onView(withId(R.id.reset_password_link_text_view))
                .perform(click());
    }

    private void assertRequestResetPasswordActivityViews() {
        Matcher<View> resetPasswordInstructionsMatcher = allOf(withText(R.string.reset_password_instructions), isDisplayed());
        onView(withId(R.id.reset_password_instructions_text_view))
                .check(matches(resetPasswordInstructionsMatcher));

        Matcher<View> emailEditTextMatcher = allOf(withHint(R.string.your_email_hint), isDisplayed());
        onView(withId(R.id.email_edit_text))
                .check(matches(emailEditTextMatcher));

        Matcher<View> resetPasswordButton = allOf(hasDescendant(withText(R.string.reset_button_text)), isDisplayed());
        onView(withId(R.id.reset_password_button))
                .check(matches(resetPasswordButton));
    }

}
