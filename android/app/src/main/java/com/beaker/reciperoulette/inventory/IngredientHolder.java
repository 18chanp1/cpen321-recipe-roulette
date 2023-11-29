package com.beaker.reciperoulette.inventory;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beaker.reciperoulette.R;

public class IngredientHolder extends RecyclerView.ViewHolder {
    private final ImageView imageView;
    private final TextView itemTitleView;
    private final TextView expiryDateView;
    private final TextView newItemView;
    private final Button consumeButtonView;



    public IngredientHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.img);
        itemTitleView = itemView.findViewById(R.id.item);
        expiryDateView = itemView.findViewById(R.id.expiry);
        consumeButtonView = itemView.findViewById(R.id.eat_but);
        newItemView = itemView.findViewById(R.id.inv_new);
    }

    public TextView getNewItemView() {
        return newItemView;
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
