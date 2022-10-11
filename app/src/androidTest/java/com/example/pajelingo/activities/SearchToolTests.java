package com.example.pajelingo.activities;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.CustomMatchers.isMeaningAtPosition;
import static com.example.pajelingo.utils.CustomMatchers.isScoreAtPosition;
import static com.example.pajelingo.utils.CustomMatchers.searchResultsMatchPattern;
import static com.example.pajelingo.utils.Tools.getRandomString;

import com.example.pajelingo.R;
import com.example.pajelingo.daos.ArticleDao;
import com.example.pajelingo.daos.MeaningDao;
import com.example.pajelingo.daos.WordDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Article;
import com.example.pajelingo.models.Meaning;
import com.example.pajelingo.models.Word;
import com.example.pajelingo.retrofit.LanguageSchoolAPI;
import com.example.pajelingo.retrofit.LanguageSchoolAPIHelper;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Response;

public class SearchToolTests extends UITests{
    private final LanguageSchoolAPI languageSchoolAPI = LanguageSchoolAPIHelper.getApiObject();

    @Before
    public void setUp() throws IOException {
        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        ArticleDao articleDao = AppDatabase.getInstance(context).getArticleDao();
        MeaningDao meaningDao = AppDatabase.getInstance(context).getMeaningDao();

        Response<List<Word>> responseWords = languageSchoolAPI.getWords().execute();
        List<Word> words = responseWords.body();
        wordDao.save(words);

        Response<List<Article>> responseArticles = languageSchoolAPI.getArticles().execute();
        List<Article> articles = responseArticles.body();
        articleDao.save(articles);

        Response<List<Meaning>> responseMeanings = languageSchoolAPI.getMeanings().execute();
        List<Meaning> meanings = responseMeanings.body();
        meaningDao.save(meanings);
    }

    @Test
    public void testRenderingSearchActivity(){
        onView(withId(R.id.search_button)).perform(click());
        onView(withId(R.id.search_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.search_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testRenderingSearchResults(){
        String searchPattern = getRandomString(1);
        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        List<Word> words = wordDao.searchWords("%"+searchPattern+"%");

        onView(withId(R.id.search_button)).perform(click());
        onView(withId(R.id.search_edit_text)).perform(typeText(searchPattern), closeSoftKeyboard());
        onView(withId(R.id.search_button)).perform(click());
        onView(withId(R.id.search_recycler_view)).check(matches(isDisplayed()));
        onView(withId(R.id.search_recycler_view)).check(matches(searchResultsMatchPattern(searchPattern)));

        for (int i = 0;i < words.size();i++){
            Word word = words.get(i);
            onView(withId(R.id.search_recycler_view))
                    .perform(scrollToPosition(i))
                    .check(matches(isScoreAtPosition(word, i)));
        }
    }

    @Test
    public void testRenderingMeaningOfWord(){
        WordDao wordDao = AppDatabase.getInstance(context).getWordDao();
        ArticleDao articleDao = AppDatabase.getInstance(context).getArticleDao();
        MeaningDao meaningDao = AppDatabase.getInstance(context).getMeaningDao();

        String searchPattern = getRandomString(1);
        List<Word> words = new ArrayList<>();

        while (words.size() == 0){
            words = wordDao.searchWords("%"+searchPattern+"%");
        }

        int randomPosition = new Random().nextInt(words.size());
        Word randomWord = words.get(randomPosition);
        Article article = articleDao.getRecordById(randomWord.getIdArticle());
        List<Meaning> meanings = meaningDao.getMeaningsOfWord(randomWord.getId());

        onView(withId(R.id.search_button)).perform(click());
        onView(withId(R.id.search_edit_text)).perform(typeText(searchPattern), closeSoftKeyboard());
        onView(withId(R.id.search_button)).perform(click());

        onView(withId(R.id.search_recycler_view)).perform(actionOnItemAtPosition(randomPosition, click()));
        onView(withId(R.id.meanings_recycler_view)).check(matches(isDisplayed()));
        onView(withText(article.getArticleName()+" "+randomWord.getWordName())).check(matches(isDisplayed()));

        for (int i = 0;i < meanings.size();i++){
            Meaning meaning = meanings.get(i);
            onView(withId(R.id.meanings_recycler_view))
                    .perform(scrollToPosition(i))
                    .check(matches(isMeaningAtPosition(meaning, i)));
        }
    }
}
