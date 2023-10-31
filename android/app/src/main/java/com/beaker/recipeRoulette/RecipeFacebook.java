package com.beaker.recipeRoulette;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecipeFacebook extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_facebook);

        //get token
        SharedPreferences sharedPref =
                this.getSharedPreferences(getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        String tok = sharedPref.getString("TOKEN", "NOTOKEN");
        String email = sharedPref.getString("EMAIL", "NOEMAIL");

        //Get from web server
        Request req = new Request.Builder()
                .url("https://cpen321-reciperoulette.westus.cloudapp.azure.com/reviews")
                .addHeader("email", email)
                .addHeader("userToken", tok)
                .build();

        OkHttpClient client = new OkHttpClient();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == 511)
                {
//                    Intent mainMenuIntent = new Intent(RecipeFacebook.this, MainActivity.class);
//                    mainMenuIntent.putExtra("REFRESHSIGNIN", "RECIPEFACEBOOK");
//
//                    startActivity(mainMenuIntent);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            recreate();
//                        }
//                    });

                    CharSequence s = "Exit the app and try again";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast t = Toast.makeText(RecipeFacebook.this, s, Toast.LENGTH_SHORT);
                            t.show();
                        }
                    });

                }

                else if(response.isSuccessful())
                {
                    String res = response.body().string();

                    RecipeFacebook.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                           Review[] userArray = new Gson().fromJson(res, Review[].class);

                            List<Review> reviews = new ArrayList<Review>();

                            for(Review r : userArray)
                            {
                                reviews.add(r);
                            }

                            RecyclerView recyclerView = findViewById(R.id.recyclerView);
                            recyclerView.setLayoutManager(new LinearLayoutManager(RecipeFacebook.this));
                            recyclerView.setAdapter(new ReviewAdaptor(getApplicationContext(), reviews));

                        }
                    });
                }
            }
        });



    }
}