package com.beaker.reciperoulette;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NNRecipeGenerationTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public TInternetOffRule ior = new TInternetOffRule();

    @Before
    public void waitForMenu() throws InterruptedException {
        Thread.sleep(500);
    }

    @Test
    public void checkGenerateRecipesButtonVisible() {
        gotoGenerateRecipe();
        String tagval = "valentinosfavoritebutton";
        onView(withId(R.id.recipeButtonLayout)).check(matches(not(withTagValue(is(tagval)))));
    }


    private void gotoGenerateRecipe()
    {
        onView(withText("Recipe Engine")).perform(click());
    }

}
