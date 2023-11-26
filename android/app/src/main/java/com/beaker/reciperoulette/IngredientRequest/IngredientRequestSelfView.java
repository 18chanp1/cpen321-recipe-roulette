package com.beaker.reciperoulette.IngredientRequest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.beaker.Utilities;
import com.beaker.reciperoulette.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//mark for deletion
public class IngredientRequestSelfView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_request_self_view);

        //get tokens
        SharedPreferences sharedPref =
                this.getSharedPreferences(getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        String tok = sharedPref.getString("TOKEN", "NOTOKEN");
        String email = sharedPref.getString("EMAIL", "NOEMAIL");

        //get requests from server
        Request req = new Request.Builder()
                .url("https://cpen321-reciperoulette.westus.cloudapp.azure.com/ingredientrequests/self/")
                .addHeader("userToken", tok)
                .addHeader("email", email)
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

                    Toast t = Toast.makeText(IngredientRequestSelfView.this, s, Toast.LENGTH_SHORT);
                    t.show();

                }
                if(response.isSuccessful())
                {
                    String res = response.body().string();
                    IngredientRequestSelfView.this.runOnUiThread(() -> {

                        //TODO add expiry logic
                        IngredientRequestSelf[] userArray = new Gson().fromJson(res, IngredientRequestSelf[].class);

                        List<IngredientRequestSelf> ingredientRequests = new ArrayList<IngredientRequestSelf>();

                        for(IngredientRequestSelf r : userArray)
                        {
                            ingredientRequests.add(r);
                        }

                        RecyclerView recyclerView = findViewById(R.id.rq_recycler);
                        recyclerView.setLayoutManager(new LinearLayoutManager(IngredientRequestSelfView.this));
                        recyclerView.setAdapter(new IngredientRequestSelfAdaptor(getApplicationContext(), ingredientRequests));

                    });


                }
            }
        });
    }
}