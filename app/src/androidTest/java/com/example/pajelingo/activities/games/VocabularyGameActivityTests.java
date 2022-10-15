package com.example.pajelingo.activities.games;

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
import static com.example.pajelingo.util.Tools.saveStateAndUserCredentials;
import static com.example.pajelingo.utils.CustomMatchers.checkAnswerFeedback;
import static com.example.pajelingo.utils.CustomMatchers.isWordInLanguage;
import static com.example.pajelingo.utils.CustomViewActions.inputVocabularyGameAnswer;
import static com.example.pajelingo.utils.Tools.assertScoreValue;
import static com.example.pajelingo.utils.Tools.getRandomLanguage;
import static com.example.pajelingo.utils.Tools.saveEntitiesFromAPI;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import com.example.pajelingo.R;
import com.example.pajelingo.daos.WordDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Word;

import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class VocabularyGameActivityTests extends GameActivityTests{
    @Override
    public void setUp() throws IOException {
        super.setUp();
        saveEntitiesFromAPI(languageSchoolAPITest.getWords(), AppDatabase.getInstance(context).getWordDao());
    }

    @Test
    public void testRenderingSetupVocabularyGame() {
        onView(withId(R.id.games_recycler_view)).perform(actionOnItemAtPosition(0, click()));
        assertRenderingSetupLayout();
    }

    @Test
    public void testChooseLanguageSetupValidationVocabularyGame(){
        Language randomLanguage = getRandomLanguage(context);

        setupVocabularyGame(randomLanguage, randomLanguage);

        assertRenderingSetupLayout();
    }

    @Test
    public void testChooseLanguageSetupVocabularyGame() {
        Language baseLanguage = getRandomLanguage(context);
        Language targetLanguage = getRandomLanguage(context, Objects.requireNonNull(baseLanguage).getLanguageName());

        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        List<Word> wordsInLanguage = wordDao.getWordsByLanguage(Objects.requireNonNull(targetLanguage).getLanguageName());

        setupVocabularyGame(baseLanguage, targetLanguage);

        String answerInputHint = context.getString(R.string.instruction_vocabulary_game)+baseLanguage.getLanguageName();
        String textCheckButton = context.getString(R.string.check_button_text);

        onView(withId(R.id.word_text_view)).check(matches(isWordInLanguage(wordsInLanguage, targetLanguage)));
        onView(allOf(withId(R.id.answer_input), withHint(answerInputHint))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.check_button), withText(textCheckButton))).check(matches(isDisplayed()));
    }

    @Test
    public void testCorrectAnswerWithoutAuthenticationVocabularyGame(){
        Language baseLanguage = getRandomLanguage(context);
        Language targetLanguage = getRandomLanguage(context, Objects.requireNonNull(baseLanguage).getLanguageName());

        setupVocabularyGame(baseLanguage, targetLanguage);

        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        List<Word> words = wordDao.getAllRecords();
        List<Word> wordsInTargetLanguage = wordDao.getWordsByLanguage(Objects.requireNonNull(targetLanguage).getLanguageName());

        onView(isRoot()).perform(inputVocabularyGameAnswer(wordsInTargetLanguage, words, baseLanguage, true));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(true)));
    }

    @Test
    public void testWrongAnswerWithoutAuthenticationVocabularyGame(){
        Language baseLanguage = getRandomLanguage(context);
        Language targetLanguage = getRandomLanguage(context, Objects.requireNonNull(baseLanguage).getLanguageName());

        setupVocabularyGame(baseLanguage, targetLanguage);

        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        List<Word> words = wordDao.getAllRecords();
        List<Word> wordsInTargetLanguage = wordDao.getWordsByLanguage(Objects.requireNonNull(targetLanguage).getLanguageName());

        onView(isRoot()).perform(inputVocabularyGameAnswer(wordsInTargetLanguage, words, baseLanguage, false));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(false)));
    }

    @Test
    public void testTwoCorrectAnswersWithAuthenticationVocabularyGame() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, testUser.getUsername(), testUser.getPassword());

        Language baseLanguage = getRandomLanguage(context);
        Language targetLanguage = getRandomLanguage(context, Objects.requireNonNull(baseLanguage).getLanguageName());

        setupVocabularyGame(baseLanguage, targetLanguage);

        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        List<Word> words = wordDao.getAllRecords();
        List<Word> wordsInTargetLanguage = wordDao.getWordsByLanguage(Objects.requireNonNull(targetLanguage).getLanguageName());

        onView(isRoot()).perform(inputVocabularyGameAnswer(wordsInTargetLanguage, words, baseLanguage, true));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(true)));

        assertScoreValue(testUser, targetLanguage, "vocabulary_game", 1L);

        onView(withId(R.id.new_word_button)).perform(click());

        onView(isRoot()).perform(inputVocabularyGameAnswer(wordsInTargetLanguage, words, baseLanguage, true));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(true)));

        assertScoreValue(testUser, targetLanguage, "vocabulary_game", 2L);
    }

    @Test
    public void testOneWrongAnswerOneCorrectAnswerWithAuthenticationVocabularyGame() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, testUser.getUsername(), testUser.getPassword());

        Language baseLanguage = getRandomLanguage(context);
        Language targetLanguage = getRandomLanguage(context, Objects.requireNonNull(baseLanguage).getLanguageName());

        setupVocabularyGame(baseLanguage, targetLanguage);

        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        List<Word> words = wordDao.getAllRecords();
        List<Word> wordsInTargetLanguage = wordDao.getWordsByLanguage(Objects.requireNonNull(targetLanguage).getLanguageName());

        onView(isRoot()).perform(inputVocabularyGameAnswer(wordsInTargetLanguage, words, baseLanguage, false));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(false)));

        assertScoreValue(testUser, targetLanguage, "vocabulary_game", 0L);

        onView(withId(R.id.new_word_button)).perform(click());

        onView(isRoot()).perform(inputVocabularyGameAnswer(wordsInTargetLanguage, words, baseLanguage, true));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(true)));

        assertScoreValue(testUser, targetLanguage, "vocabulary_game", 1L);
    }

    @Test
    public void testOneCorrectAnswerOneWrongAnswerWithAuthenticationArticleGame() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, testUser.getUsername(), testUser.getPassword());

        Language baseLanguage = getRandomLanguage(context);
        Language targetLanguage = getRandomLanguage(context, Objects.requireNonNull(baseLanguage).getLanguageName());

        setupVocabularyGame(baseLanguage, targetLanguage);

        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        List<Word> words = wordDao.getAllRecords();
        List<Word> wordsInTargetLanguage = wordDao.getWordsByLanguage(Objects.requireNonNull(targetLanguage).getLanguageName());

        onView(isRoot()).perform(inputVocabularyGameAnswer(wordsInTargetLanguage, words, baseLanguage, true));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(true)));

        assertScoreValue(testUser, targetLanguage, "vocabulary_game", 1L);

        onView(withId(R.id.new_word_button)).perform(click());

        onView(isRoot()).perform(inputVocabularyGameAnswer(wordsInTargetLanguage, words, baseLanguage, false));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(false)));

        assertScoreValue(testUser, targetLanguage, "vocabulary_game", 1L);
    }

    @Test
    public void testTwoWrongAnswersWithAuthenticationArticleGame() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, testUser.getUsername(), testUser.getPassword());

        Language baseLanguage = getRandomLanguage(context);
        Language targetLanguage = getRandomLanguage(context, Objects.requireNonNull(baseLanguage).getLanguageName());

        setupVocabularyGame(baseLanguage, targetLanguage);

        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        List<Word> words = wordDao.getAllRecords();
        List<Word> wordsInTargetLanguage = wordDao.getWordsByLanguage(Objects.requireNonNull(targetLanguage).getLanguageName());

        onView(isRoot()).perform(inputVocabularyGameAnswer(wordsInTargetLanguage, words, baseLanguage, false));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(false)));

        assertScoreValue(testUser, targetLanguage, "vocabulary_game", 0L);

        onView(withId(R.id.new_word_button)).perform(click());

        onView(isRoot()).perform(inputVocabularyGameAnswer(wordsInTargetLanguage, words, baseLanguage, false));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(false)));

        assertScoreValue(testUser, targetLanguage, "vocabulary_game", 0L);
    }

    private void setupVocabularyGame(Language baseLanguage, Language targetLanguage){
        onView(withId(R.id.games_recycler_view)).perform(actionOnItemAtPosition(0, click()));
        onView(withId(R.id.base_language_spinner)).perform(click());
        onData(is(baseLanguage)).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.target_language_spinner)).perform(click());
        onData(is(targetLanguage)).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.play_button)).perform(click());
    }

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
