package com.example.pajelingo.activities.games;

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
import static com.example.pajelingo.util.Tools.saveStateAndUserCredentials;
import static com.example.pajelingo.utils.CustomMatchers.checkAnswerFeedback;
import static com.example.pajelingo.utils.CustomMatchers.isTextViewVerbAndConjugationInList;
import static com.example.pajelingo.utils.CustomViewActions.inputConjugationGameAnswer;
import static com.example.pajelingo.utils.Tools.assertScoreValue;
import static com.example.pajelingo.utils.Tools.getRandomLanguage;
import static com.example.pajelingo.utils.Tools.saveEntitiesFromAPI;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import androidx.test.core.app.ActivityScenario;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MainActivity;
import com.example.pajelingo.daos.ConjugationDao;
import com.example.pajelingo.daos.WordDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Conjugation;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Word;

import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ConjugationGameActivityTests extends GameActivityTests{
    @Override
    public void setUp() throws IOException {
        super.setUp();
        saveEntitiesFromAPI(languageSchoolAPITest.getWords(), AppDatabase.getInstance(context).getWordDao());
        saveEntitiesFromAPI(languageSchoolAPITest.getConjugations(), AppDatabase.getInstance(context).getConjugationDao());
    }

    @Test
    public void testRenderingSetupConjugationGame() {
        String labelLanguageSpinner = context.getString(R.string.choose_language_label);
        String textPlayButton = context.getString(R.string.play_button_text);

        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.games_recycler_view)).perform(actionOnItemAtPosition(2, click()));
        onView(allOf(withId(R.id.label_language_choice), withText(labelLanguageSpinner))).check(matches(isDisplayed()));
        onView(withId(R.id.language_choice_spinner)).check(matches((isDisplayed())));
        onView(allOf(withId(R.id.play_button), withText(textPlayButton))).check(matches((isDisplayed())));
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

        onView(withId(R.id.verb_and_tense_text_view)).check(matches(isTextViewVerbAndConjugationInList(verbsInLanguage, conjugations)));

        String textCheckButton = context.getString(R.string.check_button_text);
        // Checking if the pronouns are all displayed
        onView(allOf(withId(R.id.pronoun_1), withText(randomLanguage.getPersonalPronoun1()))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.pronoun_2), withText(randomLanguage.getPersonalPronoun2()))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.pronoun_3), withText(randomLanguage.getPersonalPronoun3()))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.pronoun_4), withText(randomLanguage.getPersonalPronoun4()))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.pronoun_5), withText(randomLanguage.getPersonalPronoun5()))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.pronoun_6), withText(randomLanguage.getPersonalPronoun6()))).check(matches(isDisplayed()));
        // Checking if the inputs are all displayed
        onView(withId(R.id.conjugation_1)).check(matches(isDisplayed()));
        onView(withId(R.id.conjugation_2)).check(matches(isDisplayed()));
        onView(withId(R.id.conjugation_3)).check(matches(isDisplayed()));
        onView(withId(R.id.conjugation_4)).check(matches(isDisplayed()));
        onView(withId(R.id.conjugation_5)).check(matches(isDisplayed()));
        onView(withId(R.id.conjugation_6)).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.check_button), withText(textCheckButton))).check(matches(isDisplayed()));
    }

    @Test
    public void testCorrectAnswerWithoutAuthenticationConjugationGame(){
        Language randomLanguage = getRandomLanguage(context);

        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        List<Word> verbsInLanguage = wordDao.getWordsByCategoryAndByLanguage("verbs", Objects.requireNonNull(randomLanguage).getLanguageName());

        ConjugationDao conjugationDao = AppDatabase.getInstance(context).getConjugationDao();
        List<Conjugation> conjugations = conjugationDao.getAllRecords();

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupConjugationGame(randomLanguage);

        onView(isRoot()).perform(inputConjugationGameAnswer(verbsInLanguage, conjugations, true));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(true)));
    }

    @Test
    public void testWrongAnswerWithoutAuthenticationConjugationGame(){
        Language randomLanguage = getRandomLanguage(context);

        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        List<Word> verbsInLanguage = wordDao.getWordsByCategoryAndByLanguage("verbs", Objects.requireNonNull(randomLanguage).getLanguageName());

        ConjugationDao conjugationDao = AppDatabase.getInstance(context).getConjugationDao();
        List<Conjugation> conjugations = conjugationDao.getAllRecords();

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupConjugationGame(randomLanguage);

        onView(isRoot()).perform(inputConjugationGameAnswer(verbsInLanguage, conjugations, false));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(false)));
    }

    @Test
    public void testTwoCorrectAnswersWithAuthenticationConjugationGame() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, testUser.getUsername(), testUser.getPassword());

        Language randomLanguage = getRandomLanguage(context);

        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        List<Word> verbsInLanguage = wordDao.getWordsByCategoryAndByLanguage("verbs", Objects.requireNonNull(randomLanguage).getLanguageName());

        ConjugationDao conjugationDao = AppDatabase.getInstance(context).getConjugationDao();
        List<Conjugation> conjugations = conjugationDao.getAllRecords();

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupConjugationGame(randomLanguage);

        onView(isRoot()).perform(inputConjugationGameAnswer(verbsInLanguage, conjugations, true));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(true)));

        assertScoreValue(testUser, randomLanguage, "conjugation_game", 1L);

        onView(withId(R.id.new_word_button)).perform(click());

        onView(isRoot()).perform(inputConjugationGameAnswer(verbsInLanguage, conjugations, true));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(true)));


        assertScoreValue(testUser, randomLanguage, "conjugation_game", 2L);
    }

    @Test
    public void testOneWrongAnswerOneCorrectAnswerWithAuthenticationConjugationGame() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, testUser.getUsername(), testUser.getPassword());

        Language randomLanguage = getRandomLanguage(context);

        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        List<Word> verbsInLanguage = wordDao.getWordsByCategoryAndByLanguage("verbs", Objects.requireNonNull(randomLanguage).getLanguageName());

        ConjugationDao conjugationDao = AppDatabase.getInstance(context).getConjugationDao();
        List<Conjugation> conjugations = conjugationDao.getAllRecords();

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupConjugationGame(randomLanguage);

        onView(isRoot()).perform(inputConjugationGameAnswer(verbsInLanguage, conjugations, false));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(false)));

        assertScoreValue(testUser, randomLanguage, "conjugation_game", 0L);

        onView(withId(R.id.new_word_button)).perform(click());

        onView(isRoot()).perform(inputConjugationGameAnswer(verbsInLanguage, conjugations, true));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(true)));

        assertScoreValue(testUser, randomLanguage, "conjugation_game", 1L);
    }

    @Test
    public void testOneCorrectAnswerOneWrongAnswerWithAuthenticationConjugationGame() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, testUser.getUsername(), testUser.getPassword());

        Language randomLanguage = getRandomLanguage(context);

        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        List<Word> verbsInLanguage = wordDao.getWordsByCategoryAndByLanguage("verbs", Objects.requireNonNull(randomLanguage).getLanguageName());

        ConjugationDao conjugationDao = AppDatabase.getInstance(context).getConjugationDao();
        List<Conjugation> conjugations = conjugationDao.getAllRecords();

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupConjugationGame(randomLanguage);

        onView(isRoot()).perform(inputConjugationGameAnswer(verbsInLanguage, conjugations, true));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(true)));

        assertScoreValue(testUser, randomLanguage, "conjugation_game", 1L);

        onView(withId(R.id.new_word_button)).perform(click());

        onView(isRoot()).perform(inputConjugationGameAnswer(verbsInLanguage, conjugations, false));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(false)));

        assertScoreValue(testUser, randomLanguage, "conjugation_game", 1L);
    }

    @Test
    public void testTwoWrongAnswersWithAuthenticationConjugationGame() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, testUser.getUsername(), testUser.getPassword());

        Language randomLanguage = getRandomLanguage(context);

        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        List<Word> verbsInLanguage = wordDao.getWordsByCategoryAndByLanguage("verbs", Objects.requireNonNull(randomLanguage).getLanguageName());

        ConjugationDao conjugationDao = AppDatabase.getInstance(context).getConjugationDao();
        List<Conjugation> conjugations = conjugationDao.getAllRecords();

        activityScenario = ActivityScenario.launch(MainActivity.class);

        setupConjugationGame(randomLanguage);

        onView(isRoot()).perform(inputConjugationGameAnswer(verbsInLanguage, conjugations, false));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(false)));

        assertScoreValue(testUser, randomLanguage, "conjugation_game", 0L);

        onView(withId(R.id.new_word_button)).perform(click());

        onView(isRoot()).perform(inputConjugationGameAnswer(verbsInLanguage, conjugations, false));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(false)));

        assertScoreValue(testUser, randomLanguage, "conjugation_game", 0L);
    }

    private void setupConjugationGame(Language language){
        onView(withId(R.id.games_recycler_view)).perform(actionOnItemAtPosition(2, click()));
        onView(withId(R.id.language_choice_spinner)).perform(click());
        onData(is(language)).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.play_button)).perform(click());
    }

}
