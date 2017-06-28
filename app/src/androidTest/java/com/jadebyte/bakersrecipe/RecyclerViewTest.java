package com.jadebyte.bakersrecipe;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;

import com.jadebyte.bakersrecipe.activities.RecipeActivity;
import com.jadebyte.bakersrecipe.pojos.Recipe;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.Random;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by wilbur on 6/26/17 at 9:47 AM.
 */

public class RecyclerViewTest {

    @Rule
    public ActivityTestRule<RecipeActivity> activityTestRule = new ActivityTestRule<>(
            RecipeActivity.class);

    private IdlingResource idlingResource;

    @Before
    public void registerIdlingResource() {
        idlingResource = activityTestRule.getActivity().getIdlingResource();
        Espresso.registerIdlingResources(idlingResource);
    }


    @Test
    public void testResponse() {
        List<Recipe> recipeList = activityTestRule.getActivity().getRecipeList();

        if (recipeList.size() == 0) { // Network request failed

            onView(withId(R.id.recipe_info_error_button)).check(matches(isDisplayed()));
            onView(withId(R.id.recipe_info_error_text)).check(matches(isDisplayed()));

        } else { // Network request is successful
            Random random = new Random();
            onView(ViewMatchers.withId(R.id.recipe_recycler))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(random.nextInt(recipeList.size()), click()));

        }

    }


    @After
    public void unregisterIdlingResource() {
        if (idlingResource != null) {
            Espresso.unregisterIdlingResources(idlingResource);
        }
    }
}
