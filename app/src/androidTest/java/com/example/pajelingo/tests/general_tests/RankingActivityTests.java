package com.example.pajelingo.tests.general_tests;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.CustomMatchers.atPosition;
import static com.example.pajelingo.utils.CustomMatchers.hasLength;
import static com.example.pajelingo.utils.RandomTools.getRandomLanguage;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.view.View;

import androidx.test.core.app.ActivityScenario;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.MainActivity;
import com.example.pajelingo.daos.ScoreDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Language;
import com.example.pajelingo.models.Score;
import com.example.pajelingo.tests.abstract_tests.UITests;

import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.List;
import java.util.Objects;

public class RankingActivityTests extends UITests {
    @Test
    public void testSelectGame(){
        Language randomLanguage = getRandomLanguage(context);

        ScoreDao scoreDao = AppDatabase.getInstance(context).getScoreDao();
        List<Score> scores = scoreDao.getTotalScoresByLanguage(Objects.requireNonNull(randomLanguage).getLanguageName());

        activityScenario = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.action_menu))
                .perform(click());
        onView(withId(R.id.menu_item_rankings))
                .perform(click());
        onView(withId(R.id.language_spinner))
                .perform(click());
        onData(is(randomLanguage))
                .inRoot(isPlatformPopup())
                .perform(click());

        onView(withId(R.id.ranking_recycler_view))
                .check(matches(hasLength(scores.size())));

        for (int i=0;i<scores.size();i++){
            Score score = scores.get(i);

            String position = String.valueOf(i + 1);
            String username = score.getUser();
            String scoreValue = score.getScore().toString();

            Matcher<View> positionMatcher = allOf(withId(R.id.position_text_view), withText(position));
            Matcher<View> usernameMatcher = allOf(withId(R.id.username_text_view), withText(username));
            Matcher<View> scoreMatcher = allOf(withId(R.id.score_text_view), withText(scoreValue));

            onView(withId(R.id.ranking_recycler_view))
                    .perform(scrollToPosition(i))
                    .check(matches(atPosition(hasDescendant(positionMatcher), i)))
                    .check(matches(atPosition(hasDescendant(usernameMatcher), i)))
                    .check(matches(atPosition(hasDescendant(scoreMatcher), i)));
        }
    }
}
