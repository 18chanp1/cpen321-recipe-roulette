package com.beaker.reciperoulette.Inventory;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beaker.reciperoulette.IngredientRequest.Ingredient;
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

public class InventoryView extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inv_view);

        loadInventory();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        loadInventory();
    }

    public void loadInventory()
    {
        //get token
        SharedPreferences sharedPref =
                this.getSharedPreferences(getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        String tok = sharedPref.getString("TOKEN", "NOTOKEN");
        String email = sharedPref.getString("EMAIL", "NOEMAIL");

        //Get from web server
        Request req = new Request.Builder()
                .url("https://cpen321-reciperoulette.westus.cloudapp.azure.com/foodInventoryManager")
                .addHeader("userID", email)
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
                    CharSequence s = "Exit the app and try again";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast t = Toast.makeText(InventoryView.this, s, Toast.LENGTH_SHORT);
                            t.show();
                        }
                    });

                }

                else if(response.isSuccessful())
                {
                    String res = response.body().string();

                    Log.d("RecipeFacebook", res);

                    InventoryView.this.runOnUiThread(() -> {

                        IngredientRequestResult[] ingredientsArr= new Gson().fromJson(res, IngredientRequestResult[].class);

                        List<IngredientV2> invList= new ArrayList<IngredientV2>();

                        for(IngredientRequestResult ingredientRequestResult : ingredientsArr)
                        {
                            for(IngredientV2 ingredient : ingredientRequestResult.ingredients)
                            {
                                invList.add(ingredient);
                            }
                        }

                        RecyclerView recyclerView = findViewById(R.id.inv_recycler);
                        recyclerView.setLayoutManager(new LinearLayoutManager(InventoryView.this));
                        recyclerView.setAdapter(new InventoryAdapter(InventoryView.this, InventoryView.this, invList));

                        //if redirected from upload, then scroll to bottom
                        if(getIntent().getBooleanExtra("FROMUPLOAD", false))
                        {
                            recyclerView.scrollToPosition(invList.size() - 1);
                        }

                    });
                }
            }
        });
    }
}