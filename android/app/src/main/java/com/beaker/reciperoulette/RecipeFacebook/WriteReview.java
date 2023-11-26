package com.beaker.reciperoulette.RecipeFacebook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.beaker.reciperoulette.R;

public class WriteReview extends AppCompatActivity {
    private TextView titleText;
    private TextView reviewText;
    private TextView submitButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rev_write_review);

        titleText = findViewById(R.id.rev_writereview_title);
        reviewText = findViewById(R.id.rev_writereview_body);
        submitButton = findViewById(R.id.rev_writereview_submit);

        

    }
}