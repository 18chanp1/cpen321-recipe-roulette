package com.beaker.reciperoulette;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import okhttp3.OkHttpClient;
import okhttp3.Request;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class NFMultipleItemsTest {

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
        editor.apply();    }

    @Test
    public void uploadMultipleItemsTimed() throws InterruptedException, ExecutionException, TimeoutException {
        ExecutorService executor = Executors.newFixedThreadPool(100);

        List<Callable<Long>> callableTasks = new ArrayList<>();

        for(int i = 0; i < 100; i++)
        {
            callableTasks.add(this::makeRequest);
        }

        List<Future<Long>> futures = executor.invokeAll(callableTasks);

        for(Future<Long> future: futures)
        {
            assert future.get(10, TimeUnit.SECONDS) < 10000;
        }
    }

    @Test
    public void uploadMultipleItems() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(100);

        List<Callable<Long>> callableTasks = new ArrayList<>();

        for(int i = 0; i < 100; i++)
        {
            callableTasks.add(this::makeRequest);
        }

        List<Future<Long>> futures = executor.invokeAll(callableTasks);

        for(Future<Long> future: futures)
        {
            future.get();
        }
    }


    private long makeRequest()
    {
        long start = Calendar.getInstance().getTimeInMillis();
        Context c = InstrumentationRegistry.getInstrumentation().getTargetContext();

        SharedPreferences sharedPref =
                c.getSharedPreferences(c.getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        String tok = sharedPref.getString("TOKEN", "NOTOKEN");
        String email = sharedPref.getString("EMAIL", "NOEMAIL");

        Request req = new Request.Builder()
                .url("https://cpen321-reciperoulette.westus.cloudapp.azure.com/reviews")
                .addHeader("email", email)
                .addHeader("userToken", tok)
                .build();

        OkHttpClient client = new OkHttpClient();

        for(int i = 0; i < 20; i++)
        {
            try
            {
                client.newCall(req).execute();
            } catch (IOException e){
                Log.d("MULT_ITEM_TEST", "failed to get results");
            }
        }

        return Calendar.getInstance().getTimeInMillis() - start;

    }



}




