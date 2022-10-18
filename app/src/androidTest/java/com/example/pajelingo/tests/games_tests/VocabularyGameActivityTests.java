package com.example.pajelingo.tests.games_tests;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.Tools.saveStateAndUserCredentials;
import static com.example.pajelingo.utils.CustomMatchers.checkAnswerFeedback;
import static com.example.pajelingo.utils.CustomMatchers.isTextViewWordInList;
import static com.example.pajelingo.utils.CustomViewActions.inputVocabularyGameAnswer;
import static com.example.pajelingo.utils.TestTools.assertScoreValue;
import static com.example.pajelingo.utils.TestTools.getRandomLanguage;
import static com.example.pajelingo.utils.TestTools.saveEntitiesFromAPI;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.RecyclerViewActions;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MainActivity;
import com.example.pajelingo.daos.WordDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Word;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelperTest;
import com.example.pajelingo.tests.abstract_tests.GameActivityTests;

import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class VocabularyGameActivityTests extends GameActivityTests {
    @Override
    public void setUp() throws IOException {
        super.setUp();
        saveEntitiesFromAPI(languageSchoolAPITest.getWords(), AppDatabase.getInstance(context).getWordDao());
    }

    @Test
    public void testRenderingSetupVocabularyGame() {
        activityScenario = ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.games_recycler_view)).perform(actionOnItemAtPosition(0, click()));
        assertRenderingSetupLayout();
    }

    @Test
    public void testChooseLanguageSetupValidationVocabularyGame(){
        Language randomLanguage = getRandomLanguage(context);

        activityScenario = ActivityScenario.launch(MainActivity.class);
        setupVocabularyGame(randomLanguage, randomLanguage);
        assertRenderingSetupLayout();
    }

    @Test
    public void testChooseLanguageSetupVocabularyGame() {
        Language baseLanguage = getRandomLanguage(context);
        Language targetLanguage = getRandomLanguage(context, Objects.requireNonNull(baseLanguage).getLanguageName());

        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        List<Word> wordsInLanguage = wordDao.getWordsByLanguage(Objects.requireNonNull(targetLanguage).getLanguageName());

        String answerInputHint = context.getString(R.string.instruction_vocabulary_game)+baseLanguage.getLanguageName();
        String textCheckButton = context.getString(R.string.check_button_text);

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupVocabularyGame(baseLanguage, targetLanguage);

        onView(withId(R.id.word_text_view)).check(matches(isTextViewWordInList(wordsInLanguage)));
        onView(allOf(withId(R.id.answer_input), withHint(answerInputHint))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.check_button), withText(textCheckButton))).check(matches(isDisplayed()));
    }

    @Test
    public void testCorrectAnswerWithoutAuthenticationVocabularyGame(){
        Language baseLanguage = getRandomLanguage(context);
        Language targetLanguage = getRandomLanguage(context, Objects.requireNonNull(baseLanguage).getLanguageName());

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupVocabularyGame(baseLanguage, targetLanguage);

        onView(isRoot()).perform(inputVocabularyGameAnswer(context, baseLanguage, true));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(true)));
    }

    @Test
    public void testWrongAnswerWithoutAuthenticationVocabularyGame(){
        Language baseLanguage = getRandomLanguage(context);
        Language targetLanguage = getRandomLanguage(context, Objects.requireNonNull(baseLanguage).getLanguageName());

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupVocabularyGame(baseLanguage, targetLanguage);

        onView(isRoot()).perform(inputVocabularyGameAnswer(context, baseLanguage, false));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(false)));
    }

    @Test
    public void testTwoCorrectAnswersWithAuthenticationVocabularyGame() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, testUser.getUsername(), testUser.getPassword());

        Language baseLanguage = getRandomLanguage(context);
        Language targetLanguage = getRandomLanguage(context, Objects.requireNonNull(baseLanguage).getLanguageName());

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupVocabularyGame(baseLanguage, targetLanguage);

        onView(isRoot()).perform(inputVocabularyGameAnswer(context, baseLanguage, true));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(true)));

        assertScoreValue(testUser, Objects.requireNonNull(targetLanguage), "vocabulary_game", 1L);

        onView(withId(R.id.new_word_button)).perform(click());

        onView(isRoot()).perform(inputVocabularyGameAnswer(context, baseLanguage, true));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(true)));

        assertScoreValue(testUser, targetLanguage, "vocabulary_game", 2L);
    }

    @Test
    public void testOneWrongAnswerOneCorrectAnswerWithAuthenticationVocabularyGame() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, testUser.getUsername(), testUser.getPassword());

        Language baseLanguage = getRandomLanguage(context);
        Language targetLanguage = getRandomLanguage(context, Objects.requireNonNull(baseLanguage).getLanguageName());

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupVocabularyGame(baseLanguage, targetLanguage);

        onView(isRoot()).perform(inputVocabularyGameAnswer(context, baseLanguage, false));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(false)));

        assertScoreValue(testUser, Objects.requireNonNull(targetLanguage), "vocabulary_game", 0L);

        onView(withId(R.id.new_word_button)).perform(click());

        onView(isRoot()).perform(inputVocabularyGameAnswer(context, baseLanguage, true));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(true)));

        assertScoreValue(testUser, targetLanguage, "vocabulary_game", 1L);
    }

    @Test
    public void testOneCorrectAnswerOneWrongAnswerWithAuthenticationArticleGame() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, testUser.getUsername(), testUser.getPassword());

        Language baseLanguage = getRandomLanguage(context);
        Language targetLanguage = getRandomLanguage(context, Objects.requireNonNull(baseLanguage).getLanguageName());

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupVocabularyGame(baseLanguage, targetLanguage);

        onView(isRoot()).perform(inputVocabularyGameAnswer(context, baseLanguage, true));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(true)));

        assertScoreValue(testUser, Objects.requireNonNull(targetLanguage), "vocabulary_game", 1L);

        onView(withId(R.id.new_word_button)).perform(click());

        onView(isRoot()).perform(inputVocabularyGameAnswer(context, baseLanguage, false));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(false)));

        assertScoreValue(testUser, targetLanguage, "vocabulary_game", 1L);
    }

    @Test
    public void testTwoWrongAnswersWithAuthenticationArticleGame() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, testUser.getUsername(), testUser.getPassword());

        Language baseLanguage = getRandomLanguage(context);
        Language targetLanguage = getRandomLanguage(context, Objects.requireNonNull(baseLanguage).getLanguageName());

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupVocabularyGame(baseLanguage, targetLanguage);

        onView(isRoot()).perform(inputVocabularyGameAnswer(context, baseLanguage, false));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(false)));

        assertScoreValue(testUser, Objects.requireNonNull(targetLanguage), "vocabulary_game", 0L);

        onView(withId(R.id.new_word_button)).perform(click());

        onView(isRoot()).perform(inputVocabularyGameAnswer(context, baseLanguage, false));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(false)));

        assertScoreValue(testUser, targetLanguage, "vocabulary_game", 0L);
    }

    @Test
    public void testVocabularyGameWithoutLanguageData() throws IOException {
        AppDatabase.getInstance(context).clearAllTables();
        saveEntitiesFromAPI(LanguageSchoolAPIHelperTest.getApiObject().getGames(), AppDatabase.getInstance(context).getGameDao());

        activityScenario = ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.games_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        assertIsMenuActivity(true);
    }

    @Test
    public void testVocabularyGameWithoutWordData() throws IOException {
        AppDatabase.getInstance(context).clearAllTables();
        saveEntitiesFromAPI(LanguageSchoolAPIHelperTest.getApiObject().getGames(), AppDatabase.getInstance(context).getGameDao());
        saveEntitiesFromAPI(LanguageSchoolAPIHelperTest.getApiObject().getLanguages(), AppDatabase.getInstance(context).getLanguageDao());

        Language baseLanguage = getRandomLanguage(context);
        Language targetLanguage = getRandomLanguage(context, Objects.requireNonNull(baseLanguage).getLanguageName());

        activityScenario = ActivityScenario.launch(MainActivity.class);
        setupVocabularyGame(baseLanguage, targetLanguage);
        assertIsMenuActivity(true);
    }

    /**
     * Sets the base and target languages, and launches the game.
     * @param baseLanguage base language (the translation provided by the player must be in this language).
     * @param targetLanguage target language (the word to be translated are in this language).
     */
    private void setupVocabularyGame(Language baseLanguage, Language targetLanguage){
        onView(withId(R.id.games_recycler_view)).perform(actionOnItemAtPosition(0, click()));
        onView(withId(R.id.base_language_spinner)).perform(click());
        onData(is(baseLanguage)).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.target_language_spinner)).perform(click());
        onData(is(targetLanguage)).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.play_button)).perform(click());
    }

    /**
     * Verifies if the setup layout is displayed.
     */
    private void assertRenderingSetupLayout(){
        String labelBaseLanguageSpinner = context.getString(R.string.base_language_label);
        String labelTargetLanguageSpinner = context.getString(R.string.target_language_label);
        String textPlayButton = context.getString(R.string.play_button_text);

        onView(allOf(withId(R.id.label_base_language), withText(labelBaseLanguageSpinner))).check(matches(isDisplayed()));
        onView(withId(R.id.base_language_spinner)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.label_target_language), withText(labelTargetLanguageSpinner))).check(matches(isDisplayed()));
        onView(withId(R.id.target_language_spinner)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.play_button), withText(textPlayButton))).check(matches(isDisplayed()));
    }
}