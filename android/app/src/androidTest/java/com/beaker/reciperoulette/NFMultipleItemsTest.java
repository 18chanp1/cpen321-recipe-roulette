package com.beaker.reciperoulette;


import android.content.Context;
import android.content.SharedPreferences;

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
import okhttp3.Response;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class NFMultipleItemsTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void waitForMenu() throws InterruptedException {
        Thread.sleep(500);
    }

    @Test
    public void uploadMultipleItemsTimed() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        ExecutorService executor = Executors.newFixedThreadPool(100);

        List<Callable<Long>> callableTasks = new ArrayList<>();

        for(int i = 0; i < 100; i++)
        {
            callableTasks.add(this::makeRequest);
        }

        List<Future<Long>> futures = executor.invokeAll(callableTasks);

        for(Future<Long> future: futures)
        {
            assert future.get(6, TimeUnit.SECONDS) < 6000;
        }
    }

    @Test
    public void uploadMultipleItems() throws IOException, InterruptedException, ExecutionException, TimeoutException {
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
            try (Response response = client.newCall(req).execute())
            {
            } catch (IOException e) {

            }
        }

        long diff = Calendar.getInstance().getTimeInMillis() - start;
        return diff;

    }



}




