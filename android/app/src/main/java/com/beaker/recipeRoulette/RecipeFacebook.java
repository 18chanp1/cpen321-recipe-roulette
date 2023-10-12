package com.beaker.recipeRoulette;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RecipeFacebook extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_facebook);

        List<Review> items = new ArrayList<Review>();
        items.add(new Review(1, 3, "Bobby Jones", "Cooking your mum", "It sucks", Calendar.getInstance(), 69));
        items.add(new Review(1, 3, "Bobby Jones", "Cooking your mum", "It sucks", Calendar.getInstance(), 69));
        items.add(new Review(1, 3, "Bobby Jones", "Cooking your mum", "It sucks", Calendar.getInstance(), 69));
        items.add(new Review(1, 3, "Bobby Jones", "Cooking your mum", "It sucks", Calendar.getInstance(), 69));
        items.add(new Review(1, 3, "Bobby Jones", "Cooking your mum", "It sucks", Calendar.getInstance(), 69));
        items.add(new Review(1, 3, "Bobby Jones", "Cooking your mum", "It sucks", Calendar.getInstance(), 69));
        items.add(new Review(1, 3, "Bobby Jones", "Cooking your mum", "It sucks", Calendar.getInstance(), 69));
        items.add(new Review(1, 3, "Bobby Jones", "Cooking your mum", "It sucks", Calendar.getInstance(), 69));
        items.add(new Review(1, 3, "Bobby Jones", "Cooking your mum", "It sucks", Calendar.getInstance(), 69));




        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ReviewAdaptor(getApplicationContext(), items));
    }
}