package com.example.pajelingo.tests.account_tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.CustomMatchers.hasLabel;
import static com.example.pajelingo.utils.RandomTools.getRandomInteger;
import static com.example.pajelingo.utils.RandomTools.getRandomWord;
import static org.hamcrest.CoreMatchers.allOf;

import androidx.test.core.app.ActivityScenario;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MainActivity;
import com.example.pajelingo.tests.abstract_tests.FormUserActivityTests;

import org.junit.Test;

import java.io.IOException;

public class SignupActivityTests extends FormUserActivityTests {
    @Test
    public void testRenderingSignupActivity(){
        String emailLabel = context.getString(R.string.email_label);
        String usernameLabel = context.getString(R.string.username_label);
        String passwordLabel = context.getString(R.string.password_label);
        String passwordConfirmationLabel = context.getString(R.string.password_confirmation_label);

        browseToForm();

        onView(withId(R.id.email_input)).check(matches(isDisplayed())).check(matches(hasLabel(emailLabel)));
        onView(withId(R.id.username_input)).check(matches(isDisplayed())).check(matches(hasLabel(usernameLabel)));
        onView(withId(R.id.password_input)).check(matches(isDisplayed())).check(matches(hasLabel(passwordLabel)));
        onView(withId(R.id.password_confirmation_input)).check(matches(isDisplayed())).check(matches(hasLabel(passwordConfirmationLabel)));

        assertPasswordRequirements(false, false, false, false);

        onView(allOf(withId(R.id.submit_button), withText(R.string.signup_button_text))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.login_link_text_view), withText(R.string.login_link))).check(matches(isDisplayed()));
    }

    @Test
    public void testSuccessfulSignup() throws IOException, InterruptedException {
        testSuccessfulSubmission(testUser.getEmail(), testUser.getUsername(), testUser.getPassword());
    }

    @Test
    public void testFailedSignupWithoutEmail() throws IOException, InterruptedException {
        testFailedSubmissionWithValidPassword("", testUser.getUsername(), true);
    }

    @Test
    public void testFailedSignupWithoutUsername() throws IOException, InterruptedException {
        testFailedSubmissionWithValidPassword(testUser.getEmail(), "", true);
    }

    @Test
    public void testFailedSignupWithoutPassword() throws IOException, InterruptedException {
        testFailedSubmissionWithInvalidPassword(0, false, false, false);
    }

    @Test
    public void testFailedSignupEmailWithSpace() throws IOException, InterruptedException {
        String email = getRandomWord(getRandomInteger(1, 5)) + " " + testUser.getEmail();
        String username = testUser.getUsername();
        testFailedSubmissionWithValidPassword(email, username, true);
    }

    @Test
    public void testFailedSignupUsernameWithSpace() throws IOException, InterruptedException {
        String email = testUser.getEmail();
        String username = getRandomWord(getRandomInteger(1, 5)) + " " + testUser.getUsername();
        testFailedSubmissionWithValidPassword(email, username, true);
    }

    @Test
    public void testFailedSignupNonAvailableEmail() throws IOException, InterruptedException {
        testFailedSubmissionWithValidPassword(existingUser.getEmail(), testUser.getUsername(), true);
    }

    @Test
    public void testFailedSignupNonAvailableUsername() throws IOException, InterruptedException {
        testFailedSubmissionWithValidPassword(testUser.getEmail(), existingUser.getUsername(), true);
    }

    @Test
    public void testFailedSignupWith3CharactersPassword() throws IOException, InterruptedException {
        testFailedSubmissionWithInvalidPassword(3, true, true, true);
    }

    @Test
    public void testFailedSignupWith4CharactersPassword() throws IOException, InterruptedException {
        testFailedSubmissionWithInvalidPassword(4, true, true, true);
    }

    @Test
    public void testFailedSignupWith5CharactersPassword() throws IOException, InterruptedException {
        testFailedSubmissionWithInvalidPassword(5, true, true, true);
    }

    @Test
    public void testFailedSignupWith6CharactersPassword() throws IOException, InterruptedException {
        testFailedSubmissionWithInvalidPassword(6, true, true, true);
    }

    @Test
    public void testFailedSignupWith7CharactersPassword() throws IOException, InterruptedException {
        testFailedSubmissionWithInvalidPassword(7,true, true, true);
    }

    @Test
    public void testFailedSignupWith31CharactersPassword() throws IOException, InterruptedException {
        testFailedSubmissionWithInvalidPassword(31,true, true, true);
    }

    @Test
    public void testFailedSignupNoDigit() throws IOException, InterruptedException {
        testFailedSubmissionWithInvalidPassword(getRandomInteger(8, 30),true, false, true);
    }

    @Test
    public void testFailedSignupNoSpecialCharacter() throws IOException, InterruptedException {
        testFailedSubmissionWithInvalidPassword(getRandomInteger(8, 30), true, true, false);
    }

    @Test
    public void testFailedSignupNoLetter() throws IOException, InterruptedException {
        testFailedSubmissionWithInvalidPassword(getRandomInteger(8, 30),false, true, true);
    }

    @Test
    public void testFailedSignupPasswordsDoNotMatch() throws IOException, InterruptedException {
        testFailedSubmissionWithValidPassword(testUser.getEmail(), testUser.getUsername(), false);
    }

    @Override
    protected void browseToForm() {
        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_login_logout)).perform(click());
        onView(withId(R.id.signup_link_text_view)).perform(click());
    }
}
