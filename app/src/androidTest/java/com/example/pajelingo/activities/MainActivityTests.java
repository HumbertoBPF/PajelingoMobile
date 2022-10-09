package com.example.pajelingo.activities;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.CustomMatchers.showGameItemInPosition;
import static com.example.pajelingo.utils.CustomViewActions.waitForView;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.pajelingo.R;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Game;
import com.example.pajelingo.retrofit.LanguageSchoolAPI;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

@RunWith(AndroidJUnit4.class)
public class MainActivityTests extends UITests{
    private LanguageSchoolAPI languageSchoolAPI = LanguageSchoolAPIHelper.getApiObject();

    @Before
    public void setUp() throws IOException {
        Response<List<Game>> response = languageSchoolAPI.getGames().execute();
        List<Game> games = response.body();
        AppDatabase.getInstance(context).getGameDao().save(games);
    }

    @Test
    public void testMainActivity(){
        onView(withId(R.id.search_button)).check(matches(isDisplayed()));
        onView(withId(R.id.action_synchro)).check(matches(isDisplayed()));
        onView(withId(R.id.action_online)).check(matches(isDisplayed()));
        onView(withId(R.id.games_recycler_view)).check(matches(showGameItemInPosition(0, "Vocabulary Game")));
        onView(withId(R.id.games_recycler_view)).check(matches(showGameItemInPosition(1, "Article Game")));
        onView(withId(R.id.games_recycler_view)).check(matches(showGameItemInPosition(2, "Conjugation Game")));
    }

    @Test
    public void testSync(){
        onView(withId(R.id.action_synchro)).perform(click());
        onView(isRoot()).perform(waitForView(withText(context.getString(R.string.dialog_download_resources_title)), 5000, true));
        onView(withText(R.string.dialog_download_resources_confirm)).perform(click());
        onView(isRoot()).perform(waitForView(withText(context.getString(R.string.progress_download_title)), 5000, true));
        onView(isRoot()).perform(waitForView(withText(context.getString(R.string.progress_download_title)), 30000, false));
    }

    @Test
    public void testDeclineSyncConfirmationDialog(){
        onView(withId(R.id.action_synchro)).perform(click());
        onView(isRoot()).perform(waitForView(withText(context.getString(R.string.dialog_download_resources_title)), 5000, true));
        onView(withText(R.string.dialog_download_resources_decline)).perform(click());
        onView(withText(context.getString(R.string.progress_download_title))).check(doesNotExist());
        testMainActivity();
    }
}
