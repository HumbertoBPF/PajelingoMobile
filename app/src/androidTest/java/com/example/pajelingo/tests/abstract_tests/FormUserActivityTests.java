package com.example.pajelingo.tests.abstract_tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.CustomMatchers.hasRequirementText;
import static com.example.pajelingo.utils.CustomMatchers.isChecked;
import static com.example.pajelingo.utils.CustomViewActions.fillLabeledEditText;
import static com.example.pajelingo.utils.RandomTools.getInvalidPassword;
import static com.example.pajelingo.utils.RandomTools.getRandomEmail;
import static com.example.pajelingo.utils.RandomTools.getRandomString;
import static com.example.pajelingo.utils.RandomTools.getRandomUsername;
import static com.example.pajelingo.utils.RetrofitTools.assertUserExistsInDjangoApp;
import static com.example.pajelingo.utils.Tools.getAuthToken;
import static org.hamcrest.CoreMatchers.allOf;

import com.example.pajelingo.R;
import com.example.pajelingo.models.User;

import org.junit.After;
import org.junit.Before;

import java.io.IOException;

public abstract class FormUserActivityTests extends UITests{
    protected final User existingUser = new User("test.android.2@test.com", "TestAndroid2", "test-@ndro1d2");

    @Before
    public void setUp() throws IOException {
        languageSchoolAPI.deleteAccount(getAuthToken(testUser.getUsername(), testUser.getPassword())).execute();
        languageSchoolAPI.deleteAccount(getAuthToken(existingUser.getUsername(), existingUser.getPassword())).execute();
        languageSchoolAPI.signup(existingUser).execute();
    }

    /**
     * Fills the form with the specified credentials.
     * @param email email credential
     * @param username username credential
     * @param password password credential
     */
    protected void fillForm(String email, String username, String password, String passwordConfirmation) {
        onView(withId(R.id.email_input)).perform(fillLabeledEditText(email));
        onView(withId(R.id.username_input)).perform(fillLabeledEditText(username));
        onView(withId(R.id.password_input)).perform(fillLabeledEditText(password));
        onView(withId(R.id.password_confirmation_input)).perform(fillLabeledEditText(passwordConfirmation));
    }

    /**
     * Asserts that the password requirements checklist is correctly displayed.
     * @param hasLetter boolean indicating if the concerned password has letters
     * @param hasDigit boolean indicating if the concerned password has digits
     * @param hasSpecialCharacter boolean indicating if the concerned password has special characters
     * @param hasValidLength boolean indicating if the concerned password has a length between 8 and 30
     */
    protected void assertPasswordRequirements(boolean hasLetter, boolean hasDigit, boolean hasSpecialCharacter, boolean hasValidLength) {
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

    /**
     * This method must be implemented in the children classes, browsing in the app until the update form screen.
     */
    protected abstract void browseToForm();

    /**
     * Inputs the specified credentials and waits for a successful response when looking for the
     * user in the Django API database.
     * @param email email input
     * @param username username input
     * @param password password input
     * @throws IOException thrown when some error related with HTTP communication occurs
     * @throws InterruptedException thrown when some error related with main thread manipulation occurs
     */
    protected void testSuccessfulSubmission(String email, String username, String password) throws IOException, InterruptedException {
        browseToForm();

        fillForm(email, username, password, password);

        assertPasswordRequirements(true, true, true, true);

        onView(withId(R.id.submit_button)).perform(click());

        assertUserExistsInDjangoApp(email,username, password, true);
        // Delete the created user
        languageSchoolAPI.deleteAccount(getAuthToken(username, password)).execute();
    }

    /**
     * Inputs the specified credentials and waits for an authentication error (401 HTTP code) when
     * trying to find the user in the Django API database.
     * @param email email input
     * @param username username input
     * @param passwordsMatch if the password and confirmation password match
     * @throws IOException thrown when some error related with HTTP communication occurs
     * @throws InterruptedException thrown when some error related with main thread manipulation occurs
     */
    protected void testFailedSubmissionWithValidPassword(String email, String username, boolean passwordsMatch) throws IOException, InterruptedException {
        String password = getRandomString(5, true, true, true) + "1@a";
        String passwordConfirmation;

        if (passwordsMatch) {
            passwordConfirmation = password;
        }else{
            passwordConfirmation = getRandomString(password.length(), true, true, true);
        }

        browseToForm();
        fillForm(email, username, password, passwordConfirmation);

        assertPasswordRequirements(true, true, true, true);

        onView(withId(R.id.submit_button)).perform(click());

        assertUserExistsInDjangoApp(email, username, password, false);
    }

    /**
     * Inputs random valid email and username credentials, generates an invalid password and waits
     * for an authentication error (401 HTTP code) when trying to find the user in the Django API
     * database.
     * @param length length of the password to be generated
     * @param hasLetter if the generated password must contain letters
     * @param hasDigit if the generated password must contain digits
     * @param hasSpecialCharacter if the generated password must contain special characters
     * @throws IOException thrown when some error related with HTTP communication occurs
     * @throws InterruptedException thrown when some error related with main thread manipulation occurs
     */
    protected void testFailedSubmissionWithInvalidPassword(int length, boolean hasLetter,
                                                         boolean hasDigit, boolean hasSpecialCharacter) throws IOException, InterruptedException {
        String email = getRandomEmail();
        String username = getRandomUsername();
        String password = getInvalidPassword(length, hasLetter, hasDigit, hasSpecialCharacter);

        browseToForm();

        fillForm(email, username, password, password);

        assertPasswordRequirements(hasLetter, hasDigit, hasSpecialCharacter, ((length >= 8) && (length<=30)));

        onView(withId(R.id.submit_button)).perform(click());

        assertUserExistsInDjangoApp(email, username, password, false);
    }

    @After
    public void tearDown() throws IOException {
        super.tearDown();
        languageSchoolAPI.deleteAccount(getAuthToken(existingUser.getUsername(), existingUser.getPassword())).execute();
    }
}
