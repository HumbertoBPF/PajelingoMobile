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
import static com.example.pajelingo.utils.CustomMatchers.checkAnswerFeedback;
import static com.example.pajelingo.utils.CustomMatchers.isTextViewWordInList;
import static com.example.pajelingo.utils.CustomViewActions.inputArticleGameAnswer;
import static com.example.pajelingo.utils.TestTools.assertScoreValue;
import static com.example.pajelingo.utils.TestTools.getRandomLanguage;
import static com.example.pajelingo.utils.TestTools.saveEntitiesFromAPI;
import static com.example.pajelingo.utils.Tools.saveStateAndUserCredentials;
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
import com.example.pajelingo.tests.abstract_tests.GameActivityTests;

import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ArticleGameActivityTests extends GameActivityTests {
    @Override
    public void setUp() throws IOException {
        super.setUp();
        saveEntitiesFromAPI(languageSchoolAPI.getArticles(), AppDatabase.getInstance(context).getArticleDao());
        saveEntitiesFromAPI(languageSchoolAPI.getWords(), AppDatabase.getInstance(context).getWordDao());
        game = AppDatabase.getInstance(context).getGameDao().getGameByName("Guess the Article");
    }

    @Test
    public void testRenderingSetupArticleGame() {
        String labelLanguageSpinner = context.getString(R.string.choose_language_label);
        String textPlayButton = context.getString(R.string.play_button_text);

        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.games_recycler_view)).perform(actionOnItemAtPosition(1, click()));
        onView(allOf(withId(R.id.label_language_choice), withText(labelLanguageSpinner))).check(matches(isDisplayed()));
        onView(withId(R.id.language_choice_spinner)).check(matches((isDisplayed())));
        onView(allOf(withId(R.id.play_button), withText(textPlayButton))).check(matches((isDisplayed())));
    }

    @Test
    public void testChooseLanguageSetupArticleGame(){
        Language randomLanguage = getRandomLanguage(context, "English");

        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        List<Word> wordsInLanguage = wordDao.getNounsByLanguage(Objects.requireNonNull(randomLanguage).getLanguageName());

        String answerInputHint = context.getString(R.string.article_game_hint_input_text);
        String textCheckButton = context.getString(R.string.check_button_text);

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupArticleGame(randomLanguage);

        onView(withId(R.id.word_text_view)).check(matches(isTextViewWordInList(wordsInLanguage)));
        onView(allOf(withId(R.id.answer_input), withHint(answerInputHint))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.check_button), withText(textCheckButton))).check(matches(isDisplayed()));
    }

    @Test
    public void testCorrectAnswerWithoutAuthenticationArticleGame(){
        Language randomLanguage = getRandomLanguage(context, "English");

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupArticleGame(randomLanguage);

        inputAnswerAndCheckFeedback(randomLanguage, true);
    }

    @Test
    public void testWrongAnswerWithoutAuthenticationArticleGame(){
        Language randomLanguage = getRandomLanguage(context, "English");

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupArticleGame(randomLanguage);

        inputAnswerAndCheckFeedback(randomLanguage, false);
    }

    @Test
    public void testTwoCorrectAnswersWithAuthenticationArticleGame() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, testUser.getUsername(), testUser.getPassword());

        Language randomLanguage = getRandomLanguage(context, "English");

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupArticleGame(randomLanguage);

        inputAnswerAndCheckFeedback(randomLanguage, true);
        assertScoreValue(testUser, Objects.requireNonNull(randomLanguage), game.getId(), 1L);

        onView(withId(R.id.new_word_button)).perform(click());

        inputAnswerAndCheckFeedback(randomLanguage, true);
        assertScoreValue(testUser, randomLanguage, game.getId(), 2L);
    }

    @Test
    public void testOneWrongAnswerOneCorrectAnswerWithAuthenticationArticleGame() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, testUser.getUsername(), testUser.getPassword());

        Language randomLanguage = getRandomLanguage(context, "English");

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupArticleGame(randomLanguage);

        inputAnswerAndCheckFeedback(randomLanguage, false);
        assertScoreValue(testUser, Objects.requireNonNull(randomLanguage), game.getId(), 0L);

        onView(withId(R.id.new_word_button)).perform(click());

        inputAnswerAndCheckFeedback(randomLanguage, true);
        assertScoreValue(testUser, randomLanguage, game.getId(), 1L);
    }

    @Test
    public void testOneCorrectAnswerOneWrongAnswerWithAuthenticationArticleGame() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, testUser.getUsername(), testUser.getPassword());

        Language randomLanguage = getRandomLanguage(context, "English");

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupArticleGame(randomLanguage);

        inputAnswerAndCheckFeedback(randomLanguage, true);
        assertScoreValue(testUser, Objects.requireNonNull(randomLanguage), game.getId(), 1L);

        onView(withId(R.id.new_word_button)).perform(click());

        inputAnswerAndCheckFeedback(randomLanguage, false);
        assertScoreValue(testUser, randomLanguage, game.getId(), 1L);
    }

    @Test
    public void testTwoWrongAnswersWithAuthenticationArticleGame() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, testUser.getUsername(), testUser.getPassword());

        Language randomLanguage = getRandomLanguage(context, "English");

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupArticleGame(randomLanguage);

        inputAnswerAndCheckFeedback(randomLanguage, false);
        assertScoreValue(testUser, Objects.requireNonNull(randomLanguage), game.getId(), 0L);

        onView(withId(R.id.new_word_button)).perform(click());

        inputAnswerAndCheckFeedback(randomLanguage, false);
        assertScoreValue(testUser, randomLanguage, game.getId(), 0L);
    }

    @Test
    public void testArticleGameWithoutLanguageData() throws IOException {
        AppDatabase.getInstance(context).clearAllTables();
        saveEntitiesFromAPI(languageSchoolAPI.getGames(), AppDatabase.getInstance(context).getGameDao());

        activityScenario = ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.games_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        assertIsMenuActivity(true);
    }

    @Test
    public void testArticleGameWithoutWordData() throws IOException {
        AppDatabase.getInstance(context).clearAllTables();
        saveEntitiesFromAPI(languageSchoolAPI.getGames(), AppDatabase.getInstance(context).getGameDao());
        saveEntitiesFromAPI(languageSchoolAPI.getLanguages(), AppDatabase.getInstance(context).getLanguageDao());

        Language randomLanguage = getRandomLanguage(context, "English");

        activityScenario = ActivityScenario.launch(MainActivity.class);
        setupArticleGame(randomLanguage);
        assertIsMenuActivity(true);
    }

    @Test
    public void testArticleGameWithoutArticleData() throws IOException {
        AppDatabase.getInstance(context).clearAllTables();
        saveEntitiesFromAPI(languageSchoolAPI.getGames(), AppDatabase.getInstance(context).getGameDao());
        saveEntitiesFromAPI(languageSchoolAPI.getLanguages(), AppDatabase.getInstance(context).getLanguageDao());
        saveEntitiesFromAPI(languageSchoolAPI.getWords(), AppDatabase.getInstance(context).getWordDao());

        Language randomLanguage = getRandomLanguage(context, "English");

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupArticleGame(randomLanguage);

        onView(isRoot()).perform(inputArticleGameAnswer(context, randomLanguage, false));
        onView(withId(R.id.check_button)).perform(click());
        assertIsMenuActivity(true);
    }

    /**
     * Inputs an answer and checks if it is correct or wrong.
     * @param randomLanguage selected language
     * @param isCorrect boolean indicating if the answer is correct or wrong
     */
    private void inputAnswerAndCheckFeedback(Language randomLanguage, boolean isCorrect) {
        onView(isRoot()).perform(inputArticleGameAnswer(context, randomLanguage, isCorrect));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(isCorrect)));
    }

    /**
     * Sets the language and launches the game.
     * @param language language that must be set.
     */
    private void setupArticleGame(Language language){
        onView(withId(R.id.games_recycler_view)).perform(actionOnItemAtPosition(1, click()));
        onView(withId(R.id.language_choice_spinner)).perform(click());
        onData(is(language)).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.play_button)).perform(click());
    }

}
