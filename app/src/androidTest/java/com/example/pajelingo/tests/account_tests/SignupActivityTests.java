package com.example.pajelingo.tests.account_tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.CustomMatchers.hasLabel;
import static com.example.pajelingo.utils.CustomMatchers.hasRequirementText;
import static com.example.pajelingo.utils.CustomMatchers.isChecked;
import static com.example.pajelingo.utils.CustomViewActions.fillLabeledEditText;
import static com.example.pajelingo.utils.RandomTools.getInvalidPassword;
import static com.example.pajelingo.utils.RandomTools.getRandomInteger;
import static com.example.pajelingo.utils.RandomTools.getRandomWord;
import static com.example.pajelingo.utils.RetrofitTools.assertUserExistsInDjangoApp;
import static com.example.pajelingo.utils.Tools.getAuthToken;
import static org.hamcrest.CoreMatchers.allOf;

import androidx.test.core.app.ActivityScenario;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MainActivity;
import com.example.pajelingo.models.User;
import com.example.pajelingo.tests.abstract_tests.UITests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class SignupActivityTests extends UITests {
    private final User existingUser = new User("test.android.2@test.com", "TestAndroid2", "test-@ndro1d2");

    @Before
    public void setUp() throws IOException {
        languageSchoolAPI.deleteAccount(getAuthToken(testUser.getUsername(), testUser.getPassword())).execute();
        languageSchoolAPI.deleteAccount(getAuthToken(existingUser.getUsername(), existingUser.getPassword())).execute();
        languageSchoolAPI.signup(existingUser).execute();
    }

    @Test
    public void testSignupActivityActivity(){
        String emailLabel = context.getString(R.string.email_label);
        String usernameLabel = context.getString(R.string.username_label);
        String passwordLabel = context.getString(R.string.password_label);

        browseSignupScreen();

        onView(withId(R.id.email_input)).check(matches(isDisplayed())).check(matches(hasLabel(emailLabel)));
        onView(withId(R.id.username_input)).check(matches(isDisplayed())).check(matches(hasLabel(usernameLabel)));
        onView(withId(R.id.password_input)).check(matches(isDisplayed())).check(matches(hasLabel(passwordLabel)));

        assertPasswordRequirements(false, false, false, false);

        onView(allOf(withId(R.id.signup_button), withText(R.string.signup_button_text))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.login_link_text_view), withText(R.string.login_link))).check(matches(isDisplayed()));
    }

    @Test
    public void testSuccessfulSignup() throws IOException, InterruptedException {
        String email = testUser.getEmail();
        String username = testUser.getUsername();
        String password = testUser.getPassword();

        browseSignupScreen();

        fillSignupForm(email, username, password);

        assertPasswordRequirements(true, true, true, true);

        onView(withId(R.id.signup_button)).perform(click());

        assertUserExistsInDjangoApp(email,username, password, true);
    }

    @Test
    public void testFailedSignupWithoutEmail() throws IOException, InterruptedException {
        testFailedSignupWithValidPassword("", testUser.getUsername());
    }

    @Test
    public void testFailedSignupWithoutUsername() throws IOException, InterruptedException {
        testFailedSignupWithValidPassword(testUser.getEmail(), "");
    }

    @Test
    public void testFailedSignupWithoutPassword() throws IOException, InterruptedException {
        testFailedSignupInvalidPassword(0, false, false, false);
    }

    @Test
    public void testFailedSignupEmailWithSpace() throws IOException, InterruptedException {
        String email = getRandomWord(getRandomInteger(1, 5)) + " " + testUser.getEmail();
        String username = testUser.getUsername();
        testFailedSignupWithValidPassword(email, username);
    }

    @Test
    public void testFailedSignupUsernameWithSpace() throws IOException, InterruptedException {
        String email = testUser.getEmail();
        String username = getRandomWord(getRandomInteger(1, 5)) + " " + testUser.getUsername();
        testFailedSignupWithValidPassword(email, username);
    }

    @Test
    public void testFailedSignupNonAvailableEmail() throws IOException, InterruptedException {
        testFailedSignupWithValidPassword(existingUser.getEmail(), testUser.getUsername());
    }

    @Test
    public void testFailedSignupNonAvailableUsername() throws IOException, InterruptedException {
        testFailedSignupWithValidPassword(testUser.getEmail(), existingUser.getUsername());
    }

    @Test
    public void testFailedSignupWith3CharactersPassword() throws IOException, InterruptedException {
        testFailedSignupInvalidPassword(3, true, true, true);
    }

    @Test
    public void testFailedSignupWith4CharactersPassword() throws IOException, InterruptedException {
        testFailedSignupInvalidPassword(4, true, true, true);
    }

    @Test
    public void testFailedSignupWith5CharactersPassword() throws IOException, InterruptedException {
        testFailedSignupInvalidPassword(5, true, true, true);
    }

    @Test
    public void testFailedSignupWith6CharactersPassword() throws IOException, InterruptedException {
        testFailedSignupInvalidPassword(6, true, true, true);
    }

    @Test
    public void testFailedSignupWith7CharactersPassword() throws IOException, InterruptedException {
        testFailedSignupInvalidPassword(7,true, true, true);
    }

    @Test
    public void testFailedSignupWith31CharactersPassword() throws IOException, InterruptedException {
        testFailedSignupInvalidPassword(31,true, true, true);
    }

    @Test
    public void testFailedSignupNoDigit() throws IOException, InterruptedException {
        testFailedSignupInvalidPassword(getRandomInteger(8, 30),true, false, true);
    }

    @Test
    public void testFailedSignupNoSpecialCharacter() throws IOException, InterruptedException {
        testFailedSignupInvalidPassword(getRandomInteger(8, 30), true, true, false);
    }

    @Test
    public void testFailedSignupNoLetter() throws IOException, InterruptedException {
        testFailedSignupInvalidPassword(getRandomInteger(8, 30),false, true, true);
    }

    private void testFailedSignupWithValidPassword(String email, String username) throws IOException, InterruptedException {
        String password = testUser.getPassword();

        browseSignupScreen();

        fillSignupForm(email, username, password);

        assertPasswordRequirements(true, true, true, true);

        onView(withId(R.id.signup_button)).perform(click());

        assertUserExistsInDjangoApp(email, username, password, false);
    }

    private void testFailedSignupInvalidPassword(int length, boolean hasLetter,
                                                 boolean hasDigit, boolean hasSpecialCharacter) throws IOException, InterruptedException {
        String email = testUser.getEmail();
        String username = testUser.getUsername();
        String password = getInvalidPassword(length, hasLetter, hasDigit, hasSpecialCharacter);

        browseSignupScreen();

        fillSignupForm(email, username, password);

        assertPasswordRequirements(hasLetter, hasDigit, hasSpecialCharacter, ((length >= 8) && (length<=30)));

        onView(withId(R.id.signup_button)).perform(click());

        assertUserExistsInDjangoApp(email, username, password, false);
    }

    /**
     * Browses in the app until the signup screen. The flow is: MainActivity --> LoginActivity --> SignupActivity
     */
    private void browseSignupScreen() {
        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_login_logout)).perform(click());
        onView(withId(R.id.signup_link_text_view)).perform(click());
    }

    /**
     * Fills the signup form with the specified credentials.
     * @param email email credential
     * @param username username credential
     * @param password password credential
     */
    private void fillSignupForm(String email, String username, String password) {
        onView(withId(R.id.email_input)).perform(fillLabeledEditText(email));
        onView(withId(R.id.username_input)).perform(fillLabeledEditText(username));
        onView(withId(R.id.password_input)).perform(fillLabeledEditText(password));
    }

    /**
     * Asserts that the password requirements checklist is correctly displayed.
     * @param hasLetter boolean indicating if the concerned password has letters.
     * @param hasDigit boolean indicating if the concerned password has digits.
     * @param hasSpecialCharacter boolean indicating if the concerned password has special characters.
     * @param hasValidLength boolean indicating if the concerned password has a length between 8 and 30.
     */
    private void assertPasswordRequirements(boolean hasLetter, boolean hasDigit, boolean hasSpecialCharacter, boolean hasValidLength) {
        String requirementText1 = context.getString(R.string.password_requirement_1);
        String requirementText2 = context.getString(R.string.password_requirement_2);
        String requirementText3 = context.getString(R.string.password_requirement_3);
        String requirementText4 = context.getString(R.string.password_requirement_4);

        onView(allOf(withId(R.id.password_requirements_label), withText(R.string.password_requirements_label))).check(matches(isDisplayed()));
        onView(withId(R.id.requirement_1)).check(matches(isDisplayed()))
                .check(matches(hasRequirementText(requirementText1))).check(matches(isChecked(hasDigit)));
        onView(withId(R.id.requirement_2)).check(matches(isDisplayed()))
                .check(matches(hasRequirementText(requirementText2))).check(matches(isChecked(hasLetter)));
        onView(withId(R.id.requirement_3)).check(matches(isDisplayed()))
                .check(matches(hasRequirementText(requirementText3))).check(matches(isChecked(hasSpecialCharacter)));
        onView(withId(R.id.requirement_4)).check(matches(isDisplayed()))
                .check(matches(hasRequirementText(requirementText4))).check(matches(isChecked(hasValidLength)));
    }

    @After
    public void tearDown() throws IOException {
        super.tearDown();
        languageSchoolAPI.deleteAccount(getAuthToken(testUser.getUsername(), testUser.getPassword())).execute();
        languageSchoolAPI.deleteAccount(getAuthToken(existingUser.getUsername(), existingUser.getPassword())).execute();
    }
}
