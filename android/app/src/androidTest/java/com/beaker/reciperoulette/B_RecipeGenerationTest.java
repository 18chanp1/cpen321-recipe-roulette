package com.beaker.reciperoulette;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class B_RecipeGenerationTest {

    @Rule
    public ActivityScenarioRule<RecipeSelect> mActivityScenarioRule =
            new ActivityScenarioRule<>(RecipeSelect.class);

    @Test
    public void checkGenerateRecipesButtonVisible() {
        Espresso.onView(ViewMatchers.withId(R.id.gen_recipes))
                .check(matches(isDisplayed()));
    }

    @Test
    public void clickGenerateRecipesButtonAndCheckRecipeList() {
        // Generate Recipes
        Espresso.onView(ViewMatchers.withId(R.id.gen_recipes))
                .perform(ViewActions.click());

        // Checks Recipe Display
        Espresso.onView(ViewMatchers.withId(R.id.recipeButtonLayout))
                .check(matches(isDisplayed()));

    }

    @Test
    public void checkGenerateRecipesButtonAndVerifyResponseTime() {
        long startTime = System.currentTimeMillis();

        // Generate Recipes
        Espresso.onView(ViewMatchers.withId(R.id.gen_recipes))
                .perform(ViewActions.click());

        // Check Recipe List
        Espresso.onView(ViewMatchers.withId(R.id.recipeButtonLayout))
                .check(matches(isDisplayed()));

        long endTime = System.currentTimeMillis();

        // Calculate the response time and verify <6 second response time
        long responseTime = endTime - startTime;
        assert(responseTime < 6000);
    }

}
