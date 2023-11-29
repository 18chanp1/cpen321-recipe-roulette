package com.beaker.reciperoulette;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.view.View;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class BMainMenuTest {

    /**
     * The following tests asserts that all the buttons can be found in the main menu.
     */

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void waitForMenu() throws InterruptedException {
        Thread.sleep(500);
    }
    @Test
    public void findRecipeEngineButton()
    {
        ViewInteraction button = onView(
                allOf(withId(R.id.menu1), withText("Recipe Engine"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.view.ViewGroup.class))),
                        isDisplayed()));
        button.check(matches(isDisplayed()));
    }

    @Test
    public void findflavorProfileButton()
    {
        ViewInteraction button2 = onView(
                allOf(withId(R.id.menu2), withText("Flavor Profile"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.view.ViewGroup.class))),
                        isDisplayed()));
        button2.check(matches(isDisplayed()));
    }


    @Test
    public void findRequestIngredientButton()
    {
        ViewInteraction button3 = onView(
                allOf(withId(R.id.menu3), withText("Request Ingredient"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.view.ViewGroup.class))),
                        isDisplayed()));
        button3.check(matches(isDisplayed()));
    }

    @Test
    public void findCookWithOthersButton()
    {
        ViewInteraction button4 = onView(
                allOf(withId(R.id.menu4), withText("Cook with others"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.view.ViewGroup.class))),
                        isDisplayed()));
        button4.check(matches(isDisplayed()));
    }

    @Test
    public void findRecipeReviewsButton()
    {
        ViewInteraction button5 = onView(
                allOf(withId(R.id.menu5), withText("Recipe Reviews"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.view.ViewGroup.class))),
                        isDisplayed()));
        button5.check(matches(isDisplayed()));
    }

    @Test
    public void findTakePhotoButton() {
        ViewInteraction button6 = onView(
                allOf(withId(R.id.menu6), withText("Add to Pantry"),
                        withParent(withParent(IsInstanceOf.instanceOf(android.view.ViewGroup.class))),
                        isDisplayed()));
        button6.check(matches(isDisplayed()));
    }
}
