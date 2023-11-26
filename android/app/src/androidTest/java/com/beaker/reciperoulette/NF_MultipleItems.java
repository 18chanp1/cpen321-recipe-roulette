package com.beaker.reciperoulette;


import static androidx.test.espresso.action.ViewActions.click;
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

import com.beaker.reciperoulette.IngredientRequest.Ingredient;
import com.beaker.reciperoulette.IngredientRequest.IngredientsRequest;
import com.google.gson.Gson;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class NF_MultipleItems {

    @Rule
    public ActivityScenarioRule<MainMenu> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainMenu.class);


    @Test
    public void uploadMultipleItems() throws IOException {
        Context c = InstrumentationRegistry.getInstrumentation().getTargetContext();

        SharedPreferences sharedPref =
                c.getSharedPreferences(c.getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        String tok = sharedPref.getString("TOKEN", "NOTOKEN");
        String email = sharedPref.getString("EMAIL", "NOEMAIL");

        //create list of multiple items
        Gson gson = new Gson();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        IngredientsRequest testRequest = new IngredientsRequest(email);
        testRequest.ingredients = new ArrayList<Ingredient>();




        testRequest.ingredients.add(new Ingredient("Turkey", 1, new long[]{Calendar.getInstance().getTimeInMillis()}));
        testRequest.ingredients.add(new Ingredient("Chicken", 1, new long[]{Calendar.getInstance().getTimeInMillis()}));
        testRequest.ingredients.add(new Ingredient("Beef", 1, new long[]{Calendar.getInstance().getTimeInMillis()}));
        testRequest.ingredients.add(new Ingredient("Human", 1, new long[]{Calendar.getInstance().getTimeInMillis()}));
        testRequest.ingredients.add(new Ingredient("Salmon", 1, new long[]{Calendar.getInstance().getTimeInMillis()}));
        testRequest.ingredients.add(new Ingredient("Grapes", 1, new long[]{Calendar.getInstance().getTimeInMillis()}));
        testRequest.ingredients.add(new Ingredient("Beans", 1, new long[]{Calendar.getInstance().getTimeInMillis()}));
        testRequest.ingredients.add(new Ingredient("Celery", 1, new long[]{Calendar.getInstance().getTimeInMillis()}));
        testRequest.ingredients.add(new Ingredient("Carrot", 1, new long[]{Calendar.getInstance().getTimeInMillis()}));
        testRequest.ingredients.add(new Ingredient("Sugar", 1, new long[]{Calendar.getInstance().getTimeInMillis()}));
        testRequest.ingredients.add(new Ingredient("Water", 1, new long[]{Calendar.getInstance().getTimeInMillis()}));
        testRequest.ingredients.add(new Ingredient("BBQ Sauce", 1, new long[]{Calendar.getInstance().getTimeInMillis()}));
        testRequest.ingredients.add(new Ingredient("Eggs", 1, new long[]{Calendar.getInstance().getTimeInMillis()}));
        testRequest.ingredients.add(new Ingredient("Jam", 1, new long[]{Calendar.getInstance().getTimeInMillis()}));
        testRequest.ingredients.add(new Ingredient("Pork", 1, new long[]{Calendar.getInstance().getTimeInMillis()}));
        testRequest.ingredients.add(new Ingredient("Chocolate", 1, new long[]{Calendar.getInstance().getTimeInMillis()}));
        testRequest.ingredients.add(new Ingredient("Banana", 1, new long[]{Calendar.getInstance().getTimeInMillis()}));
        testRequest.ingredients.add(new Ingredient("Bread", 1, new long[]{Calendar.getInstance().getTimeInMillis()}));
        testRequest.ingredients.add(new Ingredient("Banana bread", 1, new long[]{Calendar.getInstance().getTimeInMillis()}));
        testRequest.ingredients.add(new Ingredient("Cabbage", 1, new long[]{Calendar.getInstance().getTimeInMillis()}));

        RequestBody body = RequestBody.create(gson.toJson(testRequest), JSON);

        Request req = new Request.Builder()
                .url("https://cpen321-reciperoulette.westus.cloudapp.azure.com/foodInventoryManager/upload")
                .addHeader("email", email)
                .addHeader("userToken", tok)
                .post(body)
                .build();

        OkHttpClient client = new OkHttpClient();

        try(Response res = client.newCall(req).execute())
        {
            if(res.isSuccessful())
            {
                String result = res.body().string();

            }
            else {
                fail("Server Error");
            }
        }

    }



}




