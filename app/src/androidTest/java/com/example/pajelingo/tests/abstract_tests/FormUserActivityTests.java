package com.example.pajelingo.tests.abstract_tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.CustomMatchers.hasRequirementText;
import static com.example.pajelingo.utils.CustomMatchers.isChecked;
import static com.example.pajelingo.utils.CustomViewActions.fillLabeledEditText;
import static com.example.pajelingo.utils.CustomViewActions.waitUntil;
import static com.example.pajelingo.utils.RandomTools.getRandomString;
import static com.example.pajelingo.utils.RetrofitTools.assertUserExistsInDjangoApp;
import static org.hamcrest.CoreMatchers.allOf;

import com.example.pajelingo.R;

import java.io.IOException;

public abstract class FormUserActivityTests extends UITests{
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
     */
    protected void testSuccessfulSubmission(String email, String username, String password) {
        browseToForm();

        fillForm(email, username, password, password);
        assertPasswordRequirements(true, true, true, true);

        onView(withId(R.id.submit_button)).perform(click());

        onView(isRoot()).perform(waitUntil(withText((R.string.login_dialog_title)), 5000, true));
        onView(isRoot()).perform(waitUntil(withText(R.string.login_dialog_title), 30000, false));
    }

    /**
     * Fills the user form and asserts that the input is invalid.
     * @param email email input
     * @param username username input
     * @param password password input
     * @param passwordsMatch if the password and its confirmation match
     * @param hasLetter if the password contains a letter
     * @param hasDigit if the password contains a digit
     * @param hasSpecialCharacter if the password contains a special character
     * @param hasValidLength if the password has a valid length
     * @throws IOException thrown when some error related with HTTP communication occurs
     * @throws InterruptedException thrown when some error related with main thread manipulation occurs
     */
    protected void testInvalidSubmission(String email, String username, String password, boolean passwordsMatch, boolean hasLetter,
                                         boolean hasDigit, boolean hasSpecialCharacter, boolean hasValidLength) throws IOException, InterruptedException {
        browseToForm();

        String passwordConfirmation = passwordsMatch?password:getRandomString(password.length(), true, true, true);

        fillForm(email, username, password, passwordConfirmation);
        assertPasswordRequirements(hasLetter, hasDigit, hasSpecialCharacter, hasValidLength);

        onView(withId(R.id.submit_button)).perform(click());

        assertUserExistsInDjangoApp(email, username, password, false);
    }
}
