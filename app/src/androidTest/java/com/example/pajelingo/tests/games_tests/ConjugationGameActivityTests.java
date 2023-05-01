package com.example.pajelingo.tests.games_tests;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.CustomMatchers.getLabeledEditTextMatcher;
import static com.example.pajelingo.utils.CustomMatchers.getLabeledSpinnerMatcher;
import static com.example.pajelingo.utils.CustomMatchers.isTextViewVerbAndConjugationInList;
import static com.example.pajelingo.utils.CustomViewActions.expandSpinner;
import static com.example.pajelingo.utils.CustomViewActions.inputConjugationGameAnswer;
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
import com.example.pajelingo.daos.ConjugationDao;
import com.example.pajelingo.daos.WordDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Conjugation;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Word;
import com.example.pajelingo.tests.abstract_tests.GameActivityTests;

import org.hamcrest.Matcher;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ConjugationGameActivityTests extends GameActivityTests {
    @Override
    public void setUp() throws IOException {
        super.setUp();
        game = AppDatabase.getInstance(context).getGameDao().getGameByName("Conjugation Game");
    }

    @Test
    public void testRenderingSetupConjugationGame() {
        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.games_recycler_view))
                .perform(actionOnItemAtPosition(2, click()));

        Matcher<View> instructionsTextViewMatcher = allOf(withText(game.getInstructions()), isDisplayed());
        onView(withId(R.id.instructions_text_view))
                .check(matches(instructionsTextViewMatcher));

        Matcher<View> languageInputMatcher = getLabeledSpinnerMatcher(R.string.choose_language_label);
        onView(withId(R.id.language_input))
                .check(matches(languageInputMatcher));

        Matcher<View> playButtonMatcher = allOf(withText(R.string.play_button_text), isDisplayed());
        onView(withId(R.id.play_button))
                .check(matches(playButtonMatcher));
    }

    @Test
    public void testChooseLanguageSetupConjugationGame(){
        Language randomLanguage = getRandomLanguage(context);

        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        List<Word> verbsInLanguage = wordDao.getWordsByCategoryAndByLanguage("verbs", Objects.requireNonNull(randomLanguage).getLanguageName());

        ConjugationDao conjugationDao = AppDatabase.getInstance(context).getConjugationDao();
        List<Conjugation> conjugations = conjugationDao.getAllRecords();

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupConjugationGame(randomLanguage);

        onView(withId(R.id.verb_and_tense_text_view))
                .check(matches(isTextViewVerbAndConjugationInList(verbsInLanguage, conjugations)));
        // Checking if the pronouns are all displayed
        Matcher<View> conjugation1Matcher = getLabeledEditTextMatcher(randomLanguage.getPersonalPronoun1(), "");
        onView(withId(R.id.conjugation_1))
                .check(matches(conjugation1Matcher));

        Matcher<View> conjugation2Matcher = getLabeledEditTextMatcher(randomLanguage.getPersonalPronoun2(), "");
        onView(withId(R.id.conjugation_2))
                .check(matches(conjugation2Matcher));

        Matcher<View> conjugation3Matcher = getLabeledEditTextMatcher(randomLanguage.getPersonalPronoun3(), "");
        onView(withId(R.id.conjugation_3))
                .check(matches(conjugation3Matcher));

        Matcher<View> conjugation4Matcher = getLabeledEditTextMatcher(randomLanguage.getPersonalPronoun4(), "");
        onView(withId(R.id.conjugation_4))
                .check(matches(conjugation4Matcher));

        Matcher<View> conjugation5Matcher = getLabeledEditTextMatcher(randomLanguage.getPersonalPronoun5(), "");
        onView(withId(R.id.conjugation_5))
                .check(matches(conjugation5Matcher));

        Matcher<View> conjugation6Matcher = getLabeledEditTextMatcher(randomLanguage.getPersonalPronoun6(), "");
        onView(withId(R.id.conjugation_6))
                .check(matches(conjugation6Matcher));

        Matcher<View> checkButtonMatcher = allOf(withText(R.string.check_button_text), isDisplayed());
        onView(withId(R.id.check_button))
                .check(matches(checkButtonMatcher));
    }

    @Test
    public void testCorrectAnswerWithoutAuthenticationConjugationGame() throws InterruptedException {
        Language randomLanguage = getRandomLanguage(context);

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupConjugationGame(randomLanguage);

        inputAnswerAndCheckFeedback(randomLanguage, true);
    }

    @Test
    public void testWrongAnswerWithoutAuthenticationConjugationGame() throws InterruptedException {
        Language randomLanguage = getRandomLanguage(context);

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupConjugationGame(randomLanguage);

        inputAnswerAndCheckFeedback(randomLanguage, false);
    }

    @Test
    public void testTwoCorrectAnswersWithAuthenticationConjugationGame() throws IOException, InterruptedException {
        authenticateUser(context, testUser);

        Language randomLanguage = getRandomLanguage(context);

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupConjugationGame(randomLanguage);

        Long score1 = getScore(testUser, Objects.requireNonNull(randomLanguage), game.getId());

        inputAnswerAndCheckFeedback(randomLanguage, true);

        Long score2 = getScore(testUser, Objects.requireNonNull(randomLanguage), game.getId());
        assertEquals(score1 + 1L, score2.longValue());

        onView(withId(R.id.new_word_button)).perform(click());
        inputAnswerAndCheckFeedback(randomLanguage, true);

        Long score3 = getScore(testUser, Objects.requireNonNull(randomLanguage), game.getId());
        assertEquals(score2 + 1L, score3.longValue());
    }

    @Test
    public void testOneWrongAnswerOneCorrectAnswerWithAuthenticationConjugationGame() throws IOException, InterruptedException {
        authenticateUser(context, testUser);

        Language randomLanguage = getRandomLanguage(context);

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupConjugationGame(randomLanguage);

        Long score1 = getScore(testUser, Objects.requireNonNull(randomLanguage), game.getId());

        inputAnswerAndCheckFeedback(randomLanguage, false);

        Long score2 = getScore(testUser, Objects.requireNonNull(randomLanguage), game.getId());
        assertEquals(score1, score2);

        onView(withId(R.id.new_word_button)).perform(click());
        inputAnswerAndCheckFeedback(randomLanguage, true);

        Long score3 = getScore(testUser, Objects.requireNonNull(randomLanguage), game.getId());
        assertEquals(score2 + 1L, score3.longValue());
    }

    @Test
    public void testOneCorrectAnswerOneWrongAnswerWithAuthenticationConjugationGame() throws IOException, InterruptedException {
        authenticateUser(context, testUser);

        Language randomLanguage = getRandomLanguage(context);

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupConjugationGame(randomLanguage);

        Long score1 = getScore(testUser, Objects.requireNonNull(randomLanguage), game.getId());

        inputAnswerAndCheckFeedback(randomLanguage, true);

        Long score2 = getScore(testUser, Objects.requireNonNull(randomLanguage), game.getId());
        assertEquals(score1 + 1L, score2.longValue());

        onView(withId(R.id.new_word_button)).perform(click());
        inputAnswerAndCheckFeedback(randomLanguage, false);

        Long score3 = getScore(testUser, Objects.requireNonNull(randomLanguage), game.getId());
        assertEquals(score2, score3);
    }

    @Test
    public void testTwoWrongAnswersWithAuthenticationConjugationGame() throws IOException, InterruptedException {
        saveUserData(context, testUser);

        Language randomLanguage = getRandomLanguage(context);

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupConjugationGame(randomLanguage);

        Long score1 = getScore(testUser, Objects.requireNonNull(randomLanguage), game.getId());

        inputAnswerAndCheckFeedback(randomLanguage, false);

        Long score2 = getScore(testUser, Objects.requireNonNull(randomLanguage), game.getId());
        assertEquals(score1, score2);

        onView(withId(R.id.new_word_button)).perform(click());
        inputAnswerAndCheckFeedback(randomLanguage, false);

        Long score3 = getScore(testUser, Objects.requireNonNull(randomLanguage), game.getId());
        assertEquals(score2, score3);
    }

    @Test
    public void testConjugationGameWithoutLanguageData() throws IOException {
        AppDatabase.getInstance(context).clearAllTables();
        saveEntitiesFromAPI(languageSchoolAPI.getGames(), AppDatabase.getInstance(context).getGameDao());

        activityScenario = ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.games_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
        assertIsMainActivity(true);
    }

    @Test
    public void testConjugationGameWithoutWordData() throws IOException {
        AppDatabase.getInstance(context).clearAllTables();
        saveEntitiesFromAPI(languageSchoolAPI.getGames(), AppDatabase.getInstance(context).getGameDao());
        saveEntitiesFromAPI(languageSchoolAPI.getLanguages(), AppDatabase.getInstance(context).getLanguageDao());

        Language randomLanguage = getRandomLanguage(context);

        activityScenario = ActivityScenario.launch(MainActivity.class);
        setupConjugationGame(randomLanguage);
        assertIsMainActivity(true);
    }

    @Test
    public void testConjugationGameWithoutConjugationData() throws IOException {
        AppDatabase.getInstance(context).clearAllTables();
        saveEntitiesFromAPI(languageSchoolAPI.getGames(), AppDatabase.getInstance(context).getGameDao());
        saveEntitiesFromAPI(languageSchoolAPI.getLanguages(), AppDatabase.getInstance(context).getLanguageDao());
        saveEntitiesFromAPI(languageSchoolAPI.getWords(), AppDatabase.getInstance(context).getWordDao());

        Language randomLanguage = getRandomLanguage(context);

        activityScenario = ActivityScenario.launch(MainActivity.class);
        setupConjugationGame(randomLanguage);
        assertIsMainActivity(true);
    }

    /**
     * Inputs an answer and checks if it is correct or wrong.
     * @param randomLanguage selected language
     * @param isCorrect boolean indicating if the answer is correct or wrong
     */
    private void inputAnswerAndCheckFeedback(Language randomLanguage, boolean isCorrect) throws InterruptedException {
        String expectedFeedback =  isCorrect?"Correct :)":"Wrong answer";
        Thread.sleep(3000);
        onView(isRoot())
                .perform(inputConjugationGameAnswer(context, randomLanguage, isCorrect));
        onView(withId(R.id.check_button))
                .perform(click());
        onView(withId(R.id.feedback_text_view))
                .check(matches(withText(startsWith(expectedFeedback))));
    }

    /**
     * Sets the language and launches the game.
     * @param language language that must be set.
     */
    private void setupConjugationGame(Language language) {
        onView(withId(R.id.games_recycler_view))
                .perform(actionOnItemAtPosition(2, click()));
        onView(withId(R.id.language_input))
                .perform(expandSpinner());
        onData(is(language))
                .inRoot(isPlatformPopup())
                .perform(click());
        onView(withId(R.id.play_button))
                .perform(click());
    }

}
