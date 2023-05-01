package com.example.pajelingo.tests.abstract_tests;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.CustomMatchers.atPosition;
import static com.example.pajelingo.utils.CustomViewActions.expandSpinner;
import static com.example.pajelingo.utils.CustomViewActions.waitUntil;
import static com.example.pajelingo.utils.RandomTools.getRandomAlphabeticalString;
import static com.example.pajelingo.utils.RandomTools.getRandomInteger;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import android.view.View;

import com.example.pajelingo.R;
import com.example.pajelingo.daos.LanguageDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Meaning;
import com.example.pajelingo.models.Word;

import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.List;

public abstract class WordListActivityTest extends UITests {
    protected abstract void browseToActivity();
    /**
     * Search in the database all the words in a given language matching the specified search pattern.
     * @param searchPattern search string.
     * @param language concerned language.
     * @return all the words in the specified language matching the search string.
     */
    protected abstract List<Word> searchWords(String searchPattern, Language language);

    protected List<Word> getAllWords(){
        Language allLanguages = new Language(context.getString(R.string.all_languages_spinner_option));
        return searchWords("", allLanguages);
    }

    /**
     * Returns a search pattern that matches or does not match words in the specified language.
     * @param language search language.
     * @param hasResults boolean indicating if the search pattern must match any word in the specified language.
     * @return a search string that produces the specified result.
     */
    protected String getSearchPattern(Language language, boolean hasResults){
        int length = hasResults?1:8;

        String searchPattern = getRandomAlphabeticalString(length);
        List<Word> words = new ArrayList<>();

        while (words.isEmpty() == hasResults){
            searchPattern = getRandomAlphabeticalString(length);
            words = searchWords(searchPattern, language);
        }

        return searchPattern;
    }

    protected Language getRandomLanguage(){
        LanguageDao languageDao = AppDatabase.getInstance(context).getLanguageDao();
        List<Language> languages = languageDao.getAllRecords();
        languages.add(new Language(context.getString(R.string.all_languages_spinner_option)));
        return languages.get(getRandomInteger(0, languages.size() - 1));
    }

    protected void assertFilterWordsForm() {
        Matcher<View> searchEditTextMatcher = allOf(withHint(R.string.search_edit_text_hint), isDisplayed());
        onView(withId(R.id.search_edit_text))
                .check(matches(searchEditTextMatcher));

        Matcher<View> languageInputLabelMatcher = allOf(withId(R.id.label), withText(R.string.choose_language_label), isDisplayed());
        Matcher<View> languageInputMatcher = allOf(hasDescendant(languageInputLabelMatcher), isDisplayed());
        onView(withId(R.id.language_input))
                .check(matches(languageInputMatcher));

        Matcher<View> searchButtonMatcher = allOf(withText(R.string.search_button_text), isDisplayed());
        onView(withId(R.id.search_button))
                .check(matches(searchButtonMatcher));
    }

    protected void assertNoResults() {
        // Checking that loading image and text are shown
        Matcher<View> warningTextView = allOf(withText(R.string.loading_message), isDisplayed());
        onView(withId(R.id.warning_text_view))
                .check(matches(warningTextView));
        onView(withId(R.id.warning_image_view))
                .check(matches(isDisplayed()));
        // Checking that no results image and message are shown
        warningTextView = allOf(withId(R.id.warning_text_view), withText(R.string.no_results_message));
        onView(isRoot())
                .perform(waitUntil(warningTextView, 5000, true));
        onView(withId(R.id.warning_image_view))
                .check(matches(isDisplayed()));
    }

    protected void assertSearchResult(Word word, int position, String searchPattern, boolean hasFavoriteIcon) {
        Matcher<View> matcherTextView = allOf(
                withId(R.id.word_name_text_view),
                withText(word.getWordName()),
                withText(containsStringIgnoringCase(searchPattern)));

        Matcher<View> matcherHeartIcon = hasFavoriteIcon?
                allOf(withId(R.id.ic_heart), isDisplayed()):
                allOf(withId(R.id.ic_heart), not(isDisplayed()));

        onView(withId(R.id.search_recycler_view))
                .perform(scrollToPosition(position))
                .check(matches(atPosition(hasDescendant(matcherTextView), position)))
                .check(matches(atPosition(hasDescendant(matcherHeartIcon), position)));
    }

    protected void assertMeaning(Meaning meaning, int position) {
        Matcher<View> meaningView = allOf(withId(R.id.meaning_text_view), withText(meaning.getMeaning()));

        onView(withId(R.id.meanings_recycler_view))
                .perform(scrollToPosition(position))
                .check(matches(atPosition(hasDescendant(meaningView), position)));
    }

    protected void performSearch(String searchPattern, Language language){
        onView(withId(R.id.action_filter))
                .perform(click());
        // Type pattern to search
        onView(withId(R.id.search_edit_text))
                .perform(typeText(searchPattern), closeSoftKeyboard());
        // Select language
        onView(withId(R.id.language_input))
                .perform(expandSpinner());
        onData(is(language))
                .inRoot(isPlatformPopup())
                .perform(click());
        onView(withId(R.id.search_button))
                .perform(click());
    }

    protected void assertLoadingPage() {
        // Checking that loading image and text are shown
        Matcher<View> warningTextViewMatcher = allOf(withText(R.string.loading_message), isDisplayed());
        onView(withId(R.id.warning_text_view))
                .check(matches(warningTextViewMatcher));
        onView(withId(R.id.warning_image_view))
                .check(matches(isDisplayed()));
        // Checking that loading image and message disappear when the results are shown
        warningTextViewMatcher = allOf(withId(R.id.warning_text_view), withText(R.string.loading_message));
        onView(isRoot())
                .perform(waitUntil(warningTextViewMatcher, 5000, false));
    }
}
