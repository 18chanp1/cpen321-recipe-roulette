package com.beaker.reciperoulette.Inventory;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beaker.reciperoulette.R;

public class IngredientHolder extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView itemTitleView;
    TextView expiryDateView;
    Button consumeButtonView;

    public IngredientHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.img);
        itemTitleView = itemView.findViewById(R.id.item);
        expiryDateView = itemView.findViewById(R.id.expiry);
        consumeButtonView = itemView.findViewById(R.id.eat_but);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public TextView getItemTitleView() {
        return itemTitleView;
    }

    public TextView getExpiryDateView() {
        return expiryDateView;
    }

    public Button getConsumeButtonView() {
        return consumeButtonView;
    }
}
