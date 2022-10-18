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
import static com.example.pajelingo.utils.CustomMatchers.isTextViewWordInList;
import static com.example.pajelingo.utils.CustomViewActions.inputArticleGameAnswer;
import static com.example.pajelingo.utils.Tools.assertScoreValue;
import static com.example.pajelingo.utils.Tools.getRandomLanguage;
import static com.example.pajelingo.utils.Tools.saveEntitiesFromAPI;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import com.example.pajelingo.R;
import com.example.pajelingo.daos.ArticleDao;
import com.example.pajelingo.daos.WordDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Article;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Word;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class ArticleGameActivityTests extends GameActivityTests {
    @Override
    public void setUp() throws IOException {
        super.setUp();
        saveEntitiesFromAPI(languageSchoolAPITest.getArticles(), AppDatabase.getInstance(context).getArticleDao());
        saveEntitiesFromAPI(languageSchoolAPITest.getWords(), AppDatabase.getInstance(context).getWordDao());
    }

    @Test
    public void testRenderingSetupArticleGame() {
        String labelLanguageSpinner = context.getString(R.string.choose_language_label);
        String textPlayButton = context.getString(R.string.play_button_text);

        onView(withId(R.id.games_recycler_view)).perform(actionOnItemAtPosition(1, click()));
        onView(allOf(withId(R.id.label_language_choice), withText(labelLanguageSpinner))).check(matches(isDisplayed()));
        onView(withId(R.id.language_choice_spinner)).check(matches((isDisplayed())));
        onView(allOf(withId(R.id.play_button), withText(textPlayButton))).check(matches((isDisplayed())));
    }

    @Test
    public void testChooseLanguageSetupArticleGame(){
        Language randomLanguage = getRandomLanguage(context, "English");

        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        List<Word> wordsInLanguage = wordDao.getNounsByLanguage(randomLanguage.getLanguageName());

        setupArticleGame(randomLanguage);

        onView(withId(R.id.word_text_view)).check(matches(isTextViewWordInList(wordsInLanguage)));

        String answerInputHint = context.getString(R.string.article_game_hint_input_text);
        String textCheckButton = context.getString(R.string.check_button_text);
        onView(allOf(withId(R.id.answer_input), withHint(answerInputHint))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.check_button), withText(textCheckButton))).check(matches(isDisplayed()));
    }

    @Test
    public void testCorrectAnswerWithoutAuthenticationArticleGame(){
        Language randomLanguage = getRandomLanguage(context, "English");

        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        List<Word> wordsInLanguage = wordDao.getNounsByLanguage(randomLanguage.getLanguageName());

        ArticleDao articleDao = AppDatabase.getInstance(context).getArticleDao();
        List<Article> articles = articleDao.getAllRecords();

        setupArticleGame(randomLanguage);

        onView(isRoot()).perform(inputArticleGameAnswer(wordsInLanguage, articles, true));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(true)));
    }

    @Test
    public void testWrongAnswerWithoutAuthenticationArticleGame(){
        Language randomLanguage = getRandomLanguage(context, "English");

        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        List<Word> wordsInLanguage = wordDao.getNounsByLanguage(randomLanguage.getLanguageName());

        ArticleDao articleDao = AppDatabase.getInstance(context).getArticleDao();
        List<Article> articles = articleDao.getAllRecords();

        setupArticleGame(randomLanguage);

        onView(isRoot()).perform(inputArticleGameAnswer(wordsInLanguage, articles, false));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(false)));
    }

    @Test
    public void testTwoCorrectAnswersWithAuthenticationArticleGame() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, testUser.getUsername(), testUser.getPassword());

        Language randomLanguage = getRandomLanguage(context, "English");

        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        List<Word> wordsInLanguage = wordDao.getNounsByLanguage(randomLanguage.getLanguageName());

        ArticleDao articleDao = AppDatabase.getInstance(context).getArticleDao();
        List<Article> articles = articleDao.getAllRecords();

        setupArticleGame(randomLanguage);

        onView(isRoot()).perform(inputArticleGameAnswer(wordsInLanguage, articles, true));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(true)));

        assertScoreValue(testUser, randomLanguage, "article_game", 1L);

        onView(withId(R.id.new_word_button)).perform(click());

        onView(isRoot()).perform(inputArticleGameAnswer(wordsInLanguage, articles, true));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(true)));

        assertScoreValue(testUser, randomLanguage, "article_game", 2L);
    }

    @Test
    public void testOneWrongAnswerOneCorrectAnswerWithAuthenticationArticleGame() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, testUser.getUsername(), testUser.getPassword());

        Language randomLanguage = getRandomLanguage(context, "English");

        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        List<Word> wordsInLanguage = wordDao.getNounsByLanguage(randomLanguage.getLanguageName());

        ArticleDao articleDao = AppDatabase.getInstance(context).getArticleDao();
        List<Article> articles = articleDao.getAllRecords();

        setupArticleGame(randomLanguage);

        onView(isRoot()).perform(inputArticleGameAnswer(wordsInLanguage, articles, false));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(false)));

        assertScoreValue(testUser, randomLanguage, "article_game", 0L);

        onView(withId(R.id.new_word_button)).perform(click());

        onView(isRoot()).perform(inputArticleGameAnswer(wordsInLanguage, articles, true));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(true)));

        assertScoreValue(testUser, randomLanguage, "article_game", 1L);
    }

    @Test
    public void testOneCorrectAnswerOneWrongAnswerWithAuthenticationArticleGame() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, testUser.getUsername(), testUser.getPassword());

        Language randomLanguage = getRandomLanguage(context, "English");

        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        List<Word> wordsInLanguage = wordDao.getNounsByLanguage(randomLanguage.getLanguageName());

        ArticleDao articleDao = AppDatabase.getInstance(context).getArticleDao();
        List<Article> articles = articleDao.getAllRecords();

        setupArticleGame(randomLanguage);

        onView(isRoot()).perform(inputArticleGameAnswer(wordsInLanguage, articles, true));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(true)));

        assertScoreValue(testUser, randomLanguage, "article_game", 1L);

        onView(withId(R.id.new_word_button)).perform(click());

        onView(isRoot()).perform(inputArticleGameAnswer(wordsInLanguage, articles, false));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(false)));

        assertScoreValue(testUser, randomLanguage, "article_game", 1L);
    }

    @Test
    public void testTwoWrongAnswersWithAuthenticationArticleGame() throws IOException, InterruptedException {
        saveStateAndUserCredentials(context, testUser.getUsername(), testUser.getPassword());

        Language randomLanguage = getRandomLanguage(context, "English");

        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        List<Word> wordsInLanguage = wordDao.getNounsByLanguage(randomLanguage.getLanguageName());

        ArticleDao articleDao = AppDatabase.getInstance(context).getArticleDao();
        List<Article> articles = articleDao.getAllRecords();

        setupArticleGame(randomLanguage);

        onView(isRoot()).perform(inputArticleGameAnswer(wordsInLanguage, articles, false));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(false)));

        assertScoreValue(testUser, randomLanguage, "article_game", 0L);

        onView(withId(R.id.new_word_button)).perform(click());

        onView(isRoot()).perform(inputArticleGameAnswer(wordsInLanguage, articles, false));
        onView(withId(R.id.check_button)).perform(click());
        onView(withId(R.id.feedback_text_view)).check(matches(checkAnswerFeedback(false)));

        assertScoreValue(testUser, randomLanguage, "article_game", 0L);
    }

    private void setupArticleGame(Language language){
        onView(withId(R.id.games_recycler_view)).perform(actionOnItemAtPosition(1, click()));
        onView(withId(R.id.language_choice_spinner)).perform(click());
        onData(is(language)).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.play_button)).perform(click());
    }

}
