package com.beaker.reciperoulette;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.gson.Gson;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.beaker.reciperoulette.RecyclerViewActions.*;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class ReviewsTest {

    private static boolean setup = false;

    @Rule
    public ActivityScenarioRule<MainMenu> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainMenu.class);

    @Test
    public void testReviewsEntry() {
        setup();

        RecyclerViewMatcher rvm = new RecyclerViewMatcher(R.id.recyclerView);



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

                Log.d("RecipeFacebook", res);

                Review[] userArray = new Gson().fromJson(res, Review[].class);

                List<Review> reviews = new ArrayList<Review>();


                for(int i = 0; i < userArray.length; i++)
                {
                    Matcher<View> currentMatch = new RecyclerViewMatcher(R.id.recyclerView).atPosition(i);

                    onView(withId(R.id.recyclerView))
                            .perform(scrollToPosition(i));

                    onView(currentMatch)
                            .check(matches(hasDescendant(withText(userArray[i].title))));
                }

            }
            else
            {
                fail("Entry does not match");
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void setup()
    {
        if(!setup)
        {
            ViewInteraction materialButton = onView(
                    allOf(withId(R.id.menu5), withText("Recipe Reviews")));
            materialButton.perform(click());
        }

    }

}
