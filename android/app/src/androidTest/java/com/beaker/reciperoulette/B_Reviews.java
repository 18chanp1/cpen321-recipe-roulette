package com.beaker.reciperoulette;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.fail;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.view.View;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.beaker.reciperoulette.RecipeFacebook.Review;
import com.google.gson.Gson;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.beaker.reciperoulette.RecyclerViewActions.scrollToPosition;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class B_Reviews {

    @Rule
    public ActivityScenarioRule<MainMenu> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainMenu.class);

    @Test
    public void testReviewsEntry() {
        onView(withText(R.string.recipe_review))
                .perform(click());

        RecyclerViewMatcher rvm = new RecyclerViewMatcher(R.id.rev_recycler);

        //Get context
        Context c = InstrumentationRegistry.getInstrumentation().getTargetContext();

        //get token
        SharedPreferences sharedPref =
                c.getSharedPreferences(c.getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        String tok = sharedPref.getString("TOKEN", "NOTOKEN");
        String email = sharedPref.getString("EMAIL", "NOEMAIL");

        // fetch the list of items
        Request req = new Request.Builder()
                .url("https://cpen321-reciperoulette.westus.cloudapp.azure.com/reviews")
                .addHeader("email", email)
                .addHeader("userToken", tok)
                .build();

        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(req).execute())
        {
            if(response.isSuccessful())
            {
                String res = response.body().string();
                Review[] userArray = new Gson().fromJson(res, Review[].class);

                for(int i = 0; i < userArray.length; i++)
                {
                    onView(withId(R.id.rev_recycler))
                            .perform(scrollToPosition(i));
                    Matcher<View> currentMatch = new RecyclerViewMatcher(R.id.rev_recycler).atPosition(i);

                    if(userArray[i].getTitle() != null)
                    {
                        onView(currentMatch)
                                .check(matches(hasDescendant(withText(userArray[i].getTitle()))));
                    }

                    if (userArray[i].getAuthor() != null)
                    {
                        onView(currentMatch)
                                .check(matches(hasDescendant(withText(userArray[i].getAuthor()))));
                    }

                }
            }
            else
            {
                fail("Entry does not match");
            }


        } catch (IOException e) {
            fail("Failed to get list");
        }

    }

    @Test
    public void testReviewsDetailed() {
        onView(withText(R.string.recipe_review))
                .perform(click());
        RecyclerViewMatcher rvm = new RecyclerViewMatcher(R.id.rev_recycler);

        //Get context
        Context c = InstrumentationRegistry.getInstrumentation().getTargetContext();

        //get token
        SharedPreferences sharedPref =
                c.getSharedPreferences(c.getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        String tok = sharedPref.getString("TOKEN", "NOTOKEN");
        String email = sharedPref.getString("EMAIL", "NOEMAIL");

        // fetch the list of items
        Request req = new Request.Builder()
                .url("https://cpen321-reciperoulette.westus.cloudapp.azure.com/reviews")
                .addHeader("email", email)
                .addHeader("userToken", tok)
                .build();

        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(req).execute()) {
            if (response.isSuccessful()) {
                String res = response.body().string();
                Review[] userArray = new Gson().fromJson(res, Review[].class);

                for (int i = 0; i < userArray.length; i++) {
                    onView(withId(R.id.rev_recycler))
                            .perform(scrollToPosition(i));
                    Matcher<View> currentMatch = new RecyclerViewMatcher(R.id.rev_recycler).atPosition(i);
                    //click the read more button;
                    onView(allOf(withParent(currentMatch), withId(R.id.rev_but)))
                            .perform(click());

                    //assert that the title is the same
                    onView(withId(R.id.detailed_title))
                            .check(matches(isDisplayed()))
                            .check(matches(withText(userArray[i].getTitle())));

                    //assert that contact is the same
                    onView(withId(R.id.detailed_author))
                            .check(matches(isDisplayed()))
                            .check(matches(withText(userArray[i].getAuthor())));

                    //assert that rating is the same
                    onView(withId(R.id.detailed_rating))
                            .check(matches(isDisplayed()))
                            .check(matches(withText("Rating: " + userArray[i].getRating())));

                    //assert that the review is the same
                    String review = Html.fromHtml(userArray[i].getReview(), Html.FROM_HTML_MODE_COMPACT).toString();
                    onView(withId(R.id.detailed_text))
                            .check(matches(isDisplayed()))
                            .check(matches(withText(review)));

                    //assert that like buttons work
                    onView(withId(R.id.like_button))
                            .perform(scrollTo())
                            .check(matches(isDisplayed()))
                            .check(matches(withText(R.string.like)));

                    //test the like button
                    onView(withId(R.id.like_button))
                            .perform(click());

                    //go back to refresh item
                    pressBack();
                    pressBack();
                    onView(withText("Recipe Reviews")).perform(click());
                    onView(withId(R.id.rev_recycler))
                            .perform(scrollToPosition(i));
                    currentMatch = new RecyclerViewMatcher(R.id.rev_recycler).atPosition(i);
                    onView(allOf(withParent(currentMatch), withId(R.id.rev_but)))
                            .perform(click());

                    //check that like count has increased
                    String newRating = String.valueOf(Integer.valueOf(userArray[i].getRating() + 1)) ;
                    onView(withId(R.id.detailed_rating))
                            .check(matches(isDisplayed()))
                            .check(matches(withText("Rating: " + newRating)));

                    pressBack();


                }
            } else {
                fail("Entry does not match");
            }


        } catch (IOException e) {
            fail("Failed to get list");
        }
    }


}
