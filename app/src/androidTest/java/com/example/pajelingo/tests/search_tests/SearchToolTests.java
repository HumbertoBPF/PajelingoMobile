package com.example.pajelingo.tests.search_tests;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.CustomMatchers.hasLabel;
import static com.example.pajelingo.utils.CustomMatchers.hasLength;
import static com.example.pajelingo.utils.CustomMatchers.isMeaningAtPosition;
import static com.example.pajelingo.utils.CustomMatchers.isWordAtPosition;
import static com.example.pajelingo.utils.CustomMatchers.searchResultsMatchPattern;
import static com.example.pajelingo.utils.CustomViewActions.clickChildViewWithId;
import static com.example.pajelingo.utils.CustomViewActions.expandSpinner;
import static com.example.pajelingo.utils.CustomViewActions.waitUntil;
import static com.example.pajelingo.utils.RandomTools.getRandomAlphabeticalString;
import static com.example.pajelingo.utils.RandomTools.getRandomInteger;
import static com.example.pajelingo.utils.TestTools.authenticateUser;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

import androidx.test.core.app.ActivityScenario;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MainActivity;
import com.example.pajelingo.daos.LanguageDao;
import com.example.pajelingo.daos.MeaningDao;
import com.example.pajelingo.daos.WordDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Meaning;
import com.example.pajelingo.models.Word;
import com.example.pajelingo.tests.abstract_tests.UITests;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchToolTests extends UITests {
    @Test
    public void testRenderingSearchActivity(){
        activityScenario = ActivityScenario.launch(MainActivity.class);
        String labelLanguageSpinner = context.getString(R.string.choose_language_label);

        onView(withId(R.id.search_button)).perform(click());
        onView(withId(R.id.action_filter)).perform(click());
        onView(withId(R.id.search_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.language_input)).check(matches(isDisplayed())).check(matches(hasLabel(labelLanguageSpinner)));
        onView(allOf(withId(R.id.search_button), withText(R.string.search_button_text))).check(matches(isDisplayed()));
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
    public void testSearchWithResultsFavoriteWord() throws IOException, InterruptedException {
        authenticateUser(context, testUser);

        List<Word> words = getAllWords();
        int randomPosition = getRandomInteger(0, words.size() - 1);
        Word word = words.get(randomPosition);
        boolean wasFavorite = word.getFavorite();

        activityScenario = ActivityScenario.launch(MainActivity.class);

        browseToSearchActivity();

        onView(withId(R.id.search_recycler_view))
                .perform(scrollToPosition(randomPosition))
                .perform(actionOnItemAtPosition(randomPosition, clickChildViewWithId(R.id.ic_heart)));
        Thread.sleep(5000);
        // Checking if the word is now a favorite
        words = searchWords("", new Language(context.getString(R.string.all_languages_spinner_option)));
        assertEquals(words.get(randomPosition).getFavorite(), !wasFavorite);

        onView(withId(R.id.search_recycler_view))
                .perform(scrollToPosition(randomPosition))
                .check(matches(isWordAtPosition(word, randomPosition, !wasFavorite)));
    }

    @Test
    public void testSearchWithNoResults(){
        Language randomLanguage = getRandomLanguage();
        String searchPattern = getSearchPattern(randomLanguage, false);

        activityScenario = ActivityScenario.launch(MainActivity.class);

        performSearch(searchPattern, randomLanguage);
        // Checking that loading image and text are shown
        onView(allOf(withId(R.id.warning_text_view), withText(R.string.loading_message))).check(matches(isDisplayed()));
        onView(withId(R.id.warning_image_view)).check(matches(isDisplayed()));
        // Checking that no results image and message are shown
        onView(isRoot()).perform(waitUntil(allOf(withId(R.id.warning_text_view), withText(R.string.no_results_message)), 5000, true));
        onView(withId(R.id.warning_image_view)).check(matches(isDisplayed()));
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
    public void testMeaningOfWordFavoriteWord() throws IOException {
        authenticateUser(context, testUser);

        List<Word> words = getAllWords();
        int randomPosition = getRandomInteger(0, words.size() - 1);
        Word word = words.get(randomPosition);
        boolean wasFavorite = word.getFavorite();

        activityScenario = ActivityScenario.launch(MainActivity.class);

        browseToSearchActivity();

        onView(withId(R.id.search_recycler_view))
                .perform(scrollToPosition(randomPosition))
                .perform(actionOnItemAtPosition(randomPosition, click()));

        onView(withId(R.id.meanings_recycler_view)).check(matches(isDisplayed()));
        onView(withText(word.getWordName())).check(matches(isDisplayed()));

        onView(withId(R.id.favorite_word_button)).perform(click());
        // Checking if the word is now a favorite
        int expectedStringResource = wasFavorite?R.string.add_to_favorite_words:R.string.remove_from_favorite_words;
        onView(isRoot()).perform(waitUntil(allOf(withId(R.id.favorite_word_button), withText(expectedStringResource)), 5000, true));
        words = getAllWords();
        assert words.get(randomPosition).getFavorite() == !wasFavorite;
    }

    private void searchWithResults(boolean isAuthenticated) throws IOException {
        if (isAuthenticated) {
            authenticateUser(context, testUser);
        }

        Language randomLanguage = getRandomLanguage();
        String searchPattern = getSearchPattern(randomLanguage, true);
        List<Word> words = searchWords(searchPattern, randomLanguage);

        activityScenario = ActivityScenario.launch(MainActivity.class);

        performSearch(searchPattern, randomLanguage);
        assertLoadingPage();
        // Checking that the results are correctly displayed
        onView(withId(R.id.search_recycler_view))
                .check(matches(isDisplayed()))
                .check(matches(hasLength(words.size())));
        onView(withId(R.id.search_recycler_view)).check(matches(searchResultsMatchPattern(searchPattern)));

        for (int i = 0;i < words.size();i++){
            Word word = words.get(i);
            onView(withId(R.id.search_recycler_view))
                    .perform(scrollToPosition(i))
                    .check(matches(isWordAtPosition(word, i, isAuthenticated)));
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

        browseToSearchActivity();

        onView(withId(R.id.search_recycler_view))
                .perform(scrollToPosition(randomPosition))
                .perform(actionOnItemAtPosition(randomPosition, click()));

        onView(withText(randomWord.getWordName())).check(matches(isDisplayed()));
        onView(withId(R.id.meanings_recycler_view))
                .check(matches(isDisplayed()))
                .check(matches(hasLength(meanings.size())));

        for (int i = 0;i < meanings.size();i++){
            Meaning meaning = meanings.get(i);
            onView(withId(R.id.meanings_recycler_view))
                    .perform(scrollToPosition(i))
                    .check(matches(isMeaningAtPosition(meaning, i)));
        }

        if (isAuthenticated) {
            boolean isFavorite = randomWord.getFavorite();
            int expectedStringResource = isFavorite?R.string.remove_from_favorite_words: R.string.add_to_favorite_words;
            onView(allOf(withId(R.id.favorite_word_button), withText(expectedStringResource))).check(matches(isDisplayed()));
        }else {
            onView(withId(R.id.favorite_word_button)).check(matches(not(isDisplayed())));
        }
    }

    private Language getRandomLanguage(){
        LanguageDao languageDao = AppDatabase.getInstance(context).getLanguageDao();
        List<Language> languages = languageDao.getAllRecords();
        languages.add(new Language(context.getString(R.string.all_languages_spinner_option)));
        return languages.get(getRandomInteger(0, languages.size() - 1));
    }

    private List<Word> getAllWords(){
        Language allLanguages = new Language(context.getString(R.string.all_languages_spinner_option));
        return searchWords("", allLanguages);
    }

    /**
     * Returns a search pattern that matches or does not match words in the specified language.
     * @param language search language.
     * @param hasResults boolean indicating if the search pattern must match any word in the specified language.
     * @return a search string that produces the specified result.
     */
    private String getSearchPattern(Language language, boolean hasResults){
        int length = hasResults?1:8;

        String searchPattern = getRandomAlphabeticalString(length);
        List<Word> words = new ArrayList<>();

        while (words.isEmpty() == hasResults){
            searchPattern = getRandomAlphabeticalString(length);
            words = searchWords(searchPattern, language);
        }

        return searchPattern;
    }

    /**
     * Search in the database all the words in a given language matching the specified search pattern.
     * @param searchPattern search string.
     * @param language concerned language.
     * @return all the words in the specified language matching the search string.
     */
    private List<Word> searchWords(String searchPattern, Language language){
        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();

        if (language.getLanguageName().equals(context.getString(R.string.all_languages_spinner_option))){
            return wordDao.searchWords("%"+searchPattern+"%");
        }else{
            return wordDao.searchWords("%"+searchPattern+"%", language.getLanguageName());
        }
    }

    private void performSearch(String searchPattern, Language language){
        onView(withId(R.id.search_button)).perform(click());
        onView(withId(R.id.action_filter)).perform(click());
        // Type pattern to search
        onView(withId(R.id.search_edit_text)).perform(typeText(searchPattern), closeSoftKeyboard());
        // Select language
        onView(withId(R.id.language_input)).perform(expandSpinner());
        onData(is(language)).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.search_button)).perform(click());
    }

    private void browseToSearchActivity() {
        onView(withId(R.id.search_button)).perform(click());
        assertLoadingPage();
    }

    private void assertLoadingPage() {
        // Checking that loading image and text are shown
        onView(allOf(withId(R.id.warning_text_view), withText(R.string.loading_message))).check(matches(isDisplayed()));
        onView(withId(R.id.warning_image_view)).check(matches(isDisplayed()));
        // Checking that loading image and message disappear when the results are shown
        onView(isRoot())
                .perform(waitUntil(allOf(withId(R.id.warning_text_view), withText(R.string.loading_message)), 5000, false));
    }
}
