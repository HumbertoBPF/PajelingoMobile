package com.example.pajelingo.tests.account_tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.CustomMatchers.getLabeledEditTextMatcher;
import static com.example.pajelingo.utils.RandomTools.getRandomAlphabeticalString;
import static com.example.pajelingo.utils.RandomTools.getRandomEmail;
import static com.example.pajelingo.utils.RandomTools.getRandomInteger;
import static com.example.pajelingo.utils.RandomTools.getRandomInvalidPassword;
import static com.example.pajelingo.utils.RandomTools.getRandomUsername;
import static com.example.pajelingo.utils.RandomTools.getRandomValidPassword;
import static com.example.pajelingo.utils.RetrofitTools.assertUserExistsInDjangoApp;
import static org.hamcrest.CoreMatchers.allOf;

import android.view.View;

import androidx.test.core.app.ActivityScenario;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MainActivity;
import com.example.pajelingo.models.User;
import com.example.pajelingo.tests.abstract_tests.FormUserActivityTests;

import org.hamcrest.Matcher;
import org.junit.Test;

import java.io.IOException;

public class SignupActivityTests extends FormUserActivityTests {
    private final User newUser = new User("new-test-android@test.com", "new-test-android", "new-str0ng-p4ssw0rd", null);

    @Test
    public void testRenderingSignupActivity(){
        browseToForm();

        Matcher<View> emailInputMatcher = getLabeledEditTextMatcher(R.string.email_label, "");
        onView(withId(R.id.email_input))
                .check(matches(emailInputMatcher));

        Matcher<View> usernameInputMatcher = getLabeledEditTextMatcher(R.string.username_label, "");
        onView(withId(R.id.username_input))
                .check(matches(usernameInputMatcher));

        Matcher<View> passwordInputMatcher = getLabeledEditTextMatcher(R.string.password_label, "");
        onView(withId(R.id.password_input))
                .check(matches(passwordInputMatcher));

        Matcher<View> passwordConfirmationInputMatcher = getLabeledEditTextMatcher(R.string.password_confirmation_label, "");
        onView(withId(R.id.password_confirmation_input))
                .check(matches(passwordConfirmationInputMatcher));

        assertPasswordRequirements(false, false, false, false);

        Matcher<View> submitButtonMatcher = allOf(hasDescendant(withText(R.string.signup_button_text)), isDisplayed());
        onView(withId(R.id.submit_button))
                .check(matches(submitButtonMatcher));

        Matcher<View> loginLinkMatcher = allOf(withText(R.string.login_link), isDisplayed());
        onView(withId(R.id.login_link_text_view))
                .check(matches(loginLinkMatcher));
    }

    @Test
    public void testSuccessfulSignup() throws IOException {
        String email = newUser.getEmail();
        String username = newUser.getUsername();
        String password = newUser.getPassword();

        testSuccessfulSubmission(email, username, password);
        // The user must not be active since the account has not been activated yet
        assertUserExistsInDjangoApp(email, username, password, false);
    }

    @Test
    public void testFailedSignupWithoutEmail() throws IOException {
        testInvalidSubmission("", getRandomUsername(), getRandomValidPassword(), true,
                true, true, true, true);
    }

    @Test
    public void testFailedSignupWithoutUsername() throws IOException {
        testInvalidSubmission(getRandomEmail(), "", getRandomValidPassword(), true,
                true, true, true, true);
    }

    @Test
    public void testFailedSignupWithoutPassword() throws IOException {
        testInvalidSubmission(getRandomEmail(), getRandomUsername(), "", true,
                false, false, false, false);
    }

    @Test
    public void testFailedSignupEmailWithSpace() throws IOException {
        testInvalidSubmission(getRandomAlphabeticalString(getRandomInteger(1, 5)) + " " + getRandomEmail(),
                getRandomUsername(), getRandomValidPassword(), true, true, true, true, true);
    }

    @Test
    public void testFailedSignupUsernameWithSpace() throws IOException {
        testInvalidSubmission(getRandomEmail(), getRandomAlphabeticalString(getRandomInteger(1, 5)) + " " + getRandomUsername(),
                getRandomValidPassword(), true, true, true, true, true);
    }

    @Test
    public void testFailedSignupNonAvailableEmail() throws IOException {
        testInvalidSubmission(testUser.getEmail(), getRandomUsername(), getRandomValidPassword(), true,
                true, true, true, true);
    }

    @Test
    public void testFailedSignupNonAvailableUsername() throws IOException {
        testInvalidSubmission(getRandomEmail(), testUser.getUsername(), getRandomValidPassword(), true,
                true, true, true, true);
    }

    @Test
    public void testFailedSignupWith3CharactersPassword() throws IOException {
        testInvalidSubmission(getRandomEmail(), getRandomUsername(),
                getRandomInvalidPassword(3, true, true, true), true,
                true, true, true, false);
    }

    @Test
    public void testFailedSignupWith4CharactersPassword() throws IOException {
        testInvalidSubmission(getRandomEmail(), getRandomUsername(),
                getRandomInvalidPassword(4, true, true, true), true,
                true, true, true, false);
    }

    @Test
    public void testFailedSignupWith5CharactersPassword() throws IOException {
        testInvalidSubmission(getRandomEmail(), getRandomUsername(),
                getRandomInvalidPassword(5, true, true, true), true,
                true, true, true, false);
    }

    @Test
    public void testFailedSignupWith6CharactersPassword() throws IOException {
        testInvalidSubmission(getRandomEmail(), getRandomUsername(),
                getRandomInvalidPassword(6, true, true, true), true,
                true, true, true, false);
    }

    @Test
    public void testFailedSignupWith7CharactersPassword() throws IOException {
        testInvalidSubmission(getRandomEmail(), getRandomUsername(),
                getRandomInvalidPassword(7, true, true, true), true,
                true, true, true, false);
    }

    @Test
    public void testFailedSignupWith31CharactersPassword() throws IOException {
        testInvalidSubmission(getRandomEmail(), getRandomUsername(),
                getRandomInvalidPassword(31, true, true, true), true,
                true, true, true, false);
    }

    @Test
    public void testFailedSignupNoDigit() throws IOException {
        testInvalidSubmission(getRandomEmail(), getRandomUsername(),
                getRandomInvalidPassword(getRandomInteger(8, 30), true, false, true), true,
                true, false, true, true);
    }

    @Test
    public void testFailedSignupNoSpecialCharacter() throws IOException {
        testInvalidSubmission(getRandomEmail(), getRandomUsername(),
                getRandomInvalidPassword(getRandomInteger(8, 30), true, true, false), true,
                true, true, false, true);
    }

    @Test
    public void testFailedSignupNoLetter() throws IOException {
        testInvalidSubmission(getRandomEmail(), getRandomUsername(),
                getRandomInvalidPassword(getRandomInteger(8, 30), false, true, true), true,
                false, true, true, true);
    }

    @Test
    public void testFailedSignupPasswordsDoNotMatch() throws IOException {
        testInvalidSubmission(getRandomEmail(), getRandomUsername(), getRandomValidPassword(), false,
                true, true, true, true);
    }

    @Override
    protected void browseToForm() {
        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_login_logout))
                .perform(click());
        onView(withId(R.id.signup_link_text_view))
                .perform(click());
    }
}
