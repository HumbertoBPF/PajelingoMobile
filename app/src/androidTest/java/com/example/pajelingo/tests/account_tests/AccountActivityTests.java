package com.example.pajelingo.tests.account_tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.CustomMatchers.atPosition;
import static com.example.pajelingo.utils.CustomMatchers.hasLength;
import static com.example.pajelingo.utils.RandomTools.getRandomInteger;
import static org.hamcrest.Matchers.allOf;

import android.view.View;

import com.example.pajelingo.R;
import com.example.pajelingo.daos.ScoreDao;
import com.example.pajelingo.database.settings.AppDatabase;
import com.example.pajelingo.models.Badge;
import com.example.pajelingo.models.Score;
import com.example.pajelingo.tests.abstract_tests.UITests;

import org.hamcrest.Matcher;

import java.util.List;

public class AccountActivityTests extends UITests {
    /**
     * Verifies that the scores shown on the MyProfileActivity match the scores of the authenticated
     * user in the specified language.
     * @param username username that owns the profile
     * @param languageName selected language filter
     */
    protected void assertScoreHistory(String username, String languageName) {
        ScoreDao scoreDao = AppDatabase.getInstance(context).getScoreDao();

        List<Score> scores = scoreDao.getScoresByUserAndByLanguage(username, languageName);

        onView(withId(R.id.score_recycler_view)).check(matches(hasLength(scores.size())));

        for (int i = 0;i < scores.size();i++){
            Score score = scores.get(i);

            String gameName = score.getGame();
            String scoreValue = score.getScore().toString();

            Matcher<View> gameMatcher = allOf(withId(R.id.game_text_view), withText(gameName));
            Matcher<View> scoreMatcher = allOf(withId(R.id.score_text_view), withText(scoreValue));

            onView(withId(R.id.score_recycler_view))
                    .check(matches(atPosition(hasDescendant(gameMatcher) , i)))
                    .check(matches(atPosition(hasDescendant(scoreMatcher), i)));
        }
    }

    protected void assertBadges(List<Badge> badges) {
        for (Badge badge: badges) {
            Matcher<View> badgeMatcher = hasDescendant(withText(badge.getName()));

            onView(withId(R.id.badges_recycler_view))
                    .check(matches(badgeMatcher));
        }

        int nbBadges = getRandomInteger(0, badges.size() - 1);
        Badge randomBadge = badges.get(nbBadges);

        onView(withText(randomBadge.getName())).perform(click());
        onView(withText(randomBadge.getName())).check(matches(isDisplayed()));
        onView(withText(randomBadge.getDescription())).check(matches(isDisplayed()));
        onView(withText(R.string.badge_details_confirm_button_text)).perform(click());
    }
}
