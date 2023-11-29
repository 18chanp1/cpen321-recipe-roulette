package com.beaker.reciperoulette;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.beaker.reciperoulette.RecyclerViewActions.scrollToPosition;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

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
public class NNReviewsTest {

    @Rule
    public TInternetOffRule TInternetOffRule =
            new TInternetOffRule();
    @Rule
    public ActivityScenarioRule<MainMenu> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainMenu.class);

    @Before
    public void waitForMenu() throws InterruptedException {
        Context c = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPreferences sharedPref =
                c.getSharedPreferences(c.getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(c.getString(R.string.prf_token), "TESTTOKEN");
        editor.putString(c.getString(R.string.prf_eml), "18chanp1@gmail.com");
        editor.apply();
    }

    @Test
    public void testReviewsEntry() throws InterruptedException {

        onView(withId(R.id.menu5)).perform(click());

        //Get context

        Thread.sleep(100);
        onView(withId(R.id.rev_recycler))
                .perform(scrollToPosition(0));

        for(int i = 0; i < 50; i++)
        {
            onView(withId(R.id.rev_recycler))
                    .perform(scrollToPosition(i));
            Matcher<View> currentMatch = new RecyclerViewMatcher(R.id.rev_recycler).atPosition(i);
            onView(currentMatch)
                    .check(doesNotExist());
        }

    }

}




