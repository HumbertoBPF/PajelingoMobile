package com.example.pajelingo.tests.account_tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.CustomMatchers.hasInput;
import static com.example.pajelingo.utils.CustomMatchers.hasLabel;
import static com.example.pajelingo.utils.RandomTools.getRandomEmail;
import static com.example.pajelingo.utils.RandomTools.getRandomInteger;
import static com.example.pajelingo.utils.RandomTools.getRandomPassword;
import static com.example.pajelingo.utils.RandomTools.getRandomUsername;
import static com.example.pajelingo.utils.RandomTools.getRandomWord;
import static com.example.pajelingo.utils.Tools.getAuthToken;
import static com.example.pajelingo.utils.Tools.saveStateAndUserCredentials;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.not;

import androidx.test.core.app.ActivityScenario;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MainActivity;
import com.example.pajelingo.tests.abstract_tests.FormUserActivityTests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class UpdateAccountTests extends FormUserActivityTests {
    @Before
    public void setUp() throws IOException {
        context.deleteSharedPreferences(context.getString(R.string.sp_file_name));
        super.setUp();
        languageSchoolAPI.signup(testUser).execute();
        saveStateAndUserCredentials(context, testUser);
    }

    @Override
    protected void browseToForm() {
        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_menu)).perform(click());
        onView(withId(R.id.menu_item_profile)).perform(click());
        onView(withId(R.id.edit_account_button)).perform(click());
    }

    @Test
    public void testRenderingAccountUpdateForm(){
        String emailLabel = context.getString(R.string.email_label);
        String usernameLabel = context.getString(R.string.username_label);
        String passwordLabel = context.getString(R.string.password_label);
        String passwordConfirmationLabel = context.getString(R.string.password_confirmation_label);

        browseToForm();

        onView(withId(R.id.email_input)).check(matches(isDisplayed()))
                .check(matches(hasLabel(emailLabel)))
                .check(matches(hasInput(testUser.getEmail())));
        onView(withId(R.id.username_input)).check(matches(isDisplayed()))
                .check(matches(hasLabel(usernameLabel)))
                .check(matches(hasInput(testUser.getUsername())));
        onView(withId(R.id.password_input)).check(matches(isDisplayed())).check(matches(hasLabel(passwordLabel)));
        onView(withId(R.id.password_confirmation_input)).check(matches(isDisplayed())).check(matches(hasLabel(passwordConfirmationLabel)));

        assertPasswordRequirements(false, false, false, false);

        onView(allOf(withId(R.id.submit_button), withText(R.string.update_account_button_text))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.login_link_text_view), withText(R.string.login_link))).check(matches(not(isDisplayed())));
    }

    @Test
    public void testSuccessfulAccountUpdate() throws IOException, InterruptedException {
        testSuccessfulSubmission(getRandomEmail(), getRandomUsername(), getRandomPassword());
    }

    @Test
    public void testSuccessfulAccountUpdateWithTheSameEmail() throws IOException, InterruptedException {
        testSuccessfulSubmission(testUser.getEmail(), getRandomUsername(), getRandomPassword());
    }

    @Test
    public void testSuccessfulAccountUpdateWithTheSameUsername() throws IOException, InterruptedException {
        testSuccessfulSubmission(getRandomEmail(), testUser.getUsername(), getRandomPassword());
    }

    @Test
    public void testSuccessfulAccountUpdateWithTheSameEmailAndUsername() throws IOException, InterruptedException {
        testSuccessfulSubmission(testUser.getEmail(), testUser.getUsername(), getRandomPassword());
    }

    @Test
    public void testFailedAccountUpdateWithoutEmail() throws IOException, InterruptedException {
        testFailedSubmissionWithValidPassword("", getRandomUsername(), true);
    }

    @Test
    public void testFailedAccountUpdateWithoutUsername() throws IOException, InterruptedException {
        testFailedSubmissionWithValidPassword(getRandomEmail(), "", true);
    }

    @Test
    public void testFailedAccountUpdateWithoutPassword() throws IOException, InterruptedException {
        testFailedSubmissionWithInvalidPassword(0, false, false, false);
    }

    @Test
    public void testFailedAccountUpdateEmailWithSpace() throws IOException, InterruptedException {
        String email = getRandomWord(getRandomInteger(1, 5)) + " " + getRandomEmail();
        String username = getRandomUsername();
        testFailedSubmissionWithValidPassword(email, username, true);
    }

    @Test
    public void testFailedAccountUpdateUsernameWithSpace() throws IOException, InterruptedException {
        String email = getRandomEmail();
        String username = getRandomWord(getRandomInteger(1, 5)) + " " + getRandomUsername();
        testFailedSubmissionWithValidPassword(email, username, true);
    }

    @Test
    public void testFailedAccountUpdateNonAvailableEmail() throws IOException, InterruptedException {
        testFailedSubmissionWithValidPassword(existingUser.getEmail(), getRandomUsername(), true);
    }

    @Test
    public void testFailedAccountUpdateNonAvailableUsername() throws IOException, InterruptedException {
        testFailedSubmissionWithValidPassword(getRandomEmail(), existingUser.getUsername(), true);
    }

    @Test
    public void testFailedAccountUpdateWith3CharactersPassword() throws IOException, InterruptedException {
        testFailedSubmissionWithInvalidPassword(3, true, true, true);
    }

    @Test
    public void testFailedAccountUpdateWith4CharactersPassword() throws IOException, InterruptedException {
        testFailedSubmissionWithInvalidPassword(4, true, true, true);
    }

    @Test
    public void testFailedAccountUpdateWith5CharactersPassword() throws IOException, InterruptedException {
        testFailedSubmissionWithInvalidPassword(5, true, true, true);
    }

    @Test
    public void testFailedAccountUpdateWith6CharactersPassword() throws IOException, InterruptedException {
        testFailedSubmissionWithInvalidPassword(6, true, true, true);
    }

    @Test
    public void testFailedAccountUpdateWith7CharactersPassword() throws IOException, InterruptedException {
        testFailedSubmissionWithInvalidPassword(7,true, true, true);
    }

    @Test
    public void testFailedAccountUpdateWith31CharactersPassword() throws IOException, InterruptedException {
        testFailedSubmissionWithInvalidPassword(31,true, true, true);
    }

    @Test
    public void testFailedAccountUpdateNoDigit() throws IOException, InterruptedException {
        testFailedSubmissionWithInvalidPassword(getRandomInteger(8, 30),true, false, true);
    }

    @Test
    public void testFailedAccountUpdateNoSpecialCharacter() throws IOException, InterruptedException {
        testFailedSubmissionWithInvalidPassword(getRandomInteger(8, 30), true, true, false);
    }

    @Test
    public void testFailedAccountUpdateNoLetter() throws IOException, InterruptedException {
        testFailedSubmissionWithInvalidPassword(getRandomInteger(8, 30),false, true, true);
    }

    @Test
    public void testFailedAccountUpdatePasswordsDoNotMatch() throws IOException, InterruptedException {
        testFailedSubmissionWithValidPassword(getRandomEmail(), getRandomUsername(), false);
    }

    @After
    public void tearDown() throws IOException {
        super.tearDown();
        languageSchoolAPI.deleteAccount(getAuthToken(testUser.getUsername(), testUser.getPassword())).execute();
        context.deleteSharedPreferences(context.getString(R.string.sp_file_name));
    }
}
