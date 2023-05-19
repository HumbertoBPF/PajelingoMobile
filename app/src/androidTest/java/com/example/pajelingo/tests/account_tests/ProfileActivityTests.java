package com.example.pajelingo.tests.account_tests;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.RandomTools.getRandomInteger;
import static com.example.pajelingo.utils.RandomTools.getRandomLanguage;
import static com.example.pajelingo.utils.RetrofitTools.getAccountsPage;

import static org.hamcrest.Matchers.is;

import androidx.test.core.app.ActivityScenario;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MainActivity;
import com.example.pajelingo.daos.LanguageDao;
import com.example.pajelingo.daos.ScoreDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Page;
import com.example.pajelingo.models.Score;
import com.example.pajelingo.models.User;

import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ProfileActivityTests extends AccountActivityTests {
    @Test
    public void testSelectAccountOnSearchAccountActivity() throws IOException {
        LanguageDao languageDao = AppDatabase.getInstance(context).getLanguageDao();

        List<Language> languages = languageDao.getAllRecords();
        Language defaultLanguage = languages.get(0);

        String username = selectAccountOnSearchAccountActivity();

        onView(withId(R.id.username_credential_text_view))
                .check(matches(withText(context.getString(R.string.account_username, username))));

        assertScoreHistory(username, defaultLanguage.getLanguageName());
    }

    @Test
    public void testSelectAccountOnSearchAccountActivityFilterScoreByLanguage() throws IOException {
        Language randomLanguage = getRandomLanguage(context);

        String username = selectAccountOnSearchAccountActivity();

        onView(withId(R.id.username_credential_text_view))
                .check(matches(withText(context.getString(R.string.account_username, username))));

        onView(withId(R.id.language_spinner)).perform(click());
        onData(is(randomLanguage)).inRoot(isPlatformPopup()).perform(click());

        assertScoreHistory(username, Objects.requireNonNull(randomLanguage).getLanguageName());
    }

    @Test
    public void testSelectAccountRankingActivityRendering() {
        LanguageDao languageDao = AppDatabase.getInstance(context).getLanguageDao();

        List<Language> languages = languageDao.getAllRecords();
        Language defaultLanguage = languages.get(0);

        String username = selectAccountOnRankingActivity();

        onView(withId(R.id.username_credential_text_view))
                .check(matches(withText(context.getString(R.string.account_username, username))));

        assertScoreHistory(username, defaultLanguage.getLanguageName());
    }

    @Test
    public void testSelectAccountRankingActivityFilterScoreByLanguage() {
        Language randomLanguage = getRandomLanguage(context);

        String username = selectAccountOnRankingActivity();

        onView(withId(R.id.username_credential_text_view))
                .check(matches(withText(context.getString(R.string.account_username, username))));

        onView(withId(R.id.language_spinner)).perform(click());
        onData(is(randomLanguage)).inRoot(isPlatformPopup()).perform(click());

        assertScoreHistory(username, Objects.requireNonNull(randomLanguage).getLanguageName());
    }

    /**
     * Selects randomly an account among the search results on the AccountSearchActivity.
     * @return The username of the account randomly selected
     * @throws IOException Exception that may be raised due to database access
     */
    private String selectAccountOnSearchAccountActivity() throws IOException {
        int currentPage = 1;

        Page<User> accountsPage = getAccountsPage("", currentPage);
        int numberOfAccounts = accountsPage.getCount();
        List<User> accounts = accountsPage.getResults();

        while (accountsPage.getNext() != null) {
            currentPage++;
            accountsPage = getAccountsPage("", currentPage);
            accounts.addAll(accountsPage.getResults());
        }

        browseToSearchAccountActivity();

        onView(withId(R.id.search_account_button))
                .perform(click());

        int numberOfPages = 0;

        if (numberOfAccounts > 0) {
            numberOfPages = (int) Math.ceil(((float) numberOfAccounts)/10f);
        }

        for (int i=1;i < numberOfPages;i++) {
            onView(withId(R.id.accounts_recycler_view))
                    .perform(scrollToPosition(10*i - 1));
        }

        int randomIndex = getRandomInteger(0, numberOfAccounts - 1);
        String username = accounts.get(randomIndex).getUsername();

        onView(withId(R.id.accounts_recycler_view))
                .perform(scrollToPosition(randomIndex))
                .perform(actionOnItemAtPosition(randomIndex, click()));

        return username;
    }

    /**
     * Selects randomly an account among the rankings on the RankingActivity.
     * @return The username of the account randomly selected
     */
    private String selectAccountOnRankingActivity() {
        LanguageDao languageDao = AppDatabase.getInstance(context).getLanguageDao();

        List<Language> languages = languageDao.getAllRecords();
        Language defaultLanguage = languages.get(0);

        ScoreDao scoreDao = AppDatabase.getInstance(context).getScoreDao();
        List<Score> scores = scoreDao.getTotalScoresByLanguage(Objects.requireNonNull(defaultLanguage).getLanguageName());

        int randomIndex = getRandomInteger(0, scores.size() - 1);
        String username = scores.get(randomIndex).getUser();

        browseToRankingActivity();

        onView(withId(R.id.ranking_recycler_view))
                .perform(scrollToPosition(randomIndex))
                .perform(actionOnItemAtPosition(randomIndex, click()));

        return username;
    }

    private void browseToSearchAccountActivity() {
        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_menu))
                .perform(click());
        onView(withId(R.id.menu_recycler_view))
                .perform(actionOnItemAtPosition(0, click()));
    }

    private void browseToRankingActivity() {
        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_menu))
                .perform(click());
        onView(withId(R.id.menu_recycler_view))
                .perform(actionOnItemAtPosition(1, click()));
    }
}
