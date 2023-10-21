package com.beaker.recipeRoulette;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainMenu extends AppCompatActivity {
    private Button recipeReviewButton;
    private Button requestIngredientButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        recipeReviewButton = findViewById(R.id.menu5);

        recipeReviewButton.setOnClickListener(view -> {
            Intent mainMenuIntent = new Intent(MainMenu.this, com.beaker.recipeRoulette.RecipeFacebook.class);
            startActivity(mainMenuIntent);
        });

        requestIngredientButton = findViewById(R.id.menu3);
        requestIngredientButton.setOnClickListener(view -> {
            Intent reqIngrIntent = new Intent(MainMenu.this, IngredientRequestView.class);
            startActivity(reqIngrIntent);
        });
    }
}