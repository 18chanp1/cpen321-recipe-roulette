package com.beaker.reciperoulette;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class BRecipeGenerationTest {

    @Rule
    public ActivityScenarioRule<MainMenu> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainMenu.class);

    @Test
    public void checkGenerateRecipesButtonVisible() {
        gotoGenerateRecipe();
        onView(ViewMatchers.withId(R.id.gen_recipes))
                .check(matches(isDisplayed()));
    }

    @Test
    public void clickGenerateRecipesButtonAndCheckRecipeList() {
        gotoGenerateRecipe();
        // Generate Recipes
        onView(ViewMatchers.withId(R.id.gen_recipes))
                .perform(click());

        // Checks Recipe Display
        onView(ViewMatchers.withId(R.id.recipeButtonLayout))
                .check(matches(isDisplayed()));

    }

    @Test
    public void checkGenerateRecipesButtonAndVerifyResponseTime() {
        gotoGenerateRecipe();
        long startTime = System.currentTimeMillis();

        // Generate Recipes
        onView(ViewMatchers.withId(R.id.gen_recipes))
                .perform(click());

        // Check Recipe List
        onView(ViewMatchers.withId(R.id.recipeButtonLayout))
                .check(matches(isDisplayed()));

        long endTime = System.currentTimeMillis();

        // Calculate the response time and verify <6 second response time
        long responseTime = endTime - startTime;
        assert(responseTime < 6000);
    }

    private void gotoGenerateRecipe()
    {
        onView(withText("Recipe Engine")).perform(click());
    }

}
