package com.example.pajelingo.activities;

import static android.content.Context.MODE_PRIVATE;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.util.Tools.getAuthToken;
import static com.example.pajelingo.util.Tools.isUserAuthenticated;
import static com.example.pajelingo.utils.CustomViewActions.waitForView;
import static com.example.pajelingo.utils.Tools.getRandomInteger;
import static com.example.pajelingo.utils.Tools.getRandomString;
import static org.hamcrest.Matchers.allOf;

import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;

import com.example.pajelingo.R;
import com.example.pajelingo.models.User;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelperTest;
import com.example.pajelingo.retrofit.LanguageSchoolAPITest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class LoginActivityTests extends UITests{
    private final LanguageSchoolAPITest languageSchoolAPITest = (LanguageSchoolAPITest) LanguageSchoolAPIHelperTest.getApiObject();
    private final User testUser = new User("test.android@test.com", "TestAndroid", "test-android");

    @Before
    public void setUp() throws IOException {
        context.deleteSharedPreferences(context.getString(R.string.sp_file_name));
        languageSchoolAPITest.deleteAccount(getAuthToken(testUser.getUsername(), testUser.getPassword())).execute();
        languageSchoolAPITest.signup(testUser).execute();
    }

    @Test
    public void testRenderingLoginActivity(){
        String usernameLabel = context.getString(R.string.username_label);
        String passwordLabel = context.getString(R.string.password_label);
        String buttonLoginText = context.getString(R.string.login_button_text);

        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_online)).perform(click());

        onView(allOf(withId(R.id.username_label), withText(usernameLabel))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.password_label), withText(passwordLabel))).check(matches(isDisplayed()));
        onView(withId(R.id.username_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.password_edit_text)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.login_button), withText(buttonLoginText))).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginFailed(){
        String username = getRandomString(getRandomInteger(1, 18));
        String password = getRandomString(getRandomInteger(6, 30));

        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_online)).perform(click());

        onView(withId(R.id.username_edit_text)).perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.password_edit_text)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        onView(isRoot()).perform(waitForView(withText(context.getString(R.string.update_ranking_dialog_title)), 5000, true));
        onView(isRoot()).perform(waitForView(withText(context.getString(R.string.update_ranking_dialog_title)), 30000, false));

        assert !isUserAuthenticated(context);
    }

    @Test
    public void testLoginSuccessful(){
        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_online)).perform(click());

        onView(withId(R.id.username_edit_text)).perform(typeText(testUser.getUsername()), closeSoftKeyboard());
        onView(withId(R.id.password_edit_text)).perform(typeText(testUser.getPassword()), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        onView(isRoot()).perform(waitForView(withText(context.getString(R.string.update_ranking_dialog_title)), 5000, true));
        onView(isRoot()).perform(waitForView(withText(context.getString(R.string.update_ranking_dialog_title)), 30000, false));

        assert isUserAuthenticated(context);

        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.sp_file_name),MODE_PRIVATE);
        String username = sp.getString(context.getString(R.string.username_sp),null);
        String password = sp.getString(context.getString(R.string.password_sp),null);

        assert username.equals(testUser.getUsername());
        assert password.equals(testUser.getPassword());
        // Verify menu icons on app bar (ranking must be rendered after login)
        onView(withId(R.id.action_synchro)).check(matches(isDisplayed()));
        onView(withId(R.id.action_online)).check(matches(isDisplayed()));
        onView(withId(R.id.action_rankings)).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() throws IOException {
        super.tearDown();
        languageSchoolAPITest.deleteAccount(getAuthToken(testUser.getUsername(), testUser.getPassword())).execute();
        context.deleteSharedPreferences(context.getString(R.string.sp_file_name));
    }
    
}