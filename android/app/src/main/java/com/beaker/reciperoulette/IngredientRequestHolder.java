package com.beaker.reciperoulette;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class IngredientRequestHolder extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView nameView;
    TextView emailView;
    Button donateIngredientView;

    public IngredientRequestHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.imageview);
        nameView = itemView.findViewById(R.id.name);
        emailView = itemView.findViewById(R.id.email);
        donateIngredientView = itemView.findViewById(R.id.readmore_but);
    }
}
