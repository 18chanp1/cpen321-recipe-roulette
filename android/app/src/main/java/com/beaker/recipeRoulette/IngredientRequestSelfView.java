package com.beaker.recipeRoulette;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class IngredientRequestSelfView extends AppCompatActivity {

    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_request_self_view);

        //get tokens
        SharedPreferences sharedPref =
                this.getSharedPreferences(getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        String tok = sharedPref.getString("TOKEN", "NOTOKEN");

        //get requests from server
        Request req = new Request.Builder()
                .url("https://cpen321-reciperoulette.westus.cloudapp.azure.com/ingredientrequests/self/")
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
                if(response.isSuccessful())
                {
                    String res = response.body().string();
                    IngredientRequestSelfView.this.runOnUiThread(() -> {

                        //TODO add expiry logic
                        IngredientRequest[] userArray = new Gson().fromJson(res, IngredientRequest[].class);

                        List<IngredientRequest> ingredientRequests = new ArrayList<IngredientRequest>();

                        for(IngredientRequest r : userArray)
                        {
                            ingredientRequests.add(r);
                        }

                        RecyclerView recyclerView = findViewById(R.id.recyclerView);
                        recyclerView.setLayoutManager(new LinearLayoutManager(IngredientRequestSelfView.this));
                        recyclerView.setAdapter(new IngredientRequestSelfAdaptor(getApplicationContext(), ingredientRequests));

                    });


                }
            }
        });
    }
}