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
import static com.example.pajelingo.utils.CustomMatchers.getLabeledSpinnerMatcher;
import static com.example.pajelingo.utils.CustomMatchers.isTextViewWordInList;
import static com.example.pajelingo.utils.CustomViewActions.expandSpinner;
import static com.example.pajelingo.utils.CustomViewActions.inputVocabularyGameAnswer;
import static com.example.pajelingo.utils.RandomTools.getRandomLanguage;
import static com.example.pajelingo.utils.RetrofitTools.saveEntitiesFromAPI;
import static com.example.pajelingo.utils.SharedPreferences.saveUserData;
import static com.example.pajelingo.utils.TestTools.authenticateUser;
import static com.example.pajelingo.utils.TestTools.getScore;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;

import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.RecyclerViewActions;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MainActivity;
import com.example.pajelingo.daos.WordDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Word;
import com.example.pajelingo.tests.abstract_tests.GameActivityTests;

import org.hamcrest.Matcher;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class VocabularyGameActivityTests extends GameActivityTests {
    @Override
    public void setUp() throws IOException {
        super.setUp();
        game = AppDatabase.getInstance(context).getGameDao().getGameByName("Vocabulary Training");
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

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupVocabularyGame(baseLanguage, targetLanguage);

        onView(withId(R.id.word_text_view)).check(matches(isTextViewWordInList(wordsInLanguage)));

        Matcher<View> answerInputMatcher = allOf(withHint(answerInputHint), isDisplayed());
        onView(withId(R.id.answer_input))
                .check(matches(answerInputMatcher));

        Matcher<View> checkButtonMatcher = allOf(withText(R.string.check_button_text), isDisplayed());
        onView(withId(R.id.check_button))
                .check(matches(checkButtonMatcher));
    }

    @Test
    public void testCorrectAnswerWithoutAuthenticationVocabularyGame() {
        Language baseLanguage = getRandomLanguage(context);
        Language targetLanguage = getRandomLanguage(context, Objects.requireNonNull(baseLanguage).getLanguageName());

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupVocabularyGame(baseLanguage, targetLanguage);

        inputAnswerAndCheckFeedback(baseLanguage, targetLanguage, true);
    }

    @Test
    public void testWrongAnswerWithoutAuthenticationVocabularyGame() {
        Language baseLanguage = getRandomLanguage(context);
        Language targetLanguage = getRandomLanguage(context, Objects.requireNonNull(baseLanguage).getLanguageName());

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupVocabularyGame(baseLanguage, targetLanguage);

        inputAnswerAndCheckFeedback(baseLanguage, targetLanguage, false);
    }

    @Test
    public void testTwoCorrectAnswersWithAuthenticationVocabularyGame() throws IOException {
        authenticateUser(context, testUser);

        Language baseLanguage = getRandomLanguage(context);
        Language targetLanguage = getRandomLanguage(context, Objects.requireNonNull(baseLanguage).getLanguageName());

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupVocabularyGame(baseLanguage, targetLanguage);

        Long score1 = getScore(testUser, Objects.requireNonNull(targetLanguage), game.getId());

        inputAnswerAndCheckFeedback(baseLanguage, targetLanguage, true);

        Long score2 = getScore(testUser, Objects.requireNonNull(targetLanguage), game.getId());
        assertEquals(score1 + 1L, score2.longValue());

        onView(withId(R.id.new_word_button)).perform(click());
        inputAnswerAndCheckFeedback(baseLanguage, targetLanguage, true);

        Long score3 = getScore(testUser, Objects.requireNonNull(targetLanguage), game.getId());
        assertEquals(score2 + 1L, score3.longValue());
    }

    @Test
    public void testOneWrongAnswerOneCorrectAnswerWithAuthenticationVocabularyGame() throws IOException {
        authenticateUser(context, testUser);

        Language baseLanguage = getRandomLanguage(context);
        Language targetLanguage = getRandomLanguage(context, Objects.requireNonNull(baseLanguage).getLanguageName());

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupVocabularyGame(baseLanguage, targetLanguage);

        Long score1 = getScore(testUser, Objects.requireNonNull(targetLanguage), game.getId());

        inputAnswerAndCheckFeedback(baseLanguage, targetLanguage, false);

        Long score2 = getScore(testUser, Objects.requireNonNull(targetLanguage), game.getId());
        assertEquals(score1, score2);

        onView(withId(R.id.new_word_button)).perform(click());
        inputAnswerAndCheckFeedback(baseLanguage, targetLanguage, true);

        Long score3 = getScore(testUser, Objects.requireNonNull(targetLanguage), game.getId());
        assertEquals(score2 + 1L, score3.longValue());
    }

    @Test
    public void testOneCorrectAnswerOneWrongAnswerWithAuthenticationArticleGame() throws IOException {
        authenticateUser(context, testUser);

        Language baseLanguage = getRandomLanguage(context);
        Language targetLanguage = getRandomLanguage(context, Objects.requireNonNull(baseLanguage).getLanguageName());

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupVocabularyGame(baseLanguage, targetLanguage);

        Long score1 = getScore(testUser, Objects.requireNonNull(targetLanguage), game.getId());

        inputAnswerAndCheckFeedback(baseLanguage, targetLanguage, true);

        Long score2 = getScore(testUser, Objects.requireNonNull(targetLanguage), game.getId());
        assertEquals(score1 + 1L, score2.longValue());

        onView(withId(R.id.new_word_button)).perform(click());
        inputAnswerAndCheckFeedback(baseLanguage, targetLanguage, false);

        Long score3 = getScore(testUser, Objects.requireNonNull(targetLanguage), game.getId());
        assertEquals(score2, score3);
    }

    @Test
    public void testTwoWrongAnswersWithAuthenticationArticleGame() throws IOException {
        saveUserData(context, testUser);

        Language baseLanguage = getRandomLanguage(context);
        Language targetLanguage = getRandomLanguage(context, Objects.requireNonNull(baseLanguage).getLanguageName());

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupVocabularyGame(baseLanguage, targetLanguage);

        Long score1 = getScore(testUser, Objects.requireNonNull(targetLanguage), game.getId());

        inputAnswerAndCheckFeedback(baseLanguage, targetLanguage, false);

        Long score2 = getScore(testUser, Objects.requireNonNull(targetLanguage), game.getId());
        assertEquals(score1, score2);

        onView(withId(R.id.new_word_button)).perform(click());
        inputAnswerAndCheckFeedback(baseLanguage, targetLanguage, false);

        Long score3 = getScore(testUser, Objects.requireNonNull(targetLanguage), game.getId());
        assertEquals(score2, score3);
    }

    @Test
    public void testVocabularyGameWithoutLanguageData() throws IOException {
        AppDatabase.getInstance(context).clearAllTables();
        saveEntitiesFromAPI(languageSchoolAPI.getGames(), AppDatabase.getInstance(context).getGameDao());

        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.games_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        assertIsMainActivity(true);
    }

    @Test
    public void testVocabularyGameWithoutWordData() throws IOException {
        AppDatabase.getInstance(context).clearAllTables();
        saveEntitiesFromAPI(languageSchoolAPI.getGames(), AppDatabase.getInstance(context).getGameDao());
        saveEntitiesFromAPI(languageSchoolAPI.getLanguages(), AppDatabase.getInstance(context).getLanguageDao());

        Language baseLanguage = getRandomLanguage(context);
        Language targetLanguage = getRandomLanguage(context, Objects.requireNonNull(baseLanguage).getLanguageName());

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupVocabularyGame(baseLanguage, targetLanguage);
        assertIsMainActivity(true);
    }

    /**
     * Inputs an answer and checks if it is correct or wrong.
     * @param baseLanguage selected base language
     * @param targetLanguage selected target language
     * @param isCorrect boolean indicating if the answer is correct or wrong
     */
    private void inputAnswerAndCheckFeedback(Language baseLanguage, Language targetLanguage, boolean isCorrect) {
        String expectedFeedback =  isCorrect?"Correct :)":"Wrong answer";
        onView(isRoot()).perform(inputVocabularyGameAnswer(context, baseLanguage, targetLanguage, isCorrect));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(withText(startsWith(expectedFeedback))));
    }

    /**
     * Sets the base and target languages, and launches the game.
     * @param baseLanguage base language (the translation provided by the player must be in this language).
     * @param targetLanguage target language (the word to be translated are in this language).
     */
    private void setupVocabularyGame(Language baseLanguage, Language targetLanguage) {
        onView(withId(R.id.games_recycler_view)).perform(actionOnItemAtPosition(0, click()));
        onView(withId(R.id.base_language_input)).perform(expandSpinner());
        onData(is(baseLanguage)).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.target_language_input)).perform(expandSpinner());
        onData(is(targetLanguage)).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.play_button)).perform(click());
    }

    /**
     * Verifies if the setup layout is displayed.
     */
    private void assertRenderingSetupLayout(){
        Matcher<View> baseLanguageInputMatcher = getLabeledSpinnerMatcher(R.string.base_language_label);
        onView(withId(R.id.base_language_input))
                .check(matches(baseLanguageInputMatcher));

        Matcher<View> instructionsTextViewMatcher = allOf(withText(game.getInstructions()), isDisplayed());
        onView(withId(R.id.instructions_text_view))
                .check(matches(instructionsTextViewMatcher));

        Matcher<View> targetLanguageInputMatcher = getLabeledSpinnerMatcher(R.string.target_language_label);
        onView(withId(R.id.target_language_input))
                .check(matches(targetLanguageInputMatcher));

        Matcher<View> playButton = allOf(withText(R.string.play_button_text), isDisplayed());
        onView(withId(R.id.play_button))
                .check(matches(playButton));
    }
}
