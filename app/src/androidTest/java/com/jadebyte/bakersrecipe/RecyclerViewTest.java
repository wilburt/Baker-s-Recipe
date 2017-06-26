package com.jadebyte.bakersrecipe;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;

import com.jadebyte.bakersrecipe.activities.TestActivity;
import com.jadebyte.bakersrecipe.adapters.TestAdapter;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by wilbur on 6/26/17 at 9:47 AM.
 */

public class RecyclerViewTest {
    private static final int ITEM_BELOW_THE_FOLD = 20;

    @Rule
    public ActivityTestRule<TestActivity> activityTestRule = new ActivityTestRule<>(
            TestActivity.class);

    @Test
    public void scrollToItemBelowFoldCheckItsText() {

        onView(ViewMatchers.withId(R.id.test_recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(ITEM_BELOW_THE_FOLD, click()));

        String itemText = activityTestRule.getActivity().getResources().getString(
                R.string.test_item_text) + String.valueOf(ITEM_BELOW_THE_FOLD);
        onView(withText(itemText)).check(matches(isDisplayed()));
    }

    @Test
    public void itemInMiddleOfListHasSpecialText() {

        onView(ViewMatchers.withId(R.id.test_recyclerView))
                .perform(RecyclerViewActions.scrollToHolder(isInTheMiddle()));

        String middleElementText = activityTestRule.getActivity().getResources().getString(R.string.middle_item);
        onView(withText(middleElementText)).check(matches(isDisplayed()));
    }


    private static Matcher<TestAdapter.TestHolder> isInTheMiddle() {
        return new TypeSafeMatcher<TestAdapter.TestHolder>() {
            @Override
            protected boolean matchesSafely(TestAdapter.TestHolder testHolder) {
                return testHolder.getIsInTheMiddle();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("item in the middle");
            }
        };
    }
}
