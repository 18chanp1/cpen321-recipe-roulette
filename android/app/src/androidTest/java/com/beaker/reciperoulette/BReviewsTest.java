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
import static com.beaker.reciperoulette.RecyclerViewActions.scrollToPosition;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.fail;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.view.View;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.beaker.reciperoulette.reviews.Review;
import com.google.gson.Gson;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class BReviewsTest {

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
    public void testReviewsEntry() {
        onView(withText(R.string.recipe_review))
                .perform(click());

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
                assert response.body() != null;
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
                assert response.body() != null;
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
                            .perform(scrollTo())
                            .check(matches(isDisplayed()))
                            .check(matches(withText(userArray[i].getTitle())));

                    //assert that contact is the same
                    onView(withId(R.id.detailed_author))
                            .perform(scrollTo())
                            .check(matches(isDisplayed()))
                            .check(matches(withText(userArray[i].getAuthor())));

                    //assert that rating is the same
                    onView(withId(R.id.detailed_rating))
                            .perform(scrollTo())
                            .check(matches(isDisplayed()))
                            .check(matches(withText("Rating:" + userArray[i].getRating())));

                    //assert that the review is the same
                    String review = Html.fromHtml(userArray[i].getReview(), Html.FROM_HTML_MODE_COMPACT).toString();
                    onView(withId(R.id.detailed_text))
                            .perform(scrollTo())
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


                    //check that like count has increased
                    String newRating = String.valueOf(Integer.valueOf(userArray[i].getRating() + 1)) ;
                    onView(withId(R.id.detailed_rating))
                            .perform(scrollTo())
                            .check(matches(isDisplayed()))
                            .check(matches(withText("Rating:" + newRating)));

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
