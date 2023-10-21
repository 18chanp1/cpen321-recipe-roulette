package com.beaker.recipeRoulette;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class IngredientRequestView extends AppCompatActivity {
    private Button requestIngredientButton;
    private Button viewRequestButton;
    private EditText ingredientRequestText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredient_req_view);

        //setup buttons
        requestIngredientButton = findViewById(R.id.req_ingred_but);
        viewRequestButton = findViewById(R.id.view_req_but);
        ingredientRequestText = findViewById(R.id.ingredient_entry);


        //get tokens
        SharedPreferences sharedPref =
                this.getSharedPreferences(getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        String tok = sharedPref.getString("TOKEN", "NOTOKEN");

        //get requests from server
        Request req = new Request.Builder()
                .url("https://cpen321-reciperoulette.westus.cloudapp.azure.com/ingredientrequests")
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

                    IngredientRequestView.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //TODO add expiry logic
                            IngredientRequest[] userArray = new Gson().fromJson(res, IngredientRequest[].class);

                            List<IngredientRequest> ingredientRequests = new ArrayList<IngredientRequest>();

                            for(IngredientRequest r : userArray)
                            {
                                ingredientRequests.add(r);
                            }

                            RecyclerView recyclerView = findViewById(R.id.recyclerView);
                            recyclerView.setLayoutManager(new LinearLayoutManager(IngredientRequestView.this));
                            recyclerView.setAdapter(new IngredientRequestAdaptor(getApplicationContext(), ingredientRequests));

                        }
                    });


                }
            }
        });

        //setup the listener to submit requests

        requestIngredientButton.setOnClickListener(view -> {
            submitRequestHandler();
        });

        viewRequestButton.setOnClickListener(view ->
        {
            Intent selfReqI = new Intent(IngredientRequestView.this, IngredientRequestSelfView.class);
            startActivity(selfReqI);
        });


//        ingredientRequestText.setOnKeyListener((view, i, keyEvent) -> {
//            if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
//                submitRequestHandler();
//            }
//            return super;
//        });

    }

    private void submitRequestHandler()
    {
        SharedPreferences sharedPref =
                this.getSharedPreferences(getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        String tok = sharedPref.getString("TOKEN", "NOTOKEN");

        OkHttpClient client = new OkHttpClient();

        String foodReq = String.valueOf(ingredientRequestText.getText());
        Gson gson = new Gson();
        String json = gson.toJson(new IngredientRequestTicket(tok, foodReq));


        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(json, JSON);

        Request postNewReq = new Request.Builder()
                .url("https://cpen321-reciperoulette.westus.cloudapp.azure.com/ingredientrequests/new")
                .addHeader("userToken", tok)
                .addHeader("requestItem", foodReq)
                .post(body)
                .build();

        client.newCall(postNewReq).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();

                IngredientRequestView.this.runOnUiThread(() -> {
                    Toast toast = Toast.makeText(IngredientRequestView.this, "Request failed", Toast.LENGTH_LONG);
                    toast.show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                IngredientRequestView.this.runOnUiThread(() -> {
                    if(response.isSuccessful()) {
                        Log.d("test", "call ok");
                        Toast toast = Toast.makeText(IngredientRequestView.this, "Request made", Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            Intent i = new Intent(this, MainMenu.class);
            startActivity(i);
            return true;
        }
        if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
            submitRequestHandler();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

}