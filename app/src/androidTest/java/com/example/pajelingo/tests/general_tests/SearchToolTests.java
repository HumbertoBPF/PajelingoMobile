package com.example.pajelingo.tests.general_tests;

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
import static com.example.pajelingo.utils.CustomMatchers.isMeaningAtPosition;
import static com.example.pajelingo.utils.CustomMatchers.isWordAtPosition;
import static com.example.pajelingo.utils.CustomMatchers.searchResultsMatchPattern;
import static com.example.pajelingo.utils.CustomViewActions.expandSpinner;
import static com.example.pajelingo.utils.CustomViewActions.waitForView;
import static com.example.pajelingo.utils.TestTools.getRandomInteger;
import static com.example.pajelingo.utils.TestTools.getRandomString;
import static com.example.pajelingo.utils.TestTools.saveEntitiesFromAPI;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.util.Log;

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

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SearchToolTests extends UITests {
    @Before
    public void setUp() throws IOException {
        saveEntitiesFromAPI(languageSchoolAPI.getWords(), AppDatabase.getInstance(context).getWordDao());
        saveEntitiesFromAPI(languageSchoolAPI.getArticles(), AppDatabase.getInstance(context).getArticleDao());
        saveEntitiesFromAPI(languageSchoolAPI.getMeanings(), AppDatabase.getInstance(context).getMeaningDao());
    }

    @Test
    public void testRenderingSearchActivity(){
        activityScenario = ActivityScenario.launch(MainActivity.class);
        String labelLanguageSpinner = context.getString(R.string.choose_language_label);

        onView(withId(R.id.search_button)).perform(click());
        onView(withId(R.id.search_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.language_input)).check(matches(isDisplayed())).check(matches(hasLabel(labelLanguageSpinner)));
        onView(withId(R.id.search_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testRenderingSearchResults(){
        Language randomLanguage = getRandomLanguage();
        String searchPattern = getSearchPattern(randomLanguage, true);
        List<Word> words = searchWords(searchPattern, randomLanguage);

        activityScenario = ActivityScenario.launch(MainActivity.class);

        performSearch(randomLanguage, searchPattern);
        // Checking that loading image and text are shown
        onView(allOf(withId(R.id.warning_text_view), withText(R.string.loading_message))).check(matches(isDisplayed()));
        onView(withId(R.id.warning_image_view)).check(matches(isDisplayed()));
        // Checking that loading image and message disappear when the results are shown
        onView(isRoot()).perform(waitForView(withId(R.id.warning_image_view), 5000, false));
        // Checking that the results are correctly displayed
        onView(withId(R.id.search_recycler_view)).check(matches(isDisplayed()));
        onView(withId(R.id.search_recycler_view)).check(matches(searchResultsMatchPattern(searchPattern)));

        for (int i = 0;i < words.size();i++){
            Word word = words.get(i);
            onView(withId(R.id.search_recycler_view))
                    .perform(scrollToPosition(i))
                    .check(matches(isWordAtPosition(word, i)));
        }
    }

    @Test
    public void testRenderingNoResults(){
        Language randomLanguage = getRandomLanguage();
        String searchPattern = getSearchPattern(randomLanguage, false);

        activityScenario = ActivityScenario.launch(MainActivity.class);

        performSearch(randomLanguage, searchPattern);
        // Checking that loading image and text are shown
        onView(allOf(withId(R.id.warning_text_view), withText(R.string.loading_message))).check(matches(isDisplayed()));
        onView(withId(R.id.warning_image_view)).check(matches(isDisplayed()));
        // Checking that no results image and message are shown
        onView(isRoot()).perform(waitForView(allOf(withId(R.id.warning_text_view), withText(R.string.no_results_message)), 5000, true));
        onView(withId(R.id.warning_image_view)).check(matches(isDisplayed()));

    }

    @Test
    public void testRenderingMeaningOfWord(){
        MeaningDao meaningDao = AppDatabase.getInstance(context).getMeaningDao();

        Language randomLanguage = getRandomLanguage();
        String searchPattern = getSearchPattern(randomLanguage, true);
        List<Word> words = searchWords(searchPattern, randomLanguage);

        int randomPosition = new Random().nextInt(words.size());
        Word randomWord = words.get(randomPosition);

        String wordName = randomWord.getWordName();

        List<Meaning> meanings = meaningDao.getMeaningsOfWord(randomWord.getId());

        activityScenario = ActivityScenario.launch(MainActivity.class);

        performSearch(randomLanguage, searchPattern);
        // Checking that loading image and text are shown
        onView(allOf(withId(R.id.warning_text_view), withText(R.string.loading_message))).check(matches(isDisplayed()));
        onView(withId(R.id.warning_image_view)).check(matches(isDisplayed()));
        // Checking that loading image and message disappear when the results are shown
        onView(isRoot()).perform(waitForView(withId(R.id.warning_image_view), 5000, false));

        onView(withId(R.id.search_recycler_view)).perform(actionOnItemAtPosition(randomPosition, click()));
        onView(withId(R.id.meanings_recycler_view)).check(matches(isDisplayed()));
        onView(withText(wordName)).check(matches(isDisplayed()));

        for (int i = 0;i < meanings.size();i++){
            Meaning meaning = meanings.get(i);
            onView(withId(R.id.meanings_recycler_view))
                    .perform(scrollToPosition(i))
                    .check(matches(isMeaningAtPosition(meaning, i)));
        }
    }

    private Language getRandomLanguage(){
        LanguageDao languageDao = AppDatabase.getInstance(context).getLanguageDao();
        List<Language> languages = languageDao.getAllRecords();
        languages.add(new Language(context.getString(R.string.all_languages_spinner_option)));
        return languages.get(getRandomInteger(0, languages.size() - 1));
    }

    private String getSearchPattern(Language language, boolean hasResults){
        int length = hasResults?1:8;

        String searchPattern = getRandomString(length);
        List<Word> words = new ArrayList<>();

        while (words.isEmpty() == hasResults){
            Log.i("searchPattern", searchPattern);
            searchPattern = getRandomString(length);
            words = searchWords(searchPattern, language);
        }

        return searchPattern;
    }

    private List<Word> searchWords(String searchPattern, Language language){
        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();

        if (language.getLanguageName().equals(context.getString(R.string.all_languages_spinner_option))){
            return wordDao.searchWords("%"+searchPattern+"%");
        }else{
            return wordDao.searchWords("%"+searchPattern+"%", language.getLanguageName());
        }
    }

    private void performSearch(Language language, String searchPattern){
        onView(withId(R.id.search_button)).perform(click());
        // Type pattern to search
        onView(withId(R.id.search_edit_text)).perform(typeText(searchPattern), closeSoftKeyboard());
        // Select language
        onView(withId(R.id.language_input)).perform(expandSpinner());
        onData(is(language)).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.search_button)).perform(click());
    }
}
