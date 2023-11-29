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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;
import android.view.View;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.beaker.reciperoulette.requests.IngredientRequest;
import com.google.gson.Gson;

import org.hamcrest.Matcher;
import org.junit.Before;
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
public class BIngredientSharingTest {

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
                        withHint(R.string.ingredient),
                        isDisplayed()
                )));
        onView(withId(R.id.rq_contact_entry))
                .check(matches(allOf(
                        withHint(R.string.contact_details),
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
                assert response.body() != null;
                String res = response.body().string();
                IngredientRequest[] userArray = new Gson().fromJson(res, IngredientRequest[].class);

                for(int i = 0; i < userArray.length; i++)
                {
                    onView(withId(R.id.rq_recycler))
                            .perform(scrollToPosition(i));
                    Matcher<View> currentMatch = new RecyclerViewMatcher(R.id.rq_recycler).atPosition(i);

                    if(userArray[i].getIngredientName() != null)
                    {
                        onView(currentMatch)
                                .check(matches(hasDescendant(allOf(
                                        withText(userArray[i].getIngredientName()),
                                        isDisplayed()))));
                    }
                    if (userArray[i].getUserId() != null)
                    {
                        onView(currentMatch)
                                .check(matches(hasDescendant(allOf(
                                        withText(userArray[i].getUserId()),
                                        isDisplayed()))));
                    }

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
    public void makeRequest() {
        onView(withText(R.string.req_ingredient))
                .perform(click());
        String testFood = "Horse Testicles" + new Date();
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
                assert response.body() != null;
                String res = response.body().string();
                IngredientRequest[] userArray = new Gson().fromJson(res, IngredientRequest[].class);

                for(int i = 0; i < userArray.length; i++)
                {
                    if(userArray[i].getUserId().equals(email) &&
                            userArray[i].getIngredientName().equals(testFood))
                    {
                        onView(withId(R.id.rq_recycler))
                                .perform(scrollToPosition(i));
                        Matcher<View> currentMatch = new RecyclerViewMatcher(R.id.rq_recycler).atPosition(i);

                        onView(currentMatch)
                                .check(matches(hasDescendant(allOf(
                                        withText(userArray[i].getIngredientName()),
                                        isDisplayed()))));
                        onView(currentMatch)
                                .check(matches(hasDescendant(allOf(
                                        withText(userArray[i].getUserId()),
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

        String testFood = "Horse Testicles" + new Date();
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
                assert response.body() != null;
                String res = response.body().string();
                IngredientRequest[] userArray = new Gson().fromJson(res, IngredientRequest[].class);

                //find the entry and remove it.
                for(int i = 0; i < userArray.length; i++)
                {
                    if(userArray[i].getUserId().equals(email) &&
                            userArray[i].getIngredientName().equals(testFood))
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


                //check for notification
                UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
                device.openNotification();
                device.wait(Until.hasObject(By.textContains("fulfilled")), 5000);
                UiObject2 notif = device.findObject(By.textContains("fulfilled"));
                assertNotNull(notif);
                device.pressBack();



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

    }

}






