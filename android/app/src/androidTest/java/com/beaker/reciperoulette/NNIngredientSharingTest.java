package com.beaker.reciperoulette;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.beaker.reciperoulette.RecyclerViewActions.scrollToPosition;
import static org.hamcrest.Matchers.allOf;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class NNIngredientSharingTest {

    @Rule
    public ActivityScenarioRule<MainMenu> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainMenu.class);

    @Rule
    public TInternetOffRule ior = new TInternetOffRule();
    @Before
    public void waitForMenu() throws InterruptedException {
        Context c = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPreferences sharedPref =
                c.getSharedPreferences(c.getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(c.getString(R.string.prf_token), "TESTTOKEN");
        editor.putString(c.getString(R.string.prf_eml), "18chanp1@gmail.com");
        editor.apply();    }

    @Test
    public void allElementsPresent() {
        onView(withText(R.string.req_ingredient)).perform(click());

        onView(withId(R.id.rq_title))
                .check(matches(allOf(
                        withText(R.string.req_ingred),
                        isDisplayed()
                )));

        onView(withId(R.id.rq_ingredient_entry))
                .check(matches(allOf(
                        ViewMatchers.withHint(R.string.ingredient),
                        isDisplayed()
                )));
        onView(withId(R.id.rq_contact_entry))
                .check(matches(allOf(
                        ViewMatchers.withHint(R.string.contact_details),
                        isDisplayed()
                )));
        onView(withId(R.id.rq_ingredreq_but))
                .check(matches(allOf(
                        withText(R.string.req_ingredient),
                        isDisplayed()
                )));
        onView(withId(R.id.rq_selfreq_but))
                .check(matches(allOf(
                        withText(R.string.view_prev_req),
                        isDisplayed()
                )));
        onView(withId(R.id.rq_donate_title))
                .check(matches(allOf(
                        withText(R.string.donate_ingred),
                        isDisplayed()
                )));
        onView(withId(R.id.rq_recycler))
                .check(matches(allOf(
                        isDisplayed()
                )));
    }

    @Test
    public void testIngredientRequestsNoNetwork() throws InterruptedException {
        //Get context
        onView(withText(R.string.req_ingredient)).perform(click());

        Thread.sleep(100);

        for (int i = 0; i < 50; i++) {
            onView(withId(R.id.rq_recycler))
                    .perform(scrollToPosition(i));
            Matcher<View> currentMatch = new RecyclerViewMatcher(R.id.rq_recycler).atPosition(i);
            onView(currentMatch)
                    .check(doesNotExist());
        }

    }
}