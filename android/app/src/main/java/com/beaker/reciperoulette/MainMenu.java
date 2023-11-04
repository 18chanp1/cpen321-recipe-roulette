package com.beaker.reciperoulette;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainMenu extends AppCompatActivity {

    private Button recipeRequestButton;
    private Button recipeReviewButton;
    private Button requestIngredientButton;
    private Button shopTogetherButton;
    private Button flavorProfileButton;

    private Button takePhotoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        recipeRequestButton = findViewById(R.id.menu1);

        recipeRequestButton.setOnClickListener(view -> {
            Intent recipeSelectIntent = new Intent(MainMenu.this, RecipeSelect.class);
            startActivity(recipeSelectIntent);
        });

        recipeReviewButton = findViewById(R.id.menu5);
        takePhotoButton = findViewById(R.id.menu6);

        takePhotoButton.setOnClickListener(view -> {
            Intent takePhotoIntent = new Intent(MainMenu.this, TakePhoto.class);
            startActivity(takePhotoIntent);
        });

        recipeReviewButton.setOnClickListener(view -> {
            Intent mainMenuIntent = new Intent(MainMenu.this, RecipeFacebook.class);
            startActivity(mainMenuIntent);
        });

        flavorProfileButton = findViewById(R.id.menu2);
        flavorProfileButton.setOnClickListener(view -> {
            Intent fpI = new Intent(MainMenu.this, FlavorProfile.class);
            startActivity(fpI);
        });



        requestIngredientButton = findViewById(R.id.menu3);
        requestIngredientButton.setOnClickListener(view -> {
            Intent reqIngrIntent = new Intent(MainMenu.this, IngredientRequestView.class);
            startActivity(reqIngrIntent);
        });

        shopTogetherButton = findViewById(R.id.menu4);
        shopTogetherButton.setOnClickListener(view -> {
            Intent cookWithOthersIntent = new Intent(MainMenu.this, EnterChatRoomView.class);
            startActivity(cookWithOthersIntent);
        });

    }
}