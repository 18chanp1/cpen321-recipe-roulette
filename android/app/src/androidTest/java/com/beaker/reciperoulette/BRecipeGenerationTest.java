package com.beaker.reciperoulette;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.fail;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class BRecipeGenerationTest {

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
    public void checkGenerateRecipesButtonVisible() {
        gotoGenerateRecipe();
        onView(ViewMatchers.withId(R.id.gen_recipes))
                .check(matches(isDisplayed()));
    }

    @Test
    public void clickGenerateRecipesButtonAndCheckRecipeList() {
        onView(withText("Recipe Engine")).perform(click());

        // Generate Recipes
        onView(ViewMatchers.withId(R.id.gen_recipes))
                .perform(click());

        //independently obtain the recipe
        Context c = InstrumentationRegistry.getInstrumentation().getTargetContext();
        OkHttpClient client = new OkHttpClient();
        SharedPreferences sharedPref =
                c.getSharedPreferences("com.beaker.reciperoulette.TOKEN", Context.MODE_PRIVATE);
        String tok = sharedPref.getString("TOKEN", "NOTOKEN");
        String email = sharedPref.getString("EMAIL", "NOEMAIL");

        String url = "https://cpen321-reciperoulette.westus.cloudapp.azure.com/recipes?email="+ email;

        Request req = new Request.Builder()
                .url(url)
                .addHeader("userToken", tok)
                .addHeader("email", email)
                .get()
                .build();

        try (Response response = client.newCall(req).execute())
        {
            if(response.isSuccessful())
            {
                String responseBody = Objects.requireNonNull(response.body()).string();
                JSONArray jsonArray = new JSONArray(responseBody);
                String[] recipeNames = new String[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject recipeObject = jsonArray.getJSONObject(i);
                    String name = recipeObject.optString("title"); // Use "title" field
                    recipeNames[i] = name;
                }

                for (String recipeName : recipeNames) {
                    onView(withText(recipeName))
                            .check(matches(isDisplayed()));

                    onView(withText(recipeName)).perform(click());
                    checkDetailedView(recipeName);
                    pressBack();
                }

            }
            else if(response.code() == 500)
            {
                //do nothing
                Log.d("TEST_RECIPEGEN", "500 ERROR, OUT OF QUOTA");
            }
            else
            {
                fail("Entry does not match");
            }
        } catch (IOException | JSONException | InterruptedException e)
        {
            fail("Unable to parse inputs from server");
        }
    }


    @Test
    public void checkGenerateRecipesButtonAndVerifyResponseTime() {
        gotoGenerateRecipe();
        long startTime = System.currentTimeMillis();

        // Generate Recipes
        onView(ViewMatchers.withId(R.id.gen_recipes))
                .perform(click());

        // Check Recipe List
        onView(ViewMatchers.withId(R.id.recipeButtonLayout))
                .check(matches(isDisplayed()));

        long endTime = System.currentTimeMillis();

        // Calculate the response time and verify <6 second response time
        long responseTime = endTime - startTime;
        assert(responseTime < 6000);
    }

    private void gotoGenerateRecipe()
    {
        onView(withText("Recipe Engine")).perform(click());
    }

    private void checkDetailedView(String recipeName) throws InterruptedException {
        Thread.sleep(500);
        onView(withText(recipeName)).check(matches(isDisplayed()));
        onView(withId(R.id.recipe_name)).check(matches(isDisplayed()));
        onView(withId(R.id.recipe_summary)).check(matches(isDisplayed()));
        onView(withId(R.id.recipe_ingredients)).check(matches(isDisplayed()));
        onView(withId(R.id.recipe_steps)).check(matches(isDisplayed()));

        onView(withId(R.id.recipe_complete)).perform(click());

//        Context c = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        RecyclerView r = (RecyclerView) ((Activity) c).findViewById(R.id.rev_recycler);
//
//        int last = r.getAdapter().getItemCount() - 1;
//
//        onView(withId(R.id.rev_recycler))
//                .perform(scrollToPosition(last));
//        Matcher<View> currentMatch = new RecyclerViewMatcher(R.id.rev_recycler).atPosition(last);
//
//        onView(currentMatch)
//                .check(matches(hasDescendant(withText(recipeName))));


        pressBack();

    }

}
