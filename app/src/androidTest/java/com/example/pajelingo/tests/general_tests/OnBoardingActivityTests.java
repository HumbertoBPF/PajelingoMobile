package com.example.pajelingo.tests.general_tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.pajelingo.utils.CustomMatchers.floatActionButtonHasColor;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import androidx.test.core.app.ActivityScenario;

import com.example.pajelingo.R;
import com.example.pajelingo.activities.OnBoardingActivity;
import com.example.pajelingo.tests.abstract_tests.UITests;

import org.hamcrest.Matcher;
import org.junit.Test;

public class OnBoardingActivityTests extends UITests {
    private final SharedPreferences sp
            = context.getSharedPreferences(context.getString(R.string.sp_file_name), Context.MODE_PRIVATE);

    @Test
    public void testFirstPageOnBoarding(){
        activityScenario = ActivityScenario.launch(OnBoardingActivity.class);

        assertIsFirstOnBoardingPage();
        assertFalse(sp.getBoolean(context.getString(R.string.is_first_access), false));
    }

    @Test
    public void testFirstThenSecondPageOnBoarding(){
        activityScenario = ActivityScenario.launch(OnBoardingActivity.class);

        onView(withId(R.id.next_button)).perform(click());

        assertIsIntermediaryOnBoardingPage(R.string.on_boarding_2);
        assertFalse(sp.getBoolean(context.getString(R.string.is_first_access), false));
    }

    @Test
    public void testFirstThenSecondThenThirdPageOnBoarding(){
        activityScenario = ActivityScenario.launch(OnBoardingActivity.class);

        onView(withId(R.id.next_button))
                .perform(click());
        onView(withId(R.id.next_button))
                .perform(click());

        assertIsLastOnBoardingPage();
        assertFalse(sp.getBoolean(context.getString(R.string.is_first_access), false));
    }

    @Test
    public void testFirstThenSecondThenThirdThenSecondPageOnBoarding(){
        activityScenario = ActivityScenario.launch(OnBoardingActivity.class);

        onView(withId(R.id.next_button))
                .perform(click());
        onView(withId(R.id.next_button))
                .perform(click());
        onView(withId(R.id.back_button))
                .perform(click());

        assertIsIntermediaryOnBoardingPage(R.string.on_boarding_2);
        assertFalse(sp.getBoolean(context.getString(R.string.is_first_access), false));
    }

    @Test
    public void testFirstThenSecondThenFirstPageOnBoarding(){
        activityScenario = ActivityScenario.launch(OnBoardingActivity.class);

        onView(withId(R.id.next_button))
                .perform(click());
        onView(withId(R.id.back_button))
                .perform(click());

        assertIsFirstOnBoardingPage();
        assertFalse(sp.getBoolean(context.getString(R.string.is_first_access), false));
    }

    @Test
    public void testFirstThenSecondThenThirdThenSecondThenFirstPageOnBoarding(){
        activityScenario = ActivityScenario.launch(OnBoardingActivity.class);

        onView(withId(R.id.next_button))
                .perform(click());
        onView(withId(R.id.next_button))
                .perform(click());
        onView(withId(R.id.back_button))
                .perform(click());
        onView(withId(R.id.back_button))
                .perform(click());

        assertIsFirstOnBoardingPage();
        assertFalse(sp.getBoolean(context.getString(R.string.is_first_access), false));
    }

    @Test
    public void testCompleteOnBoarding(){
        activityScenario = ActivityScenario.launch(OnBoardingActivity.class);
        // User completes the onBoarding flow
        onView(withId(R.id.next_button))
                .perform(click());
        onView(withId(R.id.next_button))
                .perform(click());
        onView(withId(R.id.get_started_button))
                .perform(click());
        // Verifies if the user is in the MainActivity
        onView(withId(R.id.search_button)).check(matches(isDisplayed()));

        assertFalse(sp.getBoolean(context.getString(R.string.is_first_access), false));
    }

    private void assertIsFirstOnBoardingPage() {
        assertOnBoardingViews(R.string.on_boarding_1, false, true, false);
    }

    private void assertIsIntermediaryOnBoardingPage(int descriptionResource) {
        assertOnBoardingViews(descriptionResource, true, true, false);
    }


    private void assertIsLastOnBoardingPage() {
        assertOnBoardingViews(R.string.on_boarding_3, true, false, true);
    }

    private void assertOnBoardingViews(int descriptionResource, boolean isBackEnabled,
                                       boolean isNextEnabled, boolean isGetStartedVisible){
        onView(withId(R.id.on_boarding_image_view))
                .check(matches(isDisplayed()));

        Matcher<View> descriptionMatcher = allOf(withText(descriptionResource), isDisplayed());
        onView(withId(R.id.on_boarding_text_view))
                .check(matches(descriptionMatcher));

        assertFloatingActionButton(R.id.back_button, isBackEnabled);
        assertFloatingActionButton(R.id.next_button, isNextEnabled);
        onView(withId(R.id.get_started_button))
                .check(matches(isGetStartedVisible?isDisplayed():not(isDisplayed())));
    }

    private void assertFloatingActionButton(int fabId, boolean isEnabled){
        int colorResourceId = isEnabled?R.color.blue:R.color.gray;
        Matcher<View> isEnabledMatcher = isEnabled?isEnabled():not(isEnabled());

        Matcher<View> fabMatcher = allOf(floatActionButtonHasColor(context.getResources().getColor(colorResourceId)), isEnabledMatcher, isDisplayed());

        onView(withId(fabId))
                .check(matches(fabMatcher));
    }
}
