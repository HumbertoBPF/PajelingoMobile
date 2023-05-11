package com.example.pajelingo.tests.account_tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.CustomMatchers.hasLength;
import static com.example.pajelingo.utils.CustomViewActions.clickChildViewWithId;
import static com.example.pajelingo.utils.RandomTools.getRandomInteger;
import static com.example.pajelingo.utils.TestTools.authenticateUser;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertFalse;

import android.view.View;

import androidx.test.core.app.ActivityScenario;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MainActivity;
import com.example.pajelingo.daos.MeaningDao;
import com.example.pajelingo.daos.WordDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Meaning;
import com.example.pajelingo.models.Word;
import com.example.pajelingo.tests.abstract_tests.WordListActivityTest;

import org.hamcrest.Matcher;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class FavoriteWordsActivityTests extends WordListActivityTest {
    @Test
    public void testRenderingFavoriteWordsActivityFilters() throws IOException {
        authenticateUser(context, testUser);

        activityScenario = ActivityScenario.launch(MainActivity.class);

        browseToActivity();
        onView(withId(R.id.action_filter))
                .perform(click());
        assertFilterWordsForm();
    }

    @Test
    public void testFavoriteWordsSearchWithNoResults() throws IOException {
        authenticateUser(context, testUser);

        Language randomLanguage = getRandomLanguage();
        String searchPattern = getSearchPattern(randomLanguage, false);

        activityScenario = ActivityScenario.launch(MainActivity.class);

        browseToActivity();
        performSearch(searchPattern, randomLanguage);
        assertNoResults();
    }

    @Test
    public void testFavoriteWordsSearchWithResults() throws IOException {
        authenticateUser(context, testUser);

        Language randomLanguage = getRandomLanguage();
        String searchPattern = getSearchPattern(randomLanguage, true);
        List<Word> favoriteWords = searchWords(searchPattern, randomLanguage);

        activityScenario = ActivityScenario.launch(MainActivity.class);

        browseToActivity();
        performSearch(searchPattern, randomLanguage);
        // Checking that the results are correctly displayed
        Matcher<View> searchRecyclerViewMatcher = allOf(hasLength(favoriteWords.size()), isDisplayed());
        onView(withId(R.id.search_recycler_view))
                .check(matches(searchRecyclerViewMatcher));

        for (int i = 0;i < favoriteWords.size();i++){
            Word favoriteWord = favoriteWords.get(i);
            assertSearchResult(favoriteWord, i, searchPattern, true);
        }
    }

    @Test
    public void testFavoriteWordsSearchWithResultsToggleWord() throws IOException {
        authenticateUser(context, testUser);

        List<Word> words = getAllWords();
        int randomPosition = getRandomInteger(0, words.size() - 1);
        Word word = words.get(randomPosition);

        activityScenario = ActivityScenario.launch(MainActivity.class);

        browseToActivity();

        onView(withId(R.id.search_recycler_view))
                .perform(scrollToPosition(randomPosition))
                .perform(actionOnItemAtPosition(randomPosition, clickChildViewWithId(R.id.ic_heart)));

        word.setFavorite(false);

        assertSearchResult(word, randomPosition, "", true);
        // Checking if the word is now a favorite
        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        Word wordUpdated = wordDao.getRecordById(word.getId());
        assertFalse(wordUpdated.getFavorite());
    }

    @Test
    public void testFavoriteWordsMeaningOfWord() throws IOException {
        authenticateUser(context, testUser);

        List<Word> words = getAllWords();
        int randomPosition = getRandomInteger(0, words.size() - 1);
        Word randomWord = words.get(randomPosition);

        MeaningDao meaningDao = AppDatabase.getInstance(context).getMeaningDao();
        List<Meaning> meanings = meaningDao.getMeaningsOfWord(randomWord.getId());

        activityScenario = ActivityScenario.launch(MainActivity.class);

        browseToActivity();

        onView(withId(R.id.search_recycler_view))
                .perform(scrollToPosition(randomPosition))
                .perform(actionOnItemAtPosition(randomPosition, click()));

        onView(withText(randomWord.getWordName())).check(matches(isDisplayed()));

        Matcher<View>  meaningsRecyclerViewMatcher = allOf(hasLength(meanings.size()), isDisplayed());
        onView(withId(R.id.meanings_recycler_view))
                .check(matches(meaningsRecyclerViewMatcher));

        for (int i = 0;i < meanings.size();i++){
            Meaning meaning = meanings.get(i);
            assertMeaning(meaning, i);
        }

        Matcher<View> favoriteWordButtonMatcher = allOf(withText(R.string.remove_from_favorite_words), isDisplayed());
        onView(withId(R.id.favorite_word_button))
                .check(matches(favoriteWordButtonMatcher));
    }

    @Test
    public void testFavoriteWordsMeaningOfWordToggleWord() throws IOException {
        authenticateUser(context, testUser);

        List<Word> words = getAllWords();
        int randomPosition = getRandomInteger(0, words.size() - 1);
        Word word = words.get(randomPosition);

        activityScenario = ActivityScenario.launch(MainActivity.class);

        browseToActivity();

        // Checking that the results are correctly displayed
        onView(withId(R.id.search_recycler_view))
                .perform(scrollToPosition(randomPosition))
                .perform(actionOnItemAtPosition(randomPosition, click()));

        onView(withId(R.id.meanings_recycler_view))
                .check(matches(isDisplayed()));
        onView(withText(word.getWordName()))
                .check(matches(isDisplayed()));

        onView(withId(R.id.favorite_word_button)).perform(click());
        // Checking if the word has been toggled in the database
        Matcher<View> loadingButtonMatcher = allOf(withText(R.string.add_to_favorite_words), isDisplayed());
        onView(withId(R.id.favorite_word_button))
                .check(matches(loadingButtonMatcher));

        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        Word wordUpdated = wordDao.getRecordById(word.getId());
        assertFalse(wordUpdated.getFavorite());
    }

    @Override
    protected void browseToActivity() {
        onView(withId(R.id.action_menu))
                .perform(click());
        onView(withId(R.id.menu_item_profile))
                .perform(click());
        onView(withId(R.id.favorite_button))
                .perform(click());
    }

    @Override
    protected List<Word> searchWords(String searchPattern, Language language) {
        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();

        if (language.getLanguageName().equals(context.getString(R.string.all_languages_spinner_option))){
            return wordDao.searchFavoriteWords("%"+searchPattern+"%");
        }else{
            return wordDao.searchFavoriteWords("%"+searchPattern+"%", language.getLanguageName());
        }
    }
}
