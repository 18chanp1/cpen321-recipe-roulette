package com.beaker.reciperoulette.RecipeFacebook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beaker.Utilities;
import com.beaker.reciperoulette.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        loadReviews();

        findViewById(R.id.rev_write_but).setOnClickListener((view) -> {
            Intent writeReviewIntent = new Intent(RecipeFacebook.this, WriteReview.class);
            startActivity(writeReviewIntent);
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadReviews();
    }

    private void loadReviews()
    {
        //get token
        SharedPreferences sharedPref =
                this.getSharedPreferences(getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        String tok = sharedPref.getString(getString(R.string.prf_token), getString(R.string.prf_token_def));
        String email = sharedPref.getString(getString(R.string.prf_eml), getString(R.string.prf_eml_def));

        if(tok.equals(getString(R.string.prf_token_def)) ||
                email.equals(getString(R.string.prf_eml_def)))
            throw new IllegalStateException();

        //Get from web server
        Request req = new Request.Builder()
                .url("https://cpen321-reciperoulette.westus.cloudapp.azure.com/reviews")
                .addHeader(getString(R.string.http_args_email), email)
                .addHeader(getString(R.string.http_args_userToken), tok)
                .build();

        OkHttpClient client = new OkHttpClient();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == Utilities.HTTP_511)
                {
                    CharSequence s = getString(R.string.msg_token_expired);
                    runOnUiThread(() -> {
                        Toast t = Toast.makeText(RecipeFacebook.this, s, Toast.LENGTH_SHORT);
                        t.show();
                    });

                }

                else if(response.isSuccessful())
                {
                    assert response.body() != null;
                    String res = response.body().string();

                    RecipeFacebook.this.runOnUiThread(() -> {
                        Review[] userArray = new Gson().fromJson(res, Review[].class);
                        List<Review> reviews = new ArrayList<>();

                        if(userArray != null)
                        {
                            Collections.addAll(reviews, userArray);
                        }

                        RecyclerView recyclerView = findViewById(R.id.rev_recycler);
                        if(recyclerView == null) throw new IllegalStateException();

                        recyclerView.setLayoutManager(new LinearLayoutManager(RecipeFacebook.this));
                        recyclerView.setAdapter(new ReviewAdaptor(RecipeFacebook.this, reviews));

                    });
                }
            }
        });
    }
}