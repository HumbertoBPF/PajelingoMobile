package com.example.pajelingo.tests.account_tests;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.RandomTools.getRandomAlphabeticalString;
import static com.example.pajelingo.utils.RandomTools.getRandomLanguage;
import static com.example.pajelingo.utils.RetrofitTools.assertUserExistsInDjangoApp;
import static com.example.pajelingo.utils.TestTools.authenticateUser;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import androidx.test.core.app.ActivityScenario;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MainActivity;
import com.example.pajelingo.daos.LanguageDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.User;

import org.hamcrest.Matcher;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class MyProfileActivityTests extends AccountActivityTests {
    private final User userToDelete =
            new User("test-android-delete@test.com", "test-android-delete", "str0ng-p4ssw0rd", null, "bio delete");

    @Test
    public void testRenderingMyProfileActivity() throws IOException {
        LanguageDao languageDao = AppDatabase.getInstance(context).getLanguageDao();

        authenticateUser(context, testUser);

        List<Language> languages = languageDao.getAllRecords();
        Language defaultLanguage = languages.get(0);

        browseToMyProfileActivity();
        assertViewsMyProfileActivity();

        assertScoreHistory(testUser.getUsername(), defaultLanguage.getLanguageName());
    }

    @Test
    public void testFilterScoreByLanguageOnMyProfileActivity() throws IOException {
        authenticateUser(context, testUser);

        Language randomLanguage = getRandomLanguage(context);

        browseToMyProfileActivity();

        onView(withId(R.id.language_spinner)).perform(click());
        onData(is(randomLanguage)).inRoot(isPlatformPopup()).perform(click());

        assertScoreHistory(testUser.getUsername(), Objects.requireNonNull(randomLanguage).getLanguageName());
    }

    @Test
    public void testRenderingMyProfileActivityDeleteAccountDialog() throws IOException {
        authenticateUser(context, testUser);

        browseToMyProfileActivity();
        onView(withId(R.id.delete_account_button))
                .perform(click());

        onView(withText(R.string.dialog_delete_account_title))
                .check(matches(isDisplayed()));
        onView(withText(R.string.dialog_delete_account_message))
                .check(matches(isDisplayed()));
        onView(withText(R.string.dialog_delete_account_confirm))
                .check(matches(isDisplayed()));
        onView(withText(R.string.dialog_delete_account_decline))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testRenderingDeletionConfirmationActivity() throws IOException {
        authenticateUser(context, testUser);

        browseToMyProfileActivity();
        onView(withId(R.id.delete_account_button))
                .perform(click());
        onView(withText(R.string.dialog_delete_account_confirm))
                .perform(click());

        Matcher<View> confirmDeletionTextViewMatcher = allOf(withText(R.string.confirm_deletion_text), isDisplayed());
        onView(withId(R.id.confirm_deletion_text_view))
                .check(matches(confirmDeletionTextViewMatcher));

        Matcher<View> confirmDeletionString = allOf(withHint(R.string.confirm_deletion_string), isDisplayed());
        onView(withId(R.id.confirm_deletion_edit_text))
                .check(matches(confirmDeletionString));

        Matcher<View> confirmDeletionButtonMatcher = allOf(hasDescendant(withText(R.string.delete_account)), isDisplayed());
        onView(withId(R.id.confirm_deletion_button))
                .check(matches(confirmDeletionButtonMatcher));
    }

    @Test
    public void testDeclineMyProfileActivityDeleteAccountDialog() throws IOException {
        authenticateUser(context, testUser);

        browseToMyProfileActivity();
        onView(withId(R.id.delete_account_button)).perform(click());
        onView(withText(R.string.dialog_delete_account_decline)).perform(click());

        assertViewsMyProfileActivity();
    }

    @Test
    public void testDeleteAccountSuccessful() throws IOException {
        authenticateUser(context, userToDelete);

        browseToMyProfileActivity();

        onView(withId(R.id.delete_account_button))
                .perform(click());
        onView(withText(R.string.dialog_delete_account_confirm))
                .perform(click());

        onView(withId(R.id.confirm_deletion_edit_text))
                .perform(typeText(context.getString(R.string.confirm_deletion_string)), closeSoftKeyboard());
        onView(withId(R.id.confirm_deletion_button))
                .perform(click());

        onView(withId(R.id.search_button)).check(matches(isDisplayed()));
        assertUserExistsInDjangoApp(userToDelete, false);
    }

    @Test
    public void testDeleteAccountFailedConfirmText() throws IOException {
        authenticateUser(context, testUser);

        browseToMyProfileActivity();

        onView(withId(R.id.delete_account_button)).perform(click());
        onView(withText(R.string.dialog_delete_account_confirm)).perform(click());

        onView(withId(R.id.confirm_deletion_edit_text))
                .perform(typeText(getRandomAlphabeticalString(10)), closeSoftKeyboard());
        onView(withId(R.id.confirm_deletion_button))
                .perform(click());

        assertUserExistsInDjangoApp(testUser, true);
    }

    @Test
    public void testLogout() throws IOException {
        authenticateUser(context, testUser);

        activityScenario = ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.action_login_logout)).perform(click());

        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.sp_file_name), Context.MODE_PRIVATE);

        String email = sp.getString(context.getString(R.string.email_sp), null);
        String username = sp.getString(context.getString(R.string.username_sp), null);
        String token = sp.getString(context.getString(R.string.token_sp), null);
        String picture = sp.getString(context.getString(R.string.picture_sp), null);

        assertNull(email);
        assertNull(username);
        assertNull(token);
        assertNull(picture);
    }

    private void browseToMyProfileActivity() {
        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_menu))
                .perform(click());
        onView(withId(R.id.menu_recycler_view))
                .perform(actionOnItemAtPosition(0, click()));
    }

    private void assertViewsMyProfileActivity() {
        String usernameText = context.getString(R.string.account_username, testUser.getUsername());
        String emailText = context.getString(R.string.account_email, testUser.getEmail());
        String bioText = context.getString(R.string.account_bio, testUser.getBio());

        Matcher<View> usernameMatcher = allOf(withText(usernameText), isDisplayed());
        onView(withId(R.id.username_text_view))
                .check(matches(usernameMatcher));

        Matcher<View> emailMatcher = allOf(withText(emailText), isDisplayed());
        onView(withId(R.id.email_text_view))
                .check(matches(emailMatcher));

        Matcher<View> bioMatcher = allOf(withText(bioText), isDisplayed());
        onView(withId(R.id.bio_text_view))
                .check(matches(bioMatcher));

        Matcher<View> editAccountButtonMatcher = allOf(withText(R.string.update_account), isDisplayed());
        onView(withId(R.id.edit_account_button))
                .check(matches(editAccountButtonMatcher));

        Matcher<View> deleteAccountButtonMatcher = allOf(withText(R.string.delete_account), isDisplayed());
        onView(withId(R.id.delete_account_button))
                .check(matches(deleteAccountButtonMatcher));
    }
}
