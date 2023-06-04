package com.example.pajelingo.tests.account_tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.CustomMatchers.getLabeledEditTextMatcher;
import static com.example.pajelingo.utils.RandomTools.getRandomAlphabeticalString;
import static com.example.pajelingo.utils.RandomTools.getRandomBio;
import static com.example.pajelingo.utils.RandomTools.getRandomEmail;
import static com.example.pajelingo.utils.RandomTools.getRandomInteger;
import static com.example.pajelingo.utils.RandomTools.getRandomInvalidPassword;
import static com.example.pajelingo.utils.RandomTools.getRandomUsername;
import static com.example.pajelingo.utils.RandomTools.getRandomValidPassword;
import static com.example.pajelingo.utils.RetrofitTools.assertUserExistsInDjangoApp;
import static com.example.pajelingo.utils.TestTools.authenticateUser;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.not;

import android.view.View;

import androidx.test.core.app.ActivityScenario;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MainActivity;
import com.example.pajelingo.models.User;
import com.example.pajelingo.tests.abstract_tests.FormUserActivityTests;

import org.hamcrest.Matcher;
import org.junit.Test;

import java.io.IOException;

public class UpdateAccountTests extends FormUserActivityTests {
    private final User user0 =
            new User("update-test-android0@test.com", "update-test-android0", "upd4te-str0ng-p4ssw0rd0", "bio 0");
    private final User user1 =
            new User("update-test-android1@test.com", "update-test-android1", "upd4te-str0ng-p4ssw0rd1", "bio 1");
    private final User user2 =
            new User("update-test-android2@test.com", "update-test-android2", "upd4te-str0ng-p4ssw0rd2", "bio 2");
    private final User user3 =
            new User("update-test-android3@test.com", "update-test-android3", "upd4te-str0ng-p4ssw0rd3", "bio 3");
    private final User user4 =
            new User("update-test-android4@test.com", "update-test-android4", "upd4te-str0ng-p4ssw0rd4", "bio 4");

    @Override
    protected void browseToForm() {
        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_menu))
                .perform(click());
        onView(withId(R.id.menu_recycler_view))
                .perform(actionOnItemAtPosition(0, click()));
        onView(withId(R.id.edit_account_button))
                .perform(click());
    }

    @Test
    public void testRenderingAccountUpdateForm() throws IOException {
        authenticateUser(context, user4);

        browseToForm();

        Matcher<View> emailInputMatcher = getLabeledEditTextMatcher(R.string.email_label, user4.getEmail());
        onView(withId(R.id.email_input))
                .check(matches(emailInputMatcher));

        Matcher<View> usernameInputMatcher = getLabeledEditTextMatcher(R.string.username_label, user4.getUsername());
        onView(withId(R.id.username_input))
                .check(matches(usernameInputMatcher));

        Matcher<View> passwordInputMatcher = getLabeledEditTextMatcher(R.string.password_label, "");
        onView(withId(R.id.password_input))
                .check(matches(passwordInputMatcher));

        Matcher<View> passwordConfirmationInputMatcher = getLabeledEditTextMatcher(R.string.password_confirmation_label, "");
        onView(withId(R.id.password_confirmation_input))
                .check(matches(passwordConfirmationInputMatcher));

        assertPasswordRequirements(false, false, false, false);

        Matcher<View> submitButtonMatcher = allOf(hasDescendant(withText(R.string.update_account_button_text)), isDisplayed());
        onView(withId(R.id.submit_button))
                .check(matches(submitButtonMatcher));

        Matcher<View> loginLinkMatcher = allOf(withText(R.string.login_link), not(isDisplayed()));
        onView(withId(R.id.login_link_text_view))
                .check(matches(loginLinkMatcher));
    }

    @Test
    public void testSuccessfulAccountUpdateWithOnlyRandomCredentials() throws IOException {
        authenticateUser(context, user0);
        testSuccessfulAccountUpdate(getRandomEmail(), getRandomUsername(), user0.getBio(), getRandomValidPassword());
    }

    @Test
    public void testSuccessfulAccountUpdateWithTheSameEmail() throws IOException {
        authenticateUser(context, user1);
        testSuccessfulAccountUpdate(user1.getEmail(), getRandomUsername(), user1.getBio(), getRandomValidPassword());
    }

    @Test
    public void testSuccessfulAccountUpdateWithTheSameUsername() throws IOException {
        authenticateUser(context, user2);
        testSuccessfulAccountUpdate(getRandomEmail(), user2.getUsername(), user2.getBio(), getRandomValidPassword());
    }

    @Test
    public void testSuccessfulAccountUpdateWithTheSameEmailAndUsername() throws IOException {
        authenticateUser(context, user3);
        testSuccessfulAccountUpdate(user3.getEmail(), user3.getUsername(), user3.getBio(), getRandomValidPassword());
    }

    @Test
    public void testFailedAccountUpdateWithoutEmail() throws IOException {
        authenticateUser(context, user4);
        testInvalidSubmission("", getRandomUsername(), getRandomBio(), getRandomValidPassword(),
                true, true, true, true, true);
    }

    @Test
    public void testFailedAccountUpdateWithoutUsername() throws IOException {
        authenticateUser(context, user4);
        testInvalidSubmission(getRandomEmail(), "", getRandomBio(), getRandomValidPassword(),
                true, true, true, true, true);
    }

    @Test
    public void testFailedAccountUpdateWithoutPassword() throws IOException {
        authenticateUser(context, user4);
        testInvalidSubmission(getRandomEmail(), getRandomUsername(), getRandomBio(), "",
                true, false, false, false, false);
    }

    @Test
    public void testFailedAccountUpdateEmailWithSpace() throws IOException {
        authenticateUser(context, user4);
        testInvalidSubmission(getRandomAlphabeticalString(getRandomInteger(1, 5)) + " " + getRandomEmail(), getRandomUsername(),
                getRandomBio(), getRandomValidPassword(),
                true, true, true, true, true);
    }

    @Test
    public void testFailedAccountUpdateUsernameWithSpace() throws IOException {
        authenticateUser(context, user4);
        testInvalidSubmission(getRandomEmail(), getRandomAlphabeticalString(getRandomInteger(1, 5)) + " " + getRandomUsername(),
                getRandomBio(), getRandomValidPassword(),
                true, true, true, true, true);
    }

    @Test
    public void testFailedAccountUpdateNonAvailableEmail() throws IOException {
        authenticateUser(context, user4);
        testInvalidSubmission(testUser.getEmail(), getRandomUsername(), getRandomBio(), getRandomValidPassword(),
                true, true, true, true, true);
    }

    @Test
    public void testFailedAccountUpdateNonAvailableUsername() throws IOException {
        authenticateUser(context, user4);
        testInvalidSubmission(getRandomEmail(), testUser.getUsername(), getRandomBio(), getRandomValidPassword(),
                true, true, true, true, true);
    }

    @Test
    public void testFailedAccountUpdateWith3CharactersPassword() throws IOException {
        authenticateUser(context, user4);
        testInvalidSubmission(getRandomEmail(), getRandomUsername(), getRandomBio(),
                getRandomInvalidPassword(3, true, true, true),
                true, true, true, true, false);
    }

    @Test
    public void testFailedAccountUpdateWith4CharactersPassword() throws IOException {
        authenticateUser(context, user4);
        testInvalidSubmission(getRandomEmail(), getRandomUsername(), getRandomBio(),
                getRandomInvalidPassword(4, true, true, true),
                true, true, true, true, false);
    }

    @Test
    public void testFailedAccountUpdateWith5CharactersPassword() throws IOException {
        authenticateUser(context, user4);
        testInvalidSubmission(getRandomEmail(), getRandomUsername(), getRandomBio(),
                getRandomInvalidPassword(5, true, true, true),
                true, true, true, true, false);
    }

    @Test
    public void testFailedAccountUpdateWith6CharactersPassword() throws IOException {
        authenticateUser(context, user4);
        testInvalidSubmission(getRandomEmail(), getRandomUsername(), getRandomBio(),
                getRandomInvalidPassword(6, true, true, true),
                true, true, true, true, false);
    }

    @Test
    public void testFailedAccountUpdateWith7CharactersPassword() throws IOException {
        authenticateUser(context, user4);
        testInvalidSubmission(getRandomEmail(), getRandomUsername(), getRandomBio(),
                getRandomInvalidPassword(7, true, true, true),
                true, true, true, true, false);
    }

    @Test
    public void testFailedAccountUpdateWith31CharactersPassword() throws IOException {
        authenticateUser(context, user4);
        testInvalidSubmission(getRandomEmail(), getRandomUsername(), getRandomBio(),
                getRandomInvalidPassword(31, true, true, true),
                true, true, true, true, false);
    }

    @Test
    public void testFailedAccountUpdateNoDigit() throws IOException {
        authenticateUser(context, user4);
        testInvalidSubmission(getRandomEmail(), getRandomUsername(), getRandomBio(),
                getRandomInvalidPassword(getRandomInteger(8, 30), true, false, true),
                true, true, false, true, true);
    }

    @Test
    public void testFailedAccountUpdateNoSpecialCharacter() throws IOException {
        authenticateUser(context, user4);
        testInvalidSubmission(getRandomEmail(), getRandomUsername(), getRandomBio(),
                getRandomInvalidPassword(getRandomInteger(8, 30), true, true, false),
                true, true, true, false, true);
    }

    @Test
    public void testFailedAccountUpdateNoLetter() throws IOException {
        authenticateUser(context, user4);
        testInvalidSubmission(getRandomEmail(), getRandomUsername(), getRandomBio(),
                getRandomInvalidPassword(getRandomInteger(8, 30), false, true, true),
                true, false, true, true, true);
    }

    @Test
    public void testFailedAccountUpdatePasswordsDoNotMatch() throws IOException {
        authenticateUser(context, user4);
        testInvalidSubmission(getRandomEmail(), getRandomUsername(), getRandomBio(), getRandomValidPassword(),
                false, true, true, true, true);
    }

    private void testSuccessfulAccountUpdate(String email, String username, String bio, String password) throws IOException {
        testSuccessfulSubmission(email, username, bio, password);
        User expectedUser = new User(email, username, password, bio);
        assertUserExistsInDjangoApp(expectedUser, true);
    }
}
