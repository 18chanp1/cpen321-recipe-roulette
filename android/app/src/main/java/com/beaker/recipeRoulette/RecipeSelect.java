package com.beaker.recipeRoulette;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecipeSelect extends AppCompatActivity {
    private Button genRecipeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_select);

        genRecipeButton = findViewById(R.id.gen_recipes);

        genRecipeButton.setOnClickListener(view -> {
            CallRecipeBackend();
        });
    }

    private void CallRecipeBackend() {
        OkHttpClient client = new OkHttpClient();

        String url = "https://cpen321-reciperoulette.westus.cloudapp.azure.com/recipes?user=test@ubc.ca";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                ContextCompat.getMainExecutor(RecipeSelect.this).execute(() ->
                {
                    Toast toast = Toast.makeText(RecipeSelect.this, "Failed to retrieve recipes", Toast.LENGTH_LONG);
                    toast.show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("RECIPE", responseBody);
                    String[] recipeNames = parseRecipeNames(responseBody);

                    for (String name : recipeNames) {
                        System.out.println("Recipe Name: " + name);
                    }
                    //TODO Write code to display each recipe name (up to 5 recipes) as a clickable button.
                    // The recipe names are stored in recipeNames. If there is less than 5 recipes (indicated by empty string,
                    // only the buttons with recipes should appear as buttons.

                    runOnUiThread(() -> {
                        LinearLayout recipeButtonLayout = findViewById(R.id.recipeButtonLayout); // Assuming you have a LinearLayout in your activity's layout to hold the buttons
                        recipeButtonLayout.removeAllViews(); // Clear previous buttons if any

                        for (String name : recipeNames) {
                            if (!name.isEmpty()) {
                                Button recipeButton = new Button(RecipeSelect.this);
                                recipeButton.setText(name);
                                recipeButton.setOnClickListener(v -> {
                                    // Handle button click, e.g., open the selected recipe
                                    Toast.makeText(RecipeSelect.this, "Clicked " + name, Toast.LENGTH_SHORT).show();
                                    // You can also launch a new activity to display the selected recipe
                                    // Example: startActivity(new Intent(RecipeSelect.this, RecipeDetailsActivity.class));
                                });
                                recipeButtonLayout.addView(recipeButton); // Add the button to the layout
                            }
                        }
                    });

                } else {
                    Log.d("RECIPE", "Unsuccessful response");
                }
            }
        });
    }

    private String[] parseRecipeNames(String jsonInput) {
        try {
            JSONArray jsonArray = new JSONArray(jsonInput);
            String[] recipeNames = new String[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject recipeObject = jsonArray.getJSONObject(i);
                String name = recipeObject.optString("title"); // Use "title" field
                recipeNames[i] = name;
            }

            return recipeNames;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("RECIPE", "Failed to parse recipe names");
        }
        return null;
    }
}
