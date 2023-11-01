package com.beaker.recipeRoulette;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainMenu extends AppCompatActivity {
    private Button recipeReviewButton;
    private Button requestIngredientButton;
    private Button shopTogetherButton;
    private Button testListButton;

    private Button takePhotoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        recipeReviewButton = findViewById(R.id.menu5);
        takePhotoButton = findViewById(R.id.menu6);

        takePhotoButton.setOnClickListener(view -> {
            Intent takePhotoIntent = new Intent(MainMenu.this, com.beaker.recipeRoulette.TakePhoto.class);
            startActivity(takePhotoIntent);
        });

        recipeReviewButton.setOnClickListener(view -> {
            Intent mainMenuIntent = new Intent(MainMenu.this, com.beaker.recipeRoulette.RecipeFacebook.class);
            startActivity(mainMenuIntent);
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

        testListButton = findViewById(R.id.menu6);
        testListButton.setOnClickListener(view ->
        {
            Intent i = new Intent(MainMenu.this, ChatRoomLiveView.class);
            startActivity(i);
        });


    }
}