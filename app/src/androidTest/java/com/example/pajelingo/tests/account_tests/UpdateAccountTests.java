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
import static com.example.pajelingo.utils.RandomTools.getRandomInvalidPassword;
import static com.example.pajelingo.utils.RandomTools.getRandomUsername;
import static com.example.pajelingo.utils.RandomTools.getRandomValidPassword;
import static com.example.pajelingo.utils.RandomTools.getRandomWord;
import static com.example.pajelingo.utils.RetrofitTools.assertUserExistsInDjangoApp;
import static com.example.pajelingo.utils.Tools.saveStateAndUserCredentials;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.not;

import androidx.test.core.app.ActivityScenario;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MainActivity;
import com.example.pajelingo.models.User;
import com.example.pajelingo.tests.abstract_tests.FormUserActivityTests;

import org.junit.Test;

import java.io.IOException;

public class UpdateAccountTests extends FormUserActivityTests {
    private final User user0 = new User("update-test-android0@test.com", "update-test-android0", "upd4te-str0ng-p4ssw0rd0", null);
    private final User user1 = new User("update-test-android1@test.com", "update-test-android1", "upd4te-str0ng-p4ssw0rd1", null);
    private final User user2 = new User("update-test-android2@test.com", "update-test-android2", "upd4te-str0ng-p4ssw0rd2", null);
    private final User user3 = new User("update-test-android3@test.com", "update-test-android3", "upd4te-str0ng-p4ssw0rd3", null);
    private final User user4 = new User("update-test-android4@test.com", "update-test-android4", "upd4te-str0ng-p4ssw0rd4", null);

    @Override
    protected void browseToForm() {
        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_menu)).perform(click());
        onView(withId(R.id.menu_item_profile)).perform(click());
        onView(withId(R.id.edit_account_button)).perform(click());
    }

    @Test
    public void testRenderingAccountUpdateForm(){
        saveStateAndUserCredentials(context, user0);

        String emailLabel = context.getString(R.string.email_label);
        String usernameLabel = context.getString(R.string.username_label);
        String passwordLabel = context.getString(R.string.password_label);
        String passwordConfirmationLabel = context.getString(R.string.password_confirmation_label);

        browseToForm();

        onView(withId(R.id.email_input)).check(matches(isDisplayed()))
                .check(matches(hasLabel(emailLabel)))
                .check(matches(hasInput(user0.getEmail())));
        onView(withId(R.id.username_input)).check(matches(isDisplayed()))
                .check(matches(hasLabel(usernameLabel)))
                .check(matches(hasInput(user0.getUsername())));
        onView(withId(R.id.password_input)).check(matches(isDisplayed())).check(matches(hasLabel(passwordLabel)));
        onView(withId(R.id.password_confirmation_input)).check(matches(isDisplayed())).check(matches(hasLabel(passwordConfirmationLabel)));

        assertPasswordRequirements(false, false, false, false);

        onView(allOf(withId(R.id.submit_button), withText(R.string.update_account_button_text))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.login_link_text_view), withText(R.string.login_link))).check(matches(not(isDisplayed())));
    }

    @Test
    public void testSuccessfulAccountUpdateWithOnlyRandomCredentials() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, user0);
        testSuccessfulAccountUpdate(getRandomEmail(), getRandomUsername(), getRandomValidPassword());
    }

    @Test
    public void testSuccessfulAccountUpdateWithTheSameEmail() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, user1);
        testSuccessfulAccountUpdate(user1.getEmail(), getRandomUsername(), getRandomValidPassword());
    }

    @Test
    public void testSuccessfulAccountUpdateWithTheSameUsername() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, user2);
        testSuccessfulAccountUpdate(getRandomEmail(), user2.getUsername(), getRandomValidPassword());
    }

    @Test
    public void testSuccessfulAccountUpdateWithTheSameEmailAndUsername() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, user3);
        testSuccessfulAccountUpdate(user3.getEmail(), user3.getUsername(), getRandomValidPassword());
    }

    @Test
    public void testFailedAccountUpdateWithoutEmail() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, user4);
        testInvalidSubmission("", getRandomUsername(), getRandomValidPassword(), true,
                true, true, true, true);
    }

    @Test
    public void testFailedAccountUpdateWithoutUsername() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, user4);
        testInvalidSubmission(getRandomEmail(), "", getRandomValidPassword(), true,
                true, true, true, true);
    }

    @Test
    public void testFailedAccountUpdateWithoutPassword() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, user4);
        testInvalidSubmission(getRandomEmail(), getRandomUsername(), "", true,
                false, false, false, false);
    }

    @Test
    public void testFailedAccountUpdateEmailWithSpace() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, user4);
        testInvalidSubmission(getRandomWord(getRandomInteger(1, 5)) + " " + getRandomEmail(), getRandomUsername(),
                getRandomValidPassword(), true, true, true, true, true);
    }

    @Test
    public void testFailedAccountUpdateUsernameWithSpace() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, user4);
        testInvalidSubmission(getRandomEmail(), getRandomWord(getRandomInteger(1, 5)) + " " + getRandomUsername(),
                getRandomValidPassword(), true, true, true, true, true);
    }

    @Test
    public void testFailedAccountUpdateNonAvailableEmail() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, user4);
        testInvalidSubmission(testUser.getEmail(), getRandomUsername(), getRandomValidPassword(), true,
                true, true, true, true);
    }

    @Test
    public void testFailedAccountUpdateNonAvailableUsername() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, user4);
        testInvalidSubmission(getRandomEmail(), testUser.getUsername(), getRandomValidPassword(), true,
                true, true, true, true);
    }

    @Test
    public void testFailedAccountUpdateWith3CharactersPassword() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, user4);
        testInvalidSubmission(getRandomEmail(), getRandomUsername(),
                getRandomInvalidPassword(3, true, true, true), true,
                true, true, true, false);
    }

    @Test
    public void testFailedAccountUpdateWith4CharactersPassword() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, user4);
        testInvalidSubmission(getRandomEmail(), getRandomUsername(),
                getRandomInvalidPassword(4, true, true, true), true,
                true, true, true, false);
    }

    @Test
    public void testFailedAccountUpdateWith5CharactersPassword() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, user4);
        testInvalidSubmission(getRandomEmail(), getRandomUsername(),
                getRandomInvalidPassword(5, true, true, true), true,
                true, true, true, false);
    }

    @Test
    public void testFailedAccountUpdateWith6CharactersPassword() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, user4);
        testInvalidSubmission(getRandomEmail(), getRandomUsername(),
                getRandomInvalidPassword(6, true, true, true), true,
                true, true, true, false);
    }

    @Test
    public void testFailedAccountUpdateWith7CharactersPassword() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, user4);
        testInvalidSubmission(getRandomEmail(), getRandomUsername(),
                getRandomInvalidPassword(7, true, true, true), true,
                true, true, true, false);
    }

    @Test
    public void testFailedAccountUpdateWith31CharactersPassword() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, user4);
        testInvalidSubmission(getRandomEmail(), getRandomUsername(),
                getRandomInvalidPassword(31, true, true, true), true,
                true, true, true, false);
    }

    @Test
    public void testFailedAccountUpdateNoDigit() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, user4);
        testInvalidSubmission(getRandomEmail(), getRandomUsername(),
                getRandomInvalidPassword(getRandomInteger(8, 30), true, false, true), true,
                true, false, true, true);
    }

    @Test
    public void testFailedAccountUpdateNoSpecialCharacter() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, user4);
        testInvalidSubmission(getRandomEmail(), getRandomUsername(),
                getRandomInvalidPassword(getRandomInteger(8, 30), true, true, false), true,
                true, true, false, true);
    }

    @Test
    public void testFailedAccountUpdateNoLetter() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, user4);
        testInvalidSubmission(getRandomEmail(), getRandomUsername(),
                getRandomInvalidPassword(getRandomInteger(8, 30), false, true, true), true,
                false, true, true, true);
    }

    @Test
    public void testFailedAccountUpdatePasswordsDoNotMatch() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, user4);
        testInvalidSubmission(getRandomEmail(), getRandomUsername(), getRandomValidPassword(), false,
                true, true, true, true);
    }

    private void testSuccessfulAccountUpdate(String email, String username, String password) throws IOException, InterruptedException {
        testSuccessfulSubmission(email, username, password);
        assertUserExistsInDjangoApp(email, username, password, true);
    }
}
