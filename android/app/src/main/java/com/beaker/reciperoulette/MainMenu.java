package com.beaker.reciperoulette;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.beaker.reciperoulette.ChatRoom.EnterChatRoomView;
import com.beaker.reciperoulette.IngredientRequest.IngredientRequestView;
import com.beaker.reciperoulette.RecipeFacebook.RecipeFacebook;

public class MainMenu extends AppCompatActivity {
    /*
    Save this stuff here just to keep track of buttons

    private Button recipeRequestButton;
    private Button recipeReviewButton;
    private Button requestIngredientButton;
    private Button shopTogetherButton;
    private Button flavorProfileButton;

    private Button takePhotoButton;
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button recipeRequestButton = findViewById(R.id.menu1);
        recipeRequestButton.setOnClickListener(view -> {
            Intent recipeSelectIntent = new Intent(MainMenu.this, RecipeSelect.class);
            startActivity(recipeSelectIntent);
        });

        Button takePhotoButton = findViewById(R.id.menu6);
        takePhotoButton.setOnClickListener(view -> {
            Intent takePhotoIntent = new Intent(MainMenu.this, TakePhoto.class);
            startActivity(takePhotoIntent);
        });

        Button recipeReviewButton = findViewById(R.id.menu5);
        recipeReviewButton.setOnClickListener(view -> {
            Intent mainMenuIntent = new Intent(MainMenu.this, RecipeFacebook.class);
            startActivity(mainMenuIntent);
        });

        Button flavorProfileButton = findViewById(R.id.menu2);
        flavorProfileButton.setOnClickListener(view -> {
            Intent fpI = new Intent(MainMenu.this, FlavorProfile.class);
            startActivity(fpI);
        });



        Button requestIngredientButton = findViewById(R.id.menu3);
        requestIngredientButton.setOnClickListener(view -> {
            Intent reqIngrIntent = new Intent(MainMenu.this, IngredientRequestView.class);
            startActivity(reqIngrIntent);
        });

        Button shopTogetherButton = findViewById(R.id.menu4);
        shopTogetherButton.setOnClickListener(view -> {
            Intent cookWithOthersIntent = new Intent(MainMenu.this, EnterChatRoomView.class);
            startActivity(cookWithOthersIntent);
        });

        Button signOutButton = findViewById(R.id.menu7);
        signOutButton.setOnClickListener(view -> {
            Intent signOutIntent = new Intent(MainMenu.this, MainActivity.class);
            signOutIntent.putExtra("SIGNOUT", true);
            startActivity(signOutIntent);
        });

    }
}