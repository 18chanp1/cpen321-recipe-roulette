package com.beaker.reciperoulette;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.beaker.reciperoulette.reviews.RecipeFacebook;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class RecipeDisplay extends AppCompatActivity {

    /**
     * Save this here for to keep track of buttons
     * private Button recipeCompleteButton;
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_display);

        Button recipeCompleteButton = findViewById(R.id.recipe_complete);

        recipeCompleteButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, RecipeFacebook.class);

            this.startActivity(intent);
        });

        Intent intent = getIntent();
        // Retrieve the extras
        String recipeName = intent.getStringExtra("recipeName");
        String responseBody = intent.getStringExtra("responseBody");

        String summary = parseRecipeSummary(responseBody);
        String[] ingredients = parseIngredients(responseBody);
        String[] steps = parseSteps(responseBody);

        summary = convertToPlainText(Objects.requireNonNull(summary));

        displayRecipe(recipeName, summary, ingredients, steps);
    }
    public void displayRecipe(String name, String summary, String[] ingredients, String[] steps) {
        TextView nameTextView = findViewById(R.id.recipe_name);
        nameTextView.setText(name);

        TextView summaryTextView = findViewById(R.id.recipe_summary);
        summaryTextView.setText(summary);

        TextView ingredientsTextView = findViewById(R.id.recipe_ingredients);
        if (ingredients != null && ingredients.length > 0) {
            StringBuilder ingredientsBuilder = new StringBuilder("Ingredients:\n");
            for (String ingredient : ingredients) {
                ingredientsBuilder.append("• ").append(ingredient).append("\n");
            }
            ingredientsTextView.setText(ingredientsBuilder.toString());
        } else {
            ingredientsTextView.setText(""); // Clear the TextView if there are no ingredients
        }

        TextView stepsTextView = findViewById(R.id.recipe_steps);
        if (steps != null && steps.length > 0) {
            StringBuilder stepsBuilder = new StringBuilder("Steps:\n• ");
            for (String step : steps) {
                stepsBuilder.append(step).append("\n");
            }
            stepsTextView.setText(stepsBuilder.toString());
        } else {
            stepsTextView.setText(""); // Clear the TextView if there are no steps
        }
    }

    private String parseRecipeSummary(String jsonInput) {
        try {
            JSONObject recipeObject = new JSONObject(jsonInput);
            return recipeObject.optString("recipeSummary");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("RECIPE", "Failed to parse recipe instructions");
        }
        return null;
    }

    private String[] parseIngredients(String jsonInput) {
        try {
            JSONObject recipeObject = new JSONObject(jsonInput);
            JSONArray instructionsArray = recipeObject.getJSONArray("instructions");
            Set<String> uniqueIngredients = new HashSet<>();

            for (int i = 0; i < instructionsArray.length(); i++) {
                JSONArray ingredientsArray = instructionsArray.getJSONObject(i)
                        .getJSONArray("steps").getJSONObject(2).getJSONArray("ingredients");

                for (int j = 0; j < ingredientsArray.length(); j++) {
                    JSONObject ingredientObject = ingredientsArray.getJSONObject(j);
                    String name = ingredientObject.optString("name");
                    uniqueIngredients.add(name);
                }
            }

            String[] ingredients = new String[uniqueIngredients.size()];
            return uniqueIngredients.toArray(ingredients);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("RECIPE", "Failed to parse recipe ingredients");
        }
        return null;
    }

    private String[] parseSteps(String jsonInput) {
        try {
            JSONObject recipeObject = new JSONObject(jsonInput);
            JSONArray instructionsArray = recipeObject.getJSONArray("instructions");
            String[] steps = new String[instructionsArray.length()];

            for (int i = 0; i < instructionsArray.length(); i++) {
                JSONArray stepsArray = instructionsArray.getJSONObject(i).getJSONArray("steps");
                StringBuilder stepBuilder = new StringBuilder();

                for (int j = 0; j < stepsArray.length(); j++) {
                    JSONObject stepObject = stepsArray.getJSONObject(j);
                    String step = stepObject.optString("step");
                    stepBuilder.append(step);
                    if (j < stepsArray.length() - 1) {
                        stepBuilder.append("\n• "); // Add a newline between steps
                    }
                }

                steps[i] = stepBuilder.toString();
            }

            return steps;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("RECIPE", "Failed to parse recipe instructions");
        }
        return null;
    }

    public static String convertToPlainText(String htmlString) {
        // Use regular expression to remove HTML tags
        String plainText = htmlString.replaceAll("<.*?>", "");

        // Replace HTML entities with their plain text equivalents
        plainText = plainText.replaceAll("&amp;", "&");
        plainText = plainText.replaceAll("&lt;", "<");
        plainText = plainText.replaceAll("&gt;", ">");
        plainText = plainText.replaceAll("&quot;", "\"");
        plainText = plainText.replaceAll("&apos;", "'");

        return plainText;
    }
}
