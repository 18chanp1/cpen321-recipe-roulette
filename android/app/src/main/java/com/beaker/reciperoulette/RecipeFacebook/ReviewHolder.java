package com.beaker.reciperoulette.RecipeFacebook;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beaker.reciperoulette.R;

public class ReviewHolder extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView nameView;
    TextView emailView;
    Button readMoreView;

    public ReviewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.img);
        nameView = itemView.findViewById(R.id.item);
        emailView = itemView.findViewById(R.id.expiry);
        readMoreView = itemView.findViewById(R.id.eat_but);
    }
}
