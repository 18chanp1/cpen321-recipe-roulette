package com.beaker.reciperoulette;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.beaker.reciperoulette.RecyclerViewActions.scrollToPosition;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.fail;

import android.content.Context;
import android.view.View;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class NN_ReviewsTest {

    @Rule
    public T_InternetOffRule TInternetOffRule =
            new T_InternetOffRule();
    @Rule
    public ActivityScenarioRule<MainMenu> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainMenu.class);



    @Test
    public void testReviewsEntry() throws InterruptedException {
        RecyclerViewMatcher rvm = new RecyclerViewMatcher(R.id.rev_recycler);

        onView(withId(R.id.menu5)).perform(click());

        //Get context
        Context c = InstrumentationRegistry.getInstrumentation().getTargetContext();

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




