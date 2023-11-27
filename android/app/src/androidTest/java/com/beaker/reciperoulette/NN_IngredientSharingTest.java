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
import static org.junit.Assert.fail;

import android.content.Context;
import android.view.View;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.beaker.reciperoulette.requests.IngredientRequestView;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class NN_IngredientSharingTest {

    @Rule
    public ActivityScenarioRule<IngredientRequestView> mActivityScenarioRule =
            new ActivityScenarioRule<>(IngredientRequestView.class);

    @Rule
    public T_InternetOffRule ior = new T_InternetOffRule();

    @Test
    public void allElementsPresent() {
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
        RecyclerViewMatcher rvm = new RecyclerViewMatcher(R.id.rq_recycler);
        //Get context
        Context c = InstrumentationRegistry.getInstrumentation().getTargetContext();

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