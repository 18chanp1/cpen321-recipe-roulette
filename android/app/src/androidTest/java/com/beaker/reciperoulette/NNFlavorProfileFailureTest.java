package com.beaker.reciperoulette;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NNFlavorProfileFailureTest {

    @Rule
    public ActivityScenarioRule<FlavorProfile> mActivityScenarioRule =
            new ActivityScenarioRule<>(FlavorProfile.class);

    @Rule
    public TInternetOffRule ior = new TInternetOffRule();

    @Test
    public void checkTitleExists() {

        onView(withText("Flavor Profile")).check(matches(isDisplayed()));

        onView(withId(R.id.fp_title))
                .check(matches(allOf(withText(R.string.flavor_profile), isDisplayed())));
    }

    @Test
    public void checkDescriptionExists()
    {
        onView(withId(R.id.fp_details))
                .check(matches(allOf(withText(R.string.your_flavor_profile_is),
                        isDisplayed())));
    }

    @Test
    public void checkFetchedDescription() throws InterruptedException {
        //GET from server
        //Get context


        Thread.sleep(100);
        onView(withId(R.id.fp_result))
                .check(matches(allOf(withText(""), isDisplayed())));
    }
}

