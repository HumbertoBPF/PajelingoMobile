package com.example.pajelingo.tests.search_tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.CustomMatchers.hasLength;
import static com.example.pajelingo.utils.CustomViewActions.clickChildViewWithId;
import static com.example.pajelingo.utils.CustomViewActions.waitUntil;
import static com.example.pajelingo.utils.RandomTools.getRandomInteger;
import static com.example.pajelingo.utils.TestTools.authenticateUser;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

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

public class SearchToolTests extends WordListActivityTest {
    @Test
    public void testRenderingSearchActivityFilters(){
        activityScenario = ActivityScenario.launch(MainActivity.class);

        browseToActivity();
        onView(withId(R.id.action_filter))
                .perform(click());
        assertFilterWordsForm();
    }

    @Test
    public void testSearchWithResultsNonAuthenticatedUser() throws IOException {
        searchWithResults(false);
    }

    @Test
    public void testSearchWithResultsAuthenticatedUser() throws IOException {
        searchWithResults(true);
    }

    @Test
    public void testSearchWithResultsToggleWord() throws IOException, InterruptedException {
        authenticateUser(context, testUser);

        List<Word> words = getAllWords();
        int randomPosition = getRandomInteger(0, words.size() - 1);
        Word word = words.get(randomPosition);
        boolean wasFavorite = word.getFavorite();

        activityScenario = ActivityScenario.launch(MainActivity.class);

        browseToActivity();

        onView(withId(R.id.search_recycler_view))
                .perform(scrollToPosition(randomPosition))
                .perform(actionOnItemAtPosition(randomPosition, clickChildViewWithId(R.id.ic_heart)));
        Thread.sleep(5000);
        // Checking if the word is now a favorite
        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        Word wordUpdated = wordDao.getRecordById(word.getId());
        assertEquals(wordUpdated.getFavorite(), !wasFavorite);
        assertSearchResult(wordUpdated, randomPosition, "", true);
    }

    @Test
    public void testSearchWithNoResults(){
        Language randomLanguage = getRandomLanguage();
        String searchPattern = getSearchPattern(randomLanguage, false);

        activityScenario = ActivityScenario.launch(MainActivity.class);

        browseToActivity();
        performSearch(searchPattern, randomLanguage);
        assertNoResults();
    }

    @Test
    public void testMeaningOfWordNonAuthenticatedUser() throws IOException {
        meaningOfWord(false);
    }

    @Test
    public void testMeaningOfWordAuthenticatedUser() throws IOException {
        meaningOfWord(true);
    }

    @Test
    public void testMeaningOfWordToggleWord() throws IOException {
        authenticateUser(context, testUser);

        List<Word> words = getAllWords();
        int randomPosition = getRandomInteger(0, words.size() - 1);
        Word word = words.get(randomPosition);
        boolean wasFavorite = word.getFavorite();

        activityScenario = ActivityScenario.launch(MainActivity.class);

        browseToActivity();

        onView(withId(R.id.search_recycler_view))
                .perform(scrollToPosition(randomPosition))
                .perform(actionOnItemAtPosition(randomPosition, click()));

        onView(withId(R.id.meanings_recycler_view))
                .check(matches(isDisplayed()));
        onView(withText(word.getWordName()))
                .check(matches(isDisplayed()));

        onView(withId(R.id.favorite_word_button)).perform(click());
        // Checking if the word has been toggled in the database
        int expectedStringResource = wasFavorite?R.string.add_to_favorite_words:R.string.remove_from_favorite_words;

        Matcher<View> favoriteWordButtonMatcher = allOf(withId(R.id.favorite_word_button), withText(expectedStringResource), isDisplayed());
        onView(isRoot())
                .perform(waitUntil(favoriteWordButtonMatcher, 5000));

        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        Word wordUpdated = wordDao.getRecordById(word.getId());
        assertEquals(wordUpdated.getFavorite(), !wasFavorite);
    }

    private void searchWithResults(boolean isAuthenticated) throws IOException {
        if (isAuthenticated) {
            authenticateUser(context, testUser);
        }

        Language randomLanguage = getRandomLanguage();
        String searchPattern = getSearchPattern(randomLanguage, true);
        List<Word> words = searchWords(searchPattern, randomLanguage);

        activityScenario = ActivityScenario.launch(MainActivity.class);

        browseToActivity();
        performSearch(searchPattern, randomLanguage);
        assertLoadingPage();
        // Checking that the results are correctly displayed
        Matcher<View> searchRecyclerViewMatcher = allOf(hasLength(words.size()), isDisplayed());
        onView(withId(R.id.search_recycler_view))
                .check(matches(searchRecyclerViewMatcher));

        for (int i = 0;i < words.size();i++){
            Word word = words.get(i);
            assertSearchResult(word, i, searchPattern, isAuthenticated);
        }
    }

    private void meaningOfWord(boolean isAuthenticated) throws IOException {
        if (isAuthenticated) {
            authenticateUser(context, testUser);
        }

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

        onView(withText(randomWord.getWordName()))
                .check(matches(isDisplayed()));

        Matcher<View> meaningsRecyclerViewMatcher = allOf(hasLength(meanings.size()), isDisplayed());
        onView(withId(R.id.meanings_recycler_view))
                .check(matches(meaningsRecyclerViewMatcher));

        for (int i = 0;i < meanings.size();i++){
            Meaning meaning = meanings.get(i);
            assertMeaning(meaning, i);
        }

        if (isAuthenticated) {
            boolean isFavorite = randomWord.getFavorite();
            int expectedStringResource = isFavorite?R.string.remove_from_favorite_words: R.string.add_to_favorite_words;

            Matcher<View> favoriteWordButtonMatcher = allOf(withText(expectedStringResource), isDisplayed());
            onView(withId(R.id.favorite_word_button))
                    .check(matches(favoriteWordButtonMatcher));
        }else {
            onView(withId(R.id.favorite_word_button))
                    .check(matches(not(isDisplayed())));
        }
    }

    protected List<Word> searchWords(String searchPattern, Language language){
        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();

        if (language.getLanguageName().equals(context.getString(R.string.all_languages_spinner_option))){
            return wordDao.searchWords("%"+searchPattern+"%");
        }else{
            return wordDao.searchWords("%"+searchPattern+"%", language.getLanguageName());
        }
    }

    @Override
    protected void browseToActivity() {
        onView(withId(R.id.search_button))
                .perform(click());
        assertLoadingPage();
    }
}
