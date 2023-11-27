package com.beaker.reciperoulette;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.fail;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class BFlavorProfileTest {

    @Rule
    public ActivityScenarioRule<FlavorProfile> mActivityScenarioRule =
            new ActivityScenarioRule<>(FlavorProfile.class);

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
    public void checkFetchedDescription()
    {
        //GET from server
        //Get context
        Context c = InstrumentationRegistry.getInstrumentation().getTargetContext();

        //get token
        SharedPreferences sharedPref =
                c.getSharedPreferences(c.getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        String tok = sharedPref.getString("TOKEN", "NOTOKEN");
        String email = sharedPref.getString("EMAIL", "NOEMAIL");

        // fetch the list of items
        Request req = new Request.Builder()
                .url("https://cpen321-reciperoulette.westus.cloudapp.azure.com/flavourprofile")
                .addHeader("email", email)
                .addHeader("userToken", tok)
                .build();

        OkHttpClient client = new OkHttpClient();

        try(Response res = client.newCall(req).execute())
        {
            if(res.isSuccessful())
            {
                assert res.body() != null;
                String result = res.body().string();

                //assert fetched result is the same
                onView(withId(R.id.fp_result))
                        .check(matches(allOf(withText(result), isDisplayed())));
            }
            else {
                fail("Server Error");
            }

        } catch (IOException e) {
            fail("Could not connect");
        }
    }
}
