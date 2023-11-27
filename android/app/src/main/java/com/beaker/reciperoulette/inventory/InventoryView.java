package com.beaker.reciperoulette.inventory;

import android.content.Context;
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
import java.util.Arrays;
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
        String tok = sharedPref.getString(getString(R.string.prf_token), getString(R.string.prf_token_def));
        String email = sharedPref.getString(getString(R.string.prf_eml), getString(R.string.prf_eml_def));

        if(tok.equals(getString(R.string.prf_token_def)) ||
        email.equals(getString(R.string.prf_eml_def)))
            throw new IllegalStateException();

        //Get from web server
        Request req = new Request.Builder()
                .url(getString(R.string.http_inv_url))
                .addHeader(getString(R.string.http_args_userid), email)
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
                        Toast t = Toast.makeText(InventoryView.this, s, Toast.LENGTH_SHORT);
                        t.show();
                    });

                }

                else if(response.isSuccessful())
                {
                    assert response.body() != null;
                    String res = response.body().string();

                    InventoryView.this.runOnUiThread(() -> {

                        IngredientRequestResult[] ingredientsArr= new Gson().fromJson(res, IngredientRequestResult[].class);

                        List<IngredientV2> invList= new ArrayList<>();

                        for(IngredientRequestResult ingredientRequestResult : ingredientsArr)
                        {
                            invList.addAll(Arrays.asList(ingredientRequestResult.ingredients));
                        }

                        RecyclerView recyclerView = findViewById(R.id.inv_recycler);
                        recyclerView.setLayoutManager(new LinearLayoutManager(InventoryView.this));
                        recyclerView.setAdapter(new InventoryAdapter(InventoryView.this, InventoryView.this, invList));

                        //if redirected from upload, then scroll to bottom
                        if(getIntent().getBooleanExtra(getString(R.string.inv_fromupload), false))
                        {
                            recyclerView.scrollToPosition(invList.size() - 1);
                        }

                    });
                }
            }
        });
    }
}