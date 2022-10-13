package com.example.pajelingo.activities.games;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.util.Tools.getAuthToken;
import static com.example.pajelingo.util.Tools.saveStateAndUserCredentials;
import static com.example.pajelingo.utils.CustomMatchers.checkAnswerFeedback;
import static com.example.pajelingo.utils.CustomMatchers.isWordInLanguage;
import static com.example.pajelingo.utils.CustomViewActions.inputArticleGameAnswer;
import static com.example.pajelingo.utils.Tools.assertScoreValue;
import static com.example.pajelingo.utils.Tools.getRandomInteger;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.UITests;
import com.example.pajelingo.daos.ArticleDao;
import com.example.pajelingo.daos.LanguageDao;
import com.example.pajelingo.daos.WordDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Article;
import com.example.pajelingo.models.Game;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.User;
import com.example.pajelingo.models.Word;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelperTest;
import com.example.pajelingo.retrofit.LanguageSchoolAPITest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

public class ArticleGameActivityTests extends UITests {
    private final LanguageSchoolAPITest languageSchoolAPITest = (LanguageSchoolAPITest) LanguageSchoolAPIHelperTest.getApiObject();
    private final User testUser = new User("test.android@test.com", "TestAndroid", "test-android");

    @Before
    public void setUp() throws IOException {
        context.deleteSharedPreferences(context.getString(R.string.sp_file_name));
        languageSchoolAPITest.deleteAccount(getAuthToken(testUser.getUsername(), testUser.getPassword())).execute();
        languageSchoolAPITest.signup(testUser).execute();

        Response<List<Game>> responseGame = languageSchoolAPITest.getGames().execute();
        List<Game> games = responseGame.body();
        AppDatabase.getInstance(context).getGameDao().save(games);

        Response<List<Language>> responseLanguage = languageSchoolAPITest.getLanguages().execute();
        List<Language> languages = responseLanguage.body();
        AppDatabase.getInstance(context).getLanguageDao().save(languages);

        Response<List<Article>> responseArticle = languageSchoolAPITest.getArticles().execute();
        List<Article> articles = responseArticle.body();
        AppDatabase.getInstance(context).getArticleDao().save(articles);

        Response<List<Word>> responseWord = languageSchoolAPITest.getWords().execute();
        List<Word> words = responseWord.body();
        AppDatabase.getInstance(context).getWordDao().save(words);
    }

    @Test
    public void testRenderingSetupArticleGame() {
        String labelLanguageSpinner = context.getString(R.string.choose_language_label);
        String textPlayButton = context.getString(R.string.play_button_text);

        onView(withId(R.id.games_recycler_view)).perform(scrollToPosition(1), click());
        onView(allOf(withId(R.id.label_language_choice), withText(labelLanguageSpinner))).check(matches(isDisplayed()));
        onView(withId(R.id.language_choice_spinner)).check(matches((isDisplayed())));
        onView(allOf(withId(R.id.play_button), withText(textPlayButton))).check(matches((isDisplayed())));
    }

    @Test
    public void testChooseLanguageSetupArticleGame(){
        Language randomLanguage = getRandomLanguageForArticleGame();

        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        List<Word> wordsInLanguage = wordDao.getNounsByLanguage(randomLanguage.getLanguageName());

        setupArticleGame(randomLanguage);
        onView(withId(R.id.word_text_view)).check(matches(isWordInLanguage(wordsInLanguage, randomLanguage)));

        String answerInputHint = context.getString(R.string.article_game_hint_input_text);
        String textCheckButton = context.getString(R.string.check_button_text);
        onView(allOf(withId(R.id.answer_input), withHint(answerInputHint))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.check_button), withText(textCheckButton))).check(matches(isDisplayed()));
    }

    @Test
    public void testCorrectAnswerWithoutAuthenticationArticleGame(){
        Language randomLanguage = getRandomLanguageForArticleGame();

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
        Language randomLanguage = getRandomLanguageForArticleGame();

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

        Language randomLanguage = getRandomLanguageForArticleGame();

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

        Language randomLanguage = getRandomLanguageForArticleGame();

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

        Language randomLanguage = getRandomLanguageForArticleGame();

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

        Language randomLanguage = getRandomLanguageForArticleGame();

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

    private Language getRandomLanguageForArticleGame(){
        LanguageDao languageDao = AppDatabase.getInstance(context).getLanguageDao();
        List<Language> languages = languageDao.getAllRecords();
        Language randomLanguage = languages.get(getRandomInteger(0, languages.size() - 1));

        while (randomLanguage.getLanguageName().equals("English")){
            randomLanguage = languages.get(getRandomInteger(0, languages.size() - 1));
        }

        return randomLanguage;
    }

    private void setupArticleGame(Language language){
        onView(withId(R.id.games_recycler_view)).perform(scrollToPosition(1), click());
        onView(withId(R.id.language_choice_spinner)).perform(click());
        onData(is(language)).inRoot(isPlatformPopup()).perform(click());
        onView(withId(R.id.play_button)).perform(click());
    }

    @After
    public void tearDown() throws IOException {
        languageSchoolAPITest.deleteAccount(getAuthToken(testUser.getUsername(), testUser.getPassword())).execute();
        context.deleteSharedPreferences(context.getString(R.string.sp_file_name));
    }

}
