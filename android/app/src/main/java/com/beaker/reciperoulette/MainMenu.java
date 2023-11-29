package com.beaker.reciperoulette;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.beaker.reciperoulette.chatroom.EnterChatRoomView;
import com.beaker.reciperoulette.requests.IngredientRequestView;
import com.beaker.reciperoulette.inventory.InventoryView;
import com.beaker.reciperoulette.reviews.RecipeFacebook;

public class MainMenu extends AppCompatActivity {
    /*
    Save this stuff here just to keep track of buttons
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button recipeRequestButton = findViewById(R.id.menu1);
        assert recipeRequestButton != null;
        recipeRequestButton.setOnClickListener(view -> {
            Intent recipeSelectIntent = new Intent(MainMenu.this, RecipeSelect.class);
            startActivity(recipeSelectIntent);
        });

        Button flavorProfileButton = findViewById(R.id.menu2);
        assert flavorProfileButton != null;
        flavorProfileButton.setOnClickListener(view -> {
            Intent fpI = new Intent(MainMenu.this, FlavorProfile.class);
            startActivity(fpI);
        });

        Button requestIngredientButton = findViewById(R.id.menu3);
        assert requestIngredientButton != null;
        requestIngredientButton.setOnClickListener(view -> {
            Intent reqIngrIntent = new Intent(MainMenu.this, IngredientRequestView.class);
            startActivity(reqIngrIntent);
        });

        Button shopTogetherButton = findViewById(R.id.menu4);
        assert shopTogetherButton != null;
        shopTogetherButton.setOnClickListener(view -> {
            Intent cookWithOthersIntent = new Intent(MainMenu.this, EnterChatRoomView.class);
            startActivity(cookWithOthersIntent);
        });

        Button recipeReviewButton = findViewById(R.id.menu5);
        assert recipeReviewButton != null;
        recipeReviewButton.setOnClickListener(view -> {
            Intent mainMenuIntent = new Intent(MainMenu.this, RecipeFacebook.class);
            startActivity(mainMenuIntent);
        });

        Button uploadGroceriesButton = findViewById(R.id.menu6);
        assert uploadGroceriesButton != null;
        uploadGroceriesButton.setOnClickListener(view -> {
            Intent uploadGroceriesIntent = new Intent(MainMenu.this, UploadGroceries.class);
            startActivity(uploadGroceriesIntent);
        });

        Button inventoryViewButton = findViewById(R.id.menu7);
        assert inventoryViewButton != null;
        inventoryViewButton.setOnClickListener(view -> {
            Intent inventoryIntent = new Intent(MainMenu.this, InventoryView.class);
            startActivity(inventoryIntent);
        });

        Button signOutButton = findViewById(R.id.menu8);
        assert signOutButton != null;
        signOutButton.setOnClickListener(view -> {
            Intent signOutIntent = new Intent(MainMenu.this, MainActivity.class);
            signOutIntent.putExtra(getString(R.string.menu_signout), true);
            startActivity(signOutIntent);
        });
    }
}