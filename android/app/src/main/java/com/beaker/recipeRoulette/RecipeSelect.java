package com.beaker.recipeRoulette;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

        String url = "https://cpen321-reciperoulette.westus.cloudapp.azure.com/recipes";

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
                    int[] recipeIds = parseRecipeId(responseBody);

                    for (String name : recipeNames) {
                        System.out.println("Recipe Name: " + name);
                    }
                    for (int id : recipeIds) {
                        System.out.println("Recipe id: " + id);
                    }
                    AllRecipeDetails AllRecipes = new AllRecipeDetails();
                    List<RecipeDetails> recipeList = new ArrayList<>();
                    for (int i = 0; i < recipeNames.length; i++) {
                        RecipeDetails localRecipe = new RecipeDetails();
                        localRecipe.name = recipeNames[i];
                        localRecipe.recipeId = recipeIds[i];

                        recipeList.add(localRecipe);
                    }
                    AllRecipes.recipeList = recipeList;

                    runOnUiThread(() -> {
                        LinearLayout recipeButtonLayout = findViewById(R.id.recipeButtonLayout); // Assuming you have a LinearLayout in your activity's layout to hold the buttons
                        recipeButtonLayout.removeAllViews(); // Clear previous buttons if any

                        for (RecipeDetails localRecipe : AllRecipes.recipeList) {
                            if (!localRecipe.name.isEmpty()) {
                                Button recipeButton = new Button(RecipeSelect.this);
                                recipeButton.setText(localRecipe.name);
                                recipeButton.setOnClickListener(v -> {
                                    Toast.makeText(RecipeSelect.this, "Clicked " + localRecipe.name, Toast.LENGTH_SHORT).show();

                                    SharedPreferences sharedPref =
                                            getSharedPreferences("com.beaker.recipeRoulette.TOKEN", Context.MODE_PRIVATE);
                                    String tok = sharedPref.getString("TOKEN", "NOTOKEN");
                                    String email = sharedPref.getString("EMAIL", "NOEMAIL");

                                    RecipePick chosenRecipe = new RecipePick();
                                    chosenRecipe.userId = email;
                                    chosenRecipe.recipeId = localRecipe.recipeId;

                                    OkHttpClient client = new OkHttpClient();
                                    Gson gson = new Gson();

                                    MediaType JSON = MediaType.get("application/json; charset=utf-8");
                                    RequestBody body = RequestBody.create(gson.toJson(chosenRecipe), JSON);

                                    String acceptUrl = "https://cpen321-reciperoulette.westus.cloudapp.azure.com/recipes";

                                    Request req = new Request.Builder()
                                            .url(acceptUrl)
                                            .addHeader("userToken", tok)
                                            .addHeader("email", email)
                                            .post(body)
                                            .build();

                                    try {
                                        client.newCall(req).enqueue(new Callback() {
                                            @Override
                                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                                System.err.println("Request failed with code: " + e);
                                            }

                                            @Override
                                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                                String responseBody = response.body().string();
                                                System.out.println("Response: " + responseBody);
                                                Context context = RecipeSelect.this;
                                                Intent intent = new Intent(context, RecipeDisplay.class);

                                                // Pass data as extras to the RecipeActivity
                                                intent.putExtra("recipeName", localRecipe.name);
                                                intent.putExtra("responseBody", responseBody);

                                                // Start the RecipeActivity
                                                context.startActivity(intent);
                                            }
                                        });
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
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

    private int[] parseRecipeId(String jsonInput) {
        try {
            JSONArray jsonArray = new JSONArray(jsonInput);
            int[] recipeId = new int[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject recipeObject = jsonArray.getJSONObject(i);
                int id = recipeObject.getInt("id"); // Use "title" field
                recipeId[i] = id;
            }

            return recipeId;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("RECIPE", "Failed to parse recipe ids");
        }
        return null;
    }
}

class RecipePick {
    String userId;
    int recipeId;
}

class RecipeDetails {
    String name;
    int recipeId;
}

class AllRecipeDetails {
    List<RecipeDetails> recipeList;
}