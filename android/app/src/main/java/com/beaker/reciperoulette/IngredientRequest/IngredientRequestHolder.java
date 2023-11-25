package com.beaker.reciperoulette.IngredientRequest;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beaker.reciperoulette.R;

public class IngredientRequestHolder extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView nameView;
    TextView emailView;
    Button donateIngredientView;

    public IngredientRequestHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.imageview);
        nameView = itemView.findViewById(R.id.rq_entry_name);
        emailView = itemView.findViewById(R.id.rq_entry_eml);
        donateIngredientView = itemView.findViewById(R.id.rq_donate_but);
    }
}
