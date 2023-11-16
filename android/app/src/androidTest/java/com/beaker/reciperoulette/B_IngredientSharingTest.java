package com.beaker.reciperoulette;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.beaker.reciperoulette.RecyclerViewActions.scrollToPosition;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.fail;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
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
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class B_IngredientSharingTest {

    @Rule
    public ActivityScenarioRule<MainMenu> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainMenu.class);

    @Test
    public void allElementsPresent()
    {
        onView(withText(R.string.req_ingredient))
                .perform(click());

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
    public void testRequestsEntriesMatch() {
        onView(withText(R.string.req_ingredient))
                .perform(click());

        RecyclerViewMatcher rvm = new RecyclerViewMatcher(R.id.rq_recycler);

        //Get context
        Context c = InstrumentationRegistry.getInstrumentation().getTargetContext();

        //get token
        SharedPreferences sharedPref =
                c.getSharedPreferences(c.getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        String tok = sharedPref.getString("TOKEN", "NOTOKEN");
        String email = sharedPref.getString("EMAIL", "NOEMAIL");

        // fetch the list of items
        Request req = new Request.Builder()
                .url("https://cpen321-reciperoulette.westus.cloudapp.azure.com/ingredientrequests")
                .addHeader("email", email)
                .addHeader("userToken", tok)
                .build();

        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(req).execute())
        {
            if(response.isSuccessful())
            {
                String res = response.body().string();
                IngredientRequest[] userArray = new Gson().fromJson(res, IngredientRequest[].class);

                for(int i = 0; i < userArray.length; i++)
                {
                    onView(withId(R.id.rq_recycler))
                            .perform(scrollToPosition(i));
                    Matcher<View> currentMatch = new RecyclerViewMatcher(R.id.rq_recycler).atPosition(i);

                    onView(currentMatch)
                            .check(matches(hasDescendant(allOf(
                                    withText(userArray[i].ingredientName),
                                    isDisplayed()))));
                    onView(currentMatch)
                            .check(matches(hasDescendant(allOf(
                                    withText(userArray[i].userId),
                                    isDisplayed()))));
                    onView(currentMatch)
                            .check(matches(hasDescendant(allOf(withId(R.id.rq_donate_but),
                                    withText(R.string.donate),
                                    isDisplayed()))));
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
    public void makeRequest() throws InterruptedException {
        onView(withText(R.string.req_ingredient))
                .perform(click());
        String testFood = "Horse Testicles" + new Date().toString();
        String contactDet = "123 456";

        onView(withHint(R.string.ingredient))
                .perform(typeText(testFood));

        onView(withHint(R.string.contact_details))
                .perform(typeText(contactDet));

        onView(allOf(withId(R.id.rq_ingredreq_but), withText(R.string.req_ingredient)))
                .perform(click());

        //restart
        pressBack();
        restartActivity();

        //iterate through recycler view
        RecyclerViewMatcher rvm = new RecyclerViewMatcher(R.id.rq_recycler);

        //Get context
        Context c = InstrumentationRegistry.getInstrumentation().getTargetContext();

        //get token
        SharedPreferences sharedPref =
                c.getSharedPreferences(c.getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        String tok = sharedPref.getString("TOKEN", "NOTOKEN");
        String email = sharedPref.getString("EMAIL", "NOEMAIL");

        // fetch the list of items
        Request req = new Request.Builder()
                .url("https://cpen321-reciperoulette.westus.cloudapp.azure.com/ingredientrequests")
                .addHeader("email", email)
                .addHeader("userToken", tok)
                .build();

        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(req).execute())
        {
            if(response.isSuccessful())
            {
                String res = response.body().string();
                IngredientRequest[] userArray = new Gson().fromJson(res, IngredientRequest[].class);

                for(int i = 0; i < userArray.length; i++)
                {
                    if(userArray[i].userId.equals(email) &&
                            userArray[i].ingredientName.equals(testFood))
                    {
                        onView(withId(R.id.rq_recycler))
                                .perform(scrollToPosition(i));
                        Matcher<View> currentMatch = new RecyclerViewMatcher(R.id.rq_recycler).atPosition(i);

                        onView(currentMatch)
                                .check(matches(hasDescendant(allOf(
                                        withText(userArray[i].ingredientName),
                                        isDisplayed()))));
                        onView(currentMatch)
                                .check(matches(hasDescendant(allOf(
                                        withText(userArray[i].userId),
                                        isDisplayed()))));
                        onView(currentMatch)
                                .check(matches(hasDescendant(allOf(withId(R.id.rq_donate_but),
                                        withText(R.string.donate),
                                        isDisplayed()))));
                        break;
                    } else if (i == userArray.length - 1)
                    {
                        fail("Could not find the item");
                    }
                }
            }
            else
            {
                fail("Cannot get requests");
            }


        } catch (IOException e) {
            fail("Failed to get list");
        }




    }

    @Test
    public void testDonationButton()
    {
        onView(withText(R.string.req_ingredient))
                .perform(click());

        String testFood = "Horse Testicles" + new Date().toString();
        String contactDet = "123 456";

        onView(withId(R.id.rq_ingredient_entry))
                .perform(typeText(testFood));

        onView(withId(R.id.rq_contact_entry))
                .perform(typeText(contactDet));

        onView(withId(R.id.rq_ingredreq_but))
                .perform(click());

        //restart
//        restartActivity();

        pressBack();
        pressBack();
        onView(withText(R.string.req_ingredient))
                .perform(click());

        //iterate through recycler view
        RecyclerViewMatcher rvm = new RecyclerViewMatcher(R.id.rq_recycler);

        //Get context
        Context c = InstrumentationRegistry.getInstrumentation().getTargetContext();

        //get token
        SharedPreferences sharedPref =
                c.getSharedPreferences(c.getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        String tok = sharedPref.getString("TOKEN", "NOTOKEN");
        String email = sharedPref.getString("EMAIL", "NOEMAIL");

        // fetch the list of items
        Request req = new Request.Builder()
                .url("https://cpen321-reciperoulette.westus.cloudapp.azure.com/ingredientrequests")
                .addHeader("email", email)
                .addHeader("userToken", tok)
                .build();

        OkHttpClient client = new OkHttpClient();
        try (Response response = client.newCall(req).execute())
        {
            if(response.isSuccessful())
            {
                String res = response.body().string();
                IngredientRequest[] userArray = new Gson().fromJson(res, IngredientRequest[].class);

                //find the entry and remove it.
                for(int i = 0; i < userArray.length; i++)
                {
                    if(userArray[i].userId.equals(email) &&
                            userArray[i].ingredientName.equals(testFood))
                    {
                        onView(withId(R.id.rq_recycler))
                                .perform(scrollToPosition(i));
                        Matcher<View> currentMatch = new RecyclerViewMatcher(R.id.rq_recycler).atPosition(i);

                        onView(allOf(
                                withParent(currentMatch),
                                withId(R.id.rq_donate_but),
                                withText(R.string.donate))
                        )
                                .perform(click());

                        break;
                    } else if (i == userArray.length - 1)
                    {
                        fail("Could not find the item");
                    }
                }

                restartActivity();

                //search to see if entry is gone
                for(int i = 0; i < userArray.length; i++)
                {
                    onView(withId(R.id.rq_recycler))
                            .perform(scrollToPosition(i));
                    Matcher<View> currentMatch = new RecyclerViewMatcher(R.id.rq_recycler).atPosition(i);

                    //check there is no such entry
                    onView(allOf(
                            withParent(currentMatch),
                            withId(R.id.rq_entry_name),
                            withText(testFood)
                    )).check(
                                    doesNotExist()
                            );

                }
            }
            else
            {
                fail("Cannot get requests");
            }


        } catch (IOException e) {
            fail("Failed to get list");
        }
    }

    public void restartActivity()
    {
        pressBack();
        onView(withText(R.string.req_ingredient))
                .perform(click());

//        ActivityScenario<MainMenu> scenario =
//                mActivityScenarioRule.getScenario();
//
//        scenario.moveToState(Lifecycle.State.RESUMED);
//        scenario.recreate();
    }

}






