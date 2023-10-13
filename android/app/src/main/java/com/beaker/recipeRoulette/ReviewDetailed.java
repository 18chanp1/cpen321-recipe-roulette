package com.beaker.recipeRoulette;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ReviewDetailed extends AppCompatActivity {
    private TextView titleText;
    private TextView authorText;
    private TextView reviewText;
    private TextView ratingText;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detailed);

        titleText = findViewById(R.id.detailed_title);
        authorText = findViewById(R.id.detailed_author);
        reviewText = findViewById(R.id.detailed_text);
        ratingText = findViewById(R.id.detailed_rating);
        image = findViewById(R.id.detailed_image);

        Intent in = getIntent();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            titleText.setText(in.getParcelableExtra("REVIEW", Review.class).getTitle());
            authorText.setText(in.getParcelableExtra("REVIEW", Review.class).getAuthor());
            reviewText.setText(in.getParcelableExtra("REVIEW", Review.class).getReview());
            ratingText.setText(in.getParcelableExtra("REVIEW", Review.class).getRating());
            Picasso.with(this).load(in.getParcelableExtra("REVIEW", Review.class).getImage()).into(image);
        } else {
            Review r = in.getParcelableExtra("REVIEW");

            titleText.setText(r.getTitle());
            authorText.setText(r.getAuthor());
            reviewText.setText(r.getReview());
            ratingText.setText("Rating: " + String.valueOf(r.getRating()));
            Picasso.with(this).load(r.getImage()).into(image);
        }






    }
}